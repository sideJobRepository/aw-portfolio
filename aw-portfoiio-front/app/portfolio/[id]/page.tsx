"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter, useParams, useSearchParams } from "next/navigation";
import DynamicFormField from "@/components/DynamicFormField";
import { useRecoilValue } from "recoil";
import { userState } from "@/store/user";
import { PortfolioService } from "@/services/portfolios.service";
import { useRequest } from "@/hooks/useRequest";
import { QuestionService } from "@/services/question.service";
import { SubmissionService } from "@/services/submission.service";

interface Question {
  id: string;
  step: number;
  title: string;
  description?: string;
  thumbnail?: string;
  questionType: string;
  options?: string;
  minLength: number;
  maxLength?: number;
  requireMinLength?: boolean;
  order: number;
  isRequired: boolean;
}

interface Portfolio {
  id: string;
  title: string;
  description: string;
  slug: string;
}

interface FormData {
  [key: string]: any;
}

export default function PortfolioForm() {
  const router = useRouter();
  const params = useParams();
  const searchParams = useSearchParams();

  //hooks
  const { request } = useRequest();

  //id 방식 교체
  const id = params.id as string;
  const submissionId = searchParams.get("submissionId");
  //상세보기 제어
  const isDetailMode = searchParams.get("detail") === "true";

  //로그인 상태
  const currentUser = useRecoilValue(userState);
  //썸네일
  const [showPreview, setShowPreview] = useState(false);
  const previewRef = useRef<HTMLDivElement>(null);

  //자동 저장
  const autoSaveTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const [shouldAutoSave, setShouldAutoSave] = useState(false);
  const startAutoSave = () => {
    if (autoSaveTimeoutRef.current) {
      clearTimeout(autoSaveTimeoutRef.current);
    }

    autoSaveTimeoutRef.current = setTimeout(() => {
      setShouldAutoSave(true); // 트리거
      startAutoSave(); // 다음 타이머 예약
    }, 180000);
  };

  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [currentStep, setCurrentStep] = useState(-1); // -1 = 로딩, 0+ = 질문 단계
  const [formData, setFormData] = useState<FormData>({});
  const [errors, setErrors] = useState<FormData>({});

  // 객실
  const [rooms, setRooms] = useState<
    Array<{
      id: string;
      name: string;
      desc: string;
      type: string;
      priceLow: string; // 비수기
      priceMid: string; // 준성수기
      priceHigh: string; // 성수기
    }>
  >([
    {
      id: "room-1",
      name: "",
      desc: "",
      type: "",
      priceLow: "",
      priceMid: "",
      priceHigh: "",
    },
  ]);

  // 스페셜
  const [specials, setSpecials] = useState<
    Array<{ id: string; name: string; desc: string }>
  >([{ id: "special-1", name: "", desc: "" }]);

  //환불
  const [refunds, setRefunds] = useState<
    Array<{ id: string; day: string; percent: string }>
  >([{ id: "refund-1", day: "", percent: "" }]);

  const handleAddRefund = () => {
    setRefunds((prev) => [
      ...prev,
      { id: `refund-${Date.now()}`, day: "", percent: "" },
    ]);
  };

  const handleRemoveRefund = (id: string) => {
    setRefunds((prev) => prev.filter((r) => r.id !== id));
  };

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [existingSubmissionId, setExistingSubmissionId] = useState<
    string | null
  >(null);

  const maxStep =
    questions.length > 0 ? Math.max(...questions.map((q) => q.step)) : 1;
  const minStep =
    questions.length > 0 ? Math.min(...questions.map((q) => q.step)) : 0;

  const fileMapRef = useRef<Record<string, File>>({});

  //파일 전송시 수정
  const extractSubmitData = () => {
    const optionFiles: any[] = [];
    const cleanedFormData: any = {};

    Object.entries(fileMapRef.current).forEach(([questionId, file]) => {
      const question = questions.find(
        (q) => String(q.id) === String(questionId),
      );
      if (!question) return;

      optionFiles.push({
        optionsId: question.id,
        questionStep: question.step,
        questionOrder: question.order,
        files: [file],
      });

      cleanedFormData[questionId] = null;
    });

    Object.entries(formData).forEach(([k, v]) => {
      if (fileMapRef.current[k]) return;
      cleanedFormData[k] = v;
    });

    return {
      response: {
        ...cleanedFormData,
        rooms,
        specials,
        refunds,
      },
      optionFiles,
    };
  };

  useEffect(() => {
    if (questions.length > 0 && currentStep === -1) {
      setCurrentStep(minStep);
    }
  }, [questions, minStep, currentStep]);

  // 1. 유저 정보 확인
  useEffect(() => {
    const loginGb = localStorage.getItem("login");

    //로그인 정보 확인
    if (loginGb) {
      if (currentUser) {
        fetchPortfolioAndQuestions();
      }
    } else {
      router.push("/");
    }
  }, [id, currentUser]);

  useEffect(() => {
    if (currentUser && portfolio?.id && submissionId) {
      checkExistingSubmission();
    }

    if (!isDetailMode) {
      startAutoSave();
    }
  }, [currentUser, portfolio?.id]);

  //자동저장 트리거
  useEffect(() => {
    if (!shouldAutoSave) return;

    const save = async () => {
      await handleSaveDraft();
      setShouldAutoSave(false);
    };

    save();
  }, [shouldAutoSave]);

  // Enter로 다음
  useEffect(() => {
    const handleKeyPress = (event: KeyboardEvent) => {
      if (event.key === "Enter" && !event.shiftKey && !submitting) {
        const target = event.target as HTMLElement;
        if (target.tagName === "TEXTAREA" && !event.ctrlKey) return;
        event.preventDefault();
        if (currentStep < maxStep) {
          handleNext();
        } else {
          handleSubmit();
        }
      }
    };
    document.addEventListener("keydown", handleKeyPress);
    return () => document.removeEventListener("keydown", handleKeyPress);
  }, [currentStep, maxStep, submitting]);

  const checkExistingSubmission = async () => {
    if (!submissionId) return;
    try {
      await request(
        () => SubmissionService.get(submissionId),
        (res) => {
          const data = res.data;

          if (data) {
            const parsedResponses = JSON.parse(data.submissionJson);

            setExistingSubmissionId(data.submissionId);
            setFormData(parsedResponses);

            // rooms 복원
            const savedRooms = parsedResponses?.rooms;
            if (Array.isArray(savedRooms) && savedRooms.length > 0) {
              setRooms(
                savedRooms.map((r: any, idx: number) => ({
                  id: r.id ? String(r.id) : `room-${idx + 1}`,
                  name: r.name || "",
                  desc: r.desc || "",
                  type: r.type || "",
                  priceLow: r.priceLow || "",
                  priceMid: r.priceMid || "",
                  priceHigh: r.priceHigh || "",
                })),
              );
            } else {
              setRooms([
                {
                  id: "room-1",
                  name: "",
                  desc: "",
                  type: "",
                  priceLow: "",
                  priceMid: "",
                  priceHigh: "",
                },
              ]);
            }

            // specials 복원
            const savedSpecials = parsedResponses?.specials;
            if (Array.isArray(savedSpecials) && savedSpecials.length > 0) {
              setSpecials(
                savedSpecials.map((s: any, idx: number) => ({
                  id: s.id ? String(s.id) : `special-${idx + 1}`,
                  name: s.name || "",
                  desc: s.desc || "",
                })),
              );
            } else {
              setSpecials([{ id: "special-1", name: "", desc: "" }]);
            }

            // 환불 복원
            const savedRefunds = parsedResponses?.refunds;
            if (Array.isArray(savedRefunds) && savedRefunds.length > 0) {
              setRefunds(
                savedRefunds.map((r: any, idx: number) => ({
                  id: r.id || `refund-${idx + 1}`,
                  day: r.day || "",
                  percent: r.percent || "",
                })),
              );
            } else {
              setRefunds([{ id: "refund-1", day: "", percent: "" }]);
            }
          }
        },
        { ignoreErrorRedirect: true },
      );
    } catch (error) {
      console.error("Failed to check existing submission:", error);
    }
  };

  const fetchPortfolioAndQuestions = async () => {
    try {
      await request(
        () => PortfolioService.getOne(id),
        (res) => {
          if (!res.data) {
            router.push("/");
            return;
          }

          setPortfolio(res.data);
        },
        { ignoreErrorRedirect: true },
      );

      await request(
        () => QuestionService.getPortfolios(id),
        (res) => {
          setQuestions(res.data);
        },
        { ignoreErrorRedirect: true },
      );
    } catch (error) {
      console.error("Failed to fetch data:", error);
    } finally {
      setLoading(false);
    }
  };

  //sort 추가 2025.12.24
  const currentQuestions = questions
    .filter((q) => q.step === currentStep)
    .sort((a, b) => a.order - b.order);

  const validateStep = (): boolean => {
    const newErrors: FormData = {};
    let isValid = true;

    currentQuestions.forEach((question) => {
      const value = formData[question.id];
      if (question.isRequired) {
        //객실
        if (question.questionType === "parlor") {
          if (!rooms || rooms.length === 0) {
            newErrors[question.id] = "객실을 최소 1개 이상 입력해주세요.";
            isValid = false;
            return;
          }

          const hasInvalidRoom = rooms.some(
            (room) =>
              !room.name?.trim() ||
              !room.desc?.trim() ||
              !room.type?.trim() ||
              !room.priceLow?.trim() ||
              !room.priceMid?.trim() ||
              !room.priceHigh?.trim(),
          );

          if (hasInvalidRoom) {
            newErrors[question.id] = "객실의 모든 항목을 입력해주세요.";
            isValid = false;
            return;
          }

          return;
        }

        //스페셜
        if (question.questionType === "special") {
          const hasInvalidSpecial = specials.some((sp) => {
            if (!sp.name?.trim()) return true;
            if (!sp.desc?.trim()) return true;
            if (sp.desc.trim().length < 20) return true;
            return false;
          });

          if (hasInvalidSpecial) {
            newErrors[question.id] =
              "스페셜명과 스페셜 설명을 입력하고, 설명은 최소 20자 이상이어야 합니다.";
            isValid = false;
            return;
          }

          return;
        }

        //환불 검증
        if (question.questionType === "refund") {
          if (!refunds || refunds.length === 0) {
            newErrors[question.id] = "환불 정책을 최소 1개 이상 입력해주세요.";
            isValid = false;
            return;
          }

          const invalid = refunds.some((r, idx) => {
            // percent 필수
            if (!r.percent) return true;

            // day도 필수
            if (idx > 0 && !r.day) return true;

            return false;
          });

          if (invalid) {
            newErrors[question.id] =
              "환불 비율과 방문일 기준을 모두 입력해주세요.";
            isValid = false;
            return;
          }

          return;
        }

        if (question.questionType === "file") {
          const hasNewFile = !!fileMapRef.current[question.id];
          const hasSavedFile = !!value; // 기존 임시저장 값

          if (!hasNewFile && !hasSavedFile) {
            newErrors[question.id] = "파일을 업로드해주세요.";
            isValid = false;
          }
          return;
        }

        if (question.questionType === "checkbox") {
          if (!value || typeof value !== "object") {
            newErrors[question.id] = "최소 하나 이상 선택해주세요.";
            isValid = false;
            return;
          }
          try {
            const options = JSON.parse(question.options || "{}");
            const isMultiple = options.multiple !== false;
            if (isMultiple) {
              if (
                !("checked" in value) ||
                !(value as any).checked ||
                (value as any).checked.length === 0
              ) {
                newErrors[question.id] = "최소 하나 이상 선택해주세요.";
                isValid = false;
                return;
              }
            } else {
              if (!("selected" in value) || !(value as any).selected) {
                newErrors[question.id] = "하나를 선택해주세요.";
                isValid = false;
                return;
              }
            }
          } catch {
            if (
              !("checked" in value) ||
              !(value as any).checked ||
              (value as any).checked.length === 0
            ) {
              newErrors[question.id] = "최소 하나 이상 선택해주세요.";
              isValid = false;
              return;
            }
          }
        }

        if (question.questionType === "checkbox_input") {
          if (!value || typeof value !== "object") {
            newErrors[question.id] = "최소 하나 이상 선택해주세요.";
            isValid = false;
            return;
          }

          const { checked, inputs } = value as {
            checked?: number[];
            inputs?: string[];
          };

          if (!Array.isArray(checked) || checked.length === 0) {
            newErrors[question.id] = "최소 하나 이상 선택해주세요.";
            isValid = false;
            return;
          }

          const hasEmptyInput = checked.some((idx) => {
            const v = inputs?.[idx];
            return !v || !v.trim();
          });

          if (hasEmptyInput) {
            newErrors[question.id] = "선택한 항목의 내용을 모두 입력해주세요.";
            isValid = false;
            return;
          }

          return;
        }

        if (question.questionType === "repeatable") {
          if (!value || !Array.isArray(value) || value.length === 0) {
            newErrors[question.id] = "최소 하나 이상 입력해주세요.";
            isValid = false;
            return;
          }
        }

        if (question.questionType === "agreement") {
          if (!value || !value.agreed) {
            newErrors[question.id] = "안내사항에 동의해주세요.";
            isValid = false;
            return;
          }
        } else {
          if (
            !value ||
            (typeof value === "string" && value.trim().length === 0)
          ) {
            newErrors[question.id] = "이 항목은 필수입니다.";
            isValid = false;
            return;
          }
        }
      }

      if (
        question.requireMinLength &&
        (question.questionType === "text" ||
          question.questionType === "textarea") &&
        typeof value === "string" &&
        value.trim().length > 0 &&
        value.trim().length < question.minLength
      ) {
        newErrors[question.id] =
          `최소 ${question.minLength}자 이상 입력해주세요.`;
        isValid = false;
      }
    });

    setErrors(newErrors);
    return isValid;
  };

  const validateAllSteps = (): boolean => {
    const newErrors: FormData = {};
    let isValid = true;
    const missingSteps: number[] = [];

    questions.forEach((question) => {
      const value = formData[question.id];

      if (!question.isRequired) return;

      const fail = (message: string) => {
        newErrors[question.id] = message;
        isValid = false;
        if (!missingSteps.includes(question.step)) {
          missingSteps.push(question.step);
        }
      };

      //객실
      if (question.questionType === "parlor") {
        if (!rooms || rooms.length === 0) {
          fail("객실을 최소 1개 이상 입력해주세요.");
          return;
        }

        const hasInvalidRoom = rooms.some(
          (room) =>
            !room.name?.trim() ||
            !room.desc?.trim() ||
            !room.type?.trim() ||
            !room.priceLow?.trim() ||
            !room.priceMid?.trim() ||
            !room.priceHigh?.trim(),
        );

        if (hasInvalidRoom) {
          fail("객실의 모든 항목을 입력해주세요.");
        }
        return;
      }

      //스페셜
      if (question.questionType === "special") {
        const hasInvalidSpecial = specials.some(
          (sp) =>
            !sp.name?.trim() || !sp.desc?.trim() || sp.desc.trim().length < 20,
        );

        if (hasInvalidSpecial) {
          fail(
            "스페셜명과 스페셜 설명을 입력하고, 설명은 최소 20자 이상이어야 합니다.",
          );
        }
        return;
      }

      //환불
      if (question.questionType === "refund") {
        if (!refunds || refunds.length === 0) {
          fail("환불 정책을 최소 1개 이상 입력해주세요.");
          return;
        }

        const invalid = refunds.some((r, idx) => {
          if (!r.percent) return true;
          if (idx > 0 && !r.day) return true;
          return false;
        });

        if (invalid) {
          fail("환불 비율과 방문일 기준을 모두 입력해주세요.");
        }
        return;
      }

      //파일
      if (question.questionType === "file") {
        const hasNewFile = !!fileMapRef.current[question.id];
        const hasSavedFile = !!value;

        if (!hasNewFile && !hasSavedFile) {
          fail("파일을 업로드해주세요.");
        }
        return;
      }

      //체크박스
      if (question.questionType === "checkbox") {
        if (!value || typeof value !== "object") {
          fail("최소 하나 이상 선택해주세요.");
          return;
        }

        try {
          const options = JSON.parse(question.options || "{}");
          const isMultiple = options.multiple !== false;

          if (isMultiple) {
            if (!value.checked || value.checked.length === 0) {
              fail("최소 하나 이상 선택해주세요.");
            }
          } else {
            if (!value.selected) {
              fail("하나를 선택해주세요.");
            }
          }
        } catch {
          if (!value.checked || value.checked.length === 0) {
            fail("최소 하나 이상 선택해주세요.");
          }
        }
        return;
      }

      if (question.questionType === "checkbox_input") {
        if (!value || typeof value !== "object") {
          fail("최소 하나 이상 선택해주세요.");
          return;
        }

        const { checked, inputs } = value as {
          checked?: number[];
          inputs?: string[];
        };

        if (!Array.isArray(checked) || checked.length === 0) {
          fail("최소 하나 이상 선택해주세요.");
          return;
        }

        const hasEmptyInput = checked.some((idx) => {
          const v = inputs?.[idx];
          return !v || !v.trim();
        });

        if (hasEmptyInput) {
          fail("선택한 항목의 내용을 모두 입력해주세요.");
        }

        return;
      }

      //repeatable
      if (question.questionType === "repeatable") {
        if (!Array.isArray(value) || value.length === 0) {
          fail("최소 하나 이상 입력해주세요.");
        }
        return;
      }

      //agreement
      if (question.questionType === "agreement") {
        if (!value || !value.agreed) {
          fail("안내사항에 동의해주세요.");
        }
        return;
      }

      //text
      if (!value || (typeof value === "string" && !value.trim())) {
        fail("이 항목은 필수입니다.");
        return;
      }

      if (
        question.requireMinLength &&
        typeof value === "string" &&
        value.trim().length < question.minLength
      ) {
        fail(`최소 ${question.minLength}자 이상 입력해주세요.`);
      }
    });

    setErrors(newErrors);

    if (!isValid && missingSteps.length > 0) {
      alert(
        `${missingSteps.sort((a, b) => a - b).join(", ")}단계에 미완성된 필수 항목이 있습니다.\n해당 단계로 이동하여 모든 필수 항목을 완성해주세요.`,
      );
    }

    return isValid;
  };

  // 객실 추가
  const handleAddRoom = () => {
    setRooms((prev) => [
      ...prev,
      {
        id: `room-${Date.now()}`,
        name: "",
        desc: "",
        type: "",
        priceLow: "",
        priceMid: "",
        priceHigh: "",
      },
    ]);
  };

  // 객실 삭제
  const handleRemoveRoom = (id: string) => {
    setRooms((prev) => prev.filter((room) => room.id !== id));
  };

  // 스페셜 추가
  const handleAddSpecial = () => {
    setSpecials((prev) => [
      ...prev,
      {
        id: `special-${Date.now()}`,
        name: "",
        desc: "",
      },
    ]);
  };

  // 스페셜 삭제
  const handleRemoveSpecial = (id: string) => {
    setSpecials((prev) => prev.filter((sp) => sp.id !== id));
  };

  const handleNext = () => {
    // 디테일 모드면 검증 없이 이동
    if (isDetailMode) {
      if (currentStep < maxStep) {
        setCurrentStep(currentStep + 1);
        window.scrollTo(0, 0);
      }
      return;
    }

    // 작성 모드 검증 실패 시 여기서 멈춤
    const isValid = validateStep();
    if (!isValid) return;

    if (currentStep < maxStep) {
      setCurrentStep(currentStep + 1);
      window.scrollTo(0, 0);
    }
  };

  const handlePrevious = () => {
    if (currentStep > minStep) {
      setCurrentStep(currentStep - 1);
      window.scrollTo(0, 0);
    }
  };

  //임시저장
  const handleSaveDraft = async () => {
    if (!portfolio) return;

    setSubmitting(true);
    try {
      const { response, optionFiles } = extractSubmitData();
      const fd = new FormData();

      // 수정일 경우
      if (existingSubmissionId) {
        fd.append("submissionId", String(existingSubmissionId));
      }

      fd.append("portfolioId", String(portfolio.id));
      fd.append("response", JSON.stringify(response));

      optionFiles.forEach((opt, idx) => {
        fd.append(`optionFiles[${idx}].optionsId`, opt.optionsId);
        fd.append(`optionFiles[${idx}].questionStep`, String(opt.questionStep));
        fd.append(
          `optionFiles[${idx}].questionOrder`,
          String(opt.questionOrder),
        );
        opt.files.forEach((file: File) => {
          fd.append(`optionFiles[${idx}].files`, file);
        });
      });

      await request(
        () => SubmissionService.temporaryPost(fd),
        (res) => {
          alert("임시저장되었습니다.");

          if (!existingSubmissionId) {
            window.location.href = `/portfolio/${portfolio.id}?submissionId=${res.data.submissionId}`;
          }
          startAutoSave();
        },
        { ignoreErrorRedirect: true },
      );
    } catch (error) {
      console.error("Save draft error:", error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmit = async () => {
    if (!validateAllSteps() || !portfolio) return;
    setSubmitting(true);
    try {
      const { response, optionFiles } = extractSubmitData();
      const fd = new FormData();

      // 수정일 경우
      if (existingSubmissionId) {
        fd.append("submissionId", String(existingSubmissionId));
      }

      fd.append("portfolioId", String(portfolio.id));
      fd.append("response", JSON.stringify(response));

      optionFiles.forEach((opt, idx) => {
        fd.append(`optionFiles[${idx}].optionsId`, opt.optionsId);
        fd.append(`optionFiles[${idx}].questionStep`, String(opt.questionStep));
        fd.append(
          `optionFiles[${idx}].questionOrder`,
          String(opt.questionOrder),
        );
        opt.files.forEach((file: File) => {
          fd.append(`optionFiles[${idx}].files`, file);
        });
      });

      await request(
        () => SubmissionService.post(fd),
        (res) => {
          alert("제출이 완료되었습니다!\n데이터가 안전하게 저장되었습니다.");
          router.push("/thank-you");
        },
        { ignoreErrorRedirect: true },
      );
    } catch (error) {
      console.error("Submit error:", error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleChange = (questionId: string, value: any) => {
    if (value instanceof File) {
      fileMapRef.current[questionId] = value; // 즉시 저장
      setFormData((prev) => ({ ...prev, [questionId]: value }));
      return;
    }

    if (value instanceof FileList) {
      const file = value[0];
      if (file) {
        fileMapRef.current[questionId] = file;
      }
      return;
    }

    setFormData((prev) => ({
      ...prev,
      [questionId]: value,
    }));
    if (errors[questionId]) {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[questionId];
        return newErrors;
      });
    }
  };

  //이미지 외부 영역
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        previewRef.current &&
        !previewRef.current.contains(e.target as Node)
      ) {
        setShowPreview(false);
      }
    };
    if (showPreview) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showPreview]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">로딩 중...</div>
      </div>
    );
  }

  if (!portfolio) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">
            포트폴리오를 찾을 수 없습니다
          </h2>
          <button
            onClick={() => {
              router.push("/");
            }}
            className="px-4 py-2 bg-black text-white rounded-lg"
          >
            홈으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">
            아직 설정된 질문이 없습니다
          </h2>
          <p className="text-gray-600 mb-4">관리자에게 문의해주세요.</p>
          <button
            onClick={() => {
              router.push("/");
            }}
            className="px-4 py-2 bg-black text-white rounded-lg"
          >
            홈으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        {/* Portfolio Info */}
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-black mb-2">
            {portfolio.title}
          </h1>
          {portfolio.description && (
            <p className="text-gray-600">{portfolio.description}</p>
          )}
        </div>

        {/* Progress Bar */}
        <div className="mb-8">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm font-medium text-gray-700">
              {currentStep === 0 ? "안내사항" : `단계 ${currentStep}`} /{" "}
              {maxStep}
            </span>
            <span className="text-sm text-gray-500">
              {Math.round(
                ((currentStep - minStep + 1) / (maxStep - minStep + 1)) * 100,
              )}
              % 완료
            </span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div
              className="bg-black h-2 rounded-full transition-all duration-300"
              style={{
                width: `${((currentStep - minStep + 1) / (maxStep - minStep + 1)) * 100}%`,
              }}
            />
          </div>
        </div>

        {/* Form Card */}
        <div className="bg-white border-2 border-black rounded-lg p-8 shadow-lg">
          <div>
            <div className="mb-6">
              <h2 className="text-2xl font-bold text-black mb-2">
                {currentStep === 0 ? "안내사항" : `단계 ${currentStep}`}
              </h2>
              <p className="text-gray-600">
                {currentStep === 0
                  ? "다음 단계로 진행하기 전에 안내사항을 확인해주세요."
                  : "모든 필수 항목을 작성해주세요."}
              </p>
            </div>

            <div className="pr-2 space-y-8">
              {currentQuestions.length === 0 ? (
                <div className="text-center py-8 text-gray-500">
                  이 단계에는 질문이 없습니다.
                </div>
              ) : (
                currentQuestions.map((question) => {
                  if (question.questionType === "parlor") {
                    return (
                      <div key={question.id} className="mt-6 space-y-8">
                        <div className="flex items-center justify-between">
                          <h3 className="flex items-center gap-1 text-lg font-semibold text-black">
                            객실 정보 입력
                            {question.isRequired && (
                              <span className="text-red-500 ml-1">*</span>
                            )}
                            {question.thumbnail && (
                              <div className="relative inline-flex items-center gap-1">
                                <span
                                  className="text-xs text-gray-400 hover:text-black cursor-pointer"
                                  onClick={() =>
                                    setShowPreview((prev) => !prev)
                                  }
                                >
                                  ❓
                                </span>

                                {showPreview && (
                                  <div
                                    ref={previewRef}
                                    className="absolute top-6 left-0 z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2"
                                  >
                                    <img
                                      src={question.thumbnail}
                                      alt={question.title}
                                      className="w-full h-auto object-cover rounded"
                                    />
                                  </div>
                                )}
                              </div>
                            )}
                          </h3>
                          {!isDetailMode && (
                            <button
                              type="button"
                              onClick={handleAddRoom}
                              className="px-4 py-2 bg-gray-100 border-2 border-black rounded-lg text-sm font-semibold hover:bg-black hover:text-white transition-all"
                            >
                              + 객실 추가
                            </button>
                          )}
                        </div>

                        {rooms.length === 0 && (
                          <p className="text-gray-500 text-sm">
                            아직 등록된 객실이 없습니다. “객실 추가”를
                            눌러주세요.
                          </p>
                        )}

                        {rooms.map((room, index) => (
                          <div
                            key={room.id}
                            className="p-4 border rounded-lg space-y-4 relative"
                          >
                            {rooms.length > 1 && (
                              <button
                                type="button"
                                onClick={() => handleRemoveRoom(room.id)}
                                className="absolute top-3 right-3 text-xs bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                              >
                                삭제
                              </button>
                            )}

                            <div className="flex items-center gap-2">
                              <span className="inline-flex w-7 h-7 items-center justify-center rounded-full bg-black text-white text-xs">
                                {index + 1}
                              </span>
                              <p className="text-sm text-gray-700">
                                객실 {index + 1}
                              </p>
                            </div>

                            <div>
                              <label className="block font-semibold mb-1">
                                객실명
                              </label>
                              <p className="text-xs text-gray-500 mb-1">고객에게 노출될 객실 이름을 입력해주세요.</p>
                              <input
                                type="text"
                                value={room.name}
                                disabled={isDetailMode}
                                onChange={(e) => {
                                  const updated = rooms.map((r) =>
                                    r.id === room.id
                                      ? { ...r, name: e.target.value }
                                      : r,
                                  );
                                  setRooms(updated);
                                }}
                                className="w-full border border-gray-300 rounded-lg p-2"
                                placeholder="예: 달빛방, 스테이 101호, 온돌 독채"
                              />
                            </div>

                            <div>
                              <label className="block font-semibold mb-1">
                                객실 설명
                              </label>
                              <p className="text-xs text-gray-500 mb-1">객실의 분위기와 특징, 보이는 풍경, 제공되는 서비스를 자유롭게 작성해주세요.</p>
                              <textarea
                                value={room.desc}
                                disabled={isDetailMode}
                                onChange={(e) => {
                                  const updated = rooms.map((r) =>
                                    r.id === room.id
                                      ? { ...r, desc: e.target.value }
                                      : r,
                                  );
                                  setRooms(updated);
                                }}
                                className="w-full border border-gray-300 rounded-lg p-2"
                                rows={3}
                                placeholder="예: 따뜻한 우드 톤의 인테리어와 넓은 통창으로 숲 전망을 즐길 수 있으며, 프라이빗 바비큐와 조식 서비스가 제공됩니다."
                              />
                            </div>

                            <div>
                              <label className="block font-semibold mb-1">
                                객실 형태
                              </label>
                              <p className="text-xs text-gray-500 mb-1">객실의 구조를 간단히 적어주세요.</p>
                              <input
                                type="text"
                                value={room.type}
                                disabled={isDetailMode}
                                onChange={(e) => {
                                  const updated = rooms.map((r) =>
                                    r.id === room.id
                                      ? { ...r, type: e.target.value }
                                      : r,
                                  );
                                  setRooms(updated);
                                }}
                                className="w-full border border-gray-300 rounded-lg p-2"
                                placeholder="예: 독채형, 복층 구조, 침실 분리형, 원룸형 등"
                              />
                            </div>

                            <div>
                              <label className="block font-semibold mb-1">
                                요금
                              </label>
                              <div className="flex items-center gap-4 mb-4">
                                <label className="w-16 font-semibold text-sm text-gray-700">
                                  비수기
                                </label>
                                <input
                                  type="text"
                                  value={room.priceLow}
                                  disabled={isDetailMode}
                                  onChange={(e) => {
                                    const updated = rooms.map((r) =>
                                      r.id === room.id
                                        ? { ...r, priceLow: e.target.value }
                                        : r,
                                    );
                                    setRooms(updated);
                                  }}
                                  className="w-full border border-gray-300 rounded-lg p-2"
                                  placeholder="예: 주중/주말 100,000 / 200,000"
                                />
                              </div>

                              <div className="flex items-center gap-4 mb-4">
                                <label className="w-16 font-semibold text-sm text-gray-700">
                                  준성수기
                                </label>
                                <input
                                  type="text"
                                  value={room.priceMid}
                                  disabled={isDetailMode}
                                  onChange={(e) => {
                                    const updated = rooms.map((r) =>
                                      r.id === room.id
                                        ? { ...r, priceMid: e.target.value }
                                        : r,
                                    );
                                    setRooms(updated);
                                  }}
                                  className="w-full border border-gray-300 rounded-lg p-2"
                                  placeholder="예: 주중/주말 120,000 / 220,000"
                                />
                              </div>

                              <div className="flex items-center gap-4">
                                <label className="w-16 font-semibold text-sm text-gray-700">
                                  성수기
                                </label>
                                <input
                                  type="text"
                                  value={room.priceHigh}
                                  disabled={isDetailMode}
                                  onChange={(e) => {
                                    const updated = rooms.map((r) =>
                                      r.id === room.id
                                        ? { ...r, priceHigh: e.target.value }
                                        : r,
                                    );
                                    setRooms(updated);
                                  }}
                                  className="w-full border border-gray-300 rounded-lg p-2"
                                  placeholder="예: 주중/주말 150,000 / 250,000"
                                />
                              </div>
                            </div>
                          </div>
                        ))}
                        {errors[question.id] && (
                          <p className="text-sm text-red-500 mt-2">
                            {errors[question.id]}
                          </p>
                        )}
                      </div>
                    );
                  }

                  if (question.questionType === "special") {
                    return (
                      <div key={question.id} className="mt-6 space-y-8">
                        <div className="flex items-center justify-between">
                          <h3 className="flex items-center gap-1 text-lg font-semibold text-black">
                            스페셜 정보 입력
                            {question.isRequired && (
                              <span className="text-red-500 ml-1">*</span>
                            )}
                            {question.thumbnail && (
                              <div className="relative inline-flex items-center gap-1">
                                <span
                                  className="text-xs text-gray-400 hover:text-black cursor-pointer"
                                  onClick={() =>
                                    setShowPreview((prev) => !prev)
                                  }
                                >
                                  ❓
                                </span>

                                {showPreview && (
                                  <div
                                    ref={previewRef}
                                    className="absolute top-6 left-0 z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2"
                                  >
                                    <img
                                      src={question.thumbnail}
                                      alt={question.title}
                                      className="w-full h-auto object-cover rounded"
                                    />
                                  </div>
                                )}
                              </div>
                            )}
                          </h3>
                          {!isDetailMode && (
                            <button
                              type="button"
                              onClick={handleAddSpecial}
                              className="px-4 py-2 bg-gray-100 border-2 border-black rounded-lg text-sm font-semibold hover:bg-black hover:text-white transition-all"
                            >
                              + 스페셜 추가
                            </button>
                          )}
                        </div>

                        {specials.length === 0 && (
                          <p className="text-gray-500 text-sm">
                            아직 등록된 스페셜이 없습니다. “스페셜 추가”를
                            눌러주세요.
                          </p>
                        )}

                        {specials.map((sp, index) => (
                          <div
                            key={sp.id}
                            className="p-4 border rounded-lg space-y-4 relative bg-gray-50"
                          >
                            {specials.length > 1 && (
                              <button
                                type="button"
                                onClick={() => handleRemoveSpecial(sp.id)}
                                className="absolute top-3 right-3 text-xs bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                              >
                                삭제
                              </button>
                            )}

                            <div className="flex items-center gap-2">
                              <span className="inline-flex w-7 h-7 items-center justify-center rounded-full bg-black text-white text-xs">
                                {index + 1}
                              </span>
                              <p className="text-sm text-gray-700">
                                스페셜 {index + 1}
                              </p>
                            </div>

                            <div>
                              <label className="block font-semibold mb-1">
                                스페셜명
                              </label>
                              <p className="text-xs text-gray-500 mb-1">이 숙소에서 경험할 수 있는 특별한 포인트의 이름을 적어주세요.</p>
                              <input
                                type="text"
                                value={sp.name}
                                disabled={isDetailMode}
                                onChange={(e) => {
                                  const updated = specials.map((s) =>
                                    s.id === sp.id
                                      ? { ...s, name: e.target.value }
                                      : s,
                                  );
                                  setSpecials(updated);
                                }}
                                className="w-full border border-gray-300 rounded-lg p-2"
                                placeholder="예: 오션뷰, 불멍 체험, 노천탕, 별보기, 프라이빗 사우나"
                              />
                            </div>

                            <div>
                              <label className="block font-semibold mb-1">
                                스페셜 설명
                              </label>
                              <p className="text-xs text-gray-500 mb-1">이 스페셜이 어떤 경험인지, 왜 특별한지 간단히 설명해주세요.
                              </p>
                              <textarea
                                value={sp.desc}
                                disabled={isDetailMode}
                                onChange={(e) => {
                                  const updated = specials.map((s) =>
                                    s.id === sp.id
                                      ? { ...s, desc: e.target.value }
                                      : s,
                                  );
                                  setSpecials(updated);
                                }}
                                className="w-full border border-gray-300 rounded-lg p-2"
                                rows={3}
                                placeholder="예: 객실 앞 바다를 바라보며 해질녘 노을과 함께 불멍을 즐길 수 있습니다."
                              />
                              <p
                                className={`text-xs mt-1 text-right ${
                                  sp.desc.length < 20
                                    ? "text-red-500"
                                    : "text-gray-500"
                                }`}
                              >
                                {sp.desc.length} / 최소 20자
                              </p>
                            </div>
                          </div>
                        ))}
                        {errors[question.id] && (
                          <p className="text-sm text-red-500 mt-2">
                            {errors[question.id]}
                          </p>
                        )}
                      </div>
                    );
                  }

                  if (question.questionType === "refund") {
                    return (
                      <div key={question.id} className="mt-6 space-y-4">
                        <div className="flex items-center justify-between">
                          <h3 className="flex items-center gap-1 text-lg font-semibold text-black">
                            취소/환불정책
                            {question.isRequired && (
                              <span className="text-red-500 ml-1">*</span>
                            )}
                            {question.thumbnail && (
                              <div className="relative inline-flex items-center gap-1">
                                <span
                                  className="text-xs text-gray-400 hover:text-black cursor-pointer"
                                  onClick={() =>
                                    setShowPreview((prev) => !prev)
                                  }
                                >
                                  ❓
                                </span>

                                {showPreview && (
                                  <div
                                    ref={previewRef}
                                    className="absolute top-6 left-0 z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2"
                                  >
                                    <img
                                      src={question.thumbnail}
                                      alt={question.title}
                                      className="w-full h-auto object-cover rounded"
                                    />
                                  </div>
                                )}
                              </div>
                            )}
                          </h3>
                          {!isDetailMode && (
                            <button
                              type="button"
                              onClick={handleAddRefund}
                              className="px-4 py-2 bg-gray-100 border-2 border-black rounded-lg text-sm font-semibold hover:bg-black hover:text-white transition-all"
                            >
                              + 추가
                            </button>
                          )}
                        </div>

                        <div className="p-4 border rounded-lg bg-gray-50 space-y-3">
                          {refunds.length === 0 && (
                            <p className="text-gray-500 text-sm">
                              아직 등록된 환불 기준이 없습니다. “환불 기준
                              추가”를 눌러주세요.
                            </p>
                          )}

                          {refunds.map((refund, index) => (
                            <div
                              key={refund.id}
                              className="flex flex-wrap items-center gap-2 bg-white p-3 rounded border border-gray-200"
                            >
                              {index === 0 ? (
                                <>
                                  <span>방문당일 총 금액의</span>
                                  <input
                                    type="number"
                                    value={refund.percent}
                                    disabled={isDetailMode}
                                    onChange={(e) => {
                                      const updated = refunds.map((r) =>
                                        r.id === refund.id
                                          ? { ...r, percent: e.target.value }
                                          : r,
                                      );
                                      setRefunds(updated);
                                    }}
                                    className="w-20 border border-gray-300 rounded-lg px-3 py-2 text-center"
                                  />
                                  <span>% 환불</span>
                                </>
                              ) : (
                                <>
                                  <span>방문</span>
                                  <input
                                    type="number"
                                    value={refund.day}
                                    disabled={isDetailMode}
                                    onChange={(e) => {
                                      const updated = refunds.map((r) =>
                                        r.id === refund.id
                                          ? { ...r, day: e.target.value }
                                          : r,
                                      );
                                      setRefunds(updated);
                                    }}
                                    className="w-12 border border-gray-300 rounded-lg px-2 py-1 text-center"
                                  />
                                  <span>일 전 총 금액의</span>
                                  <input
                                    type="number"
                                    value={refund.percent}
                                    disabled={isDetailMode}
                                    onChange={(e) => {
                                      const updated = refunds.map((r) =>
                                        r.id === refund.id
                                          ? { ...r, percent: e.target.value }
                                          : r,
                                      );
                                      setRefunds(updated);
                                    }}
                                    className="w-20 border border-gray-300 rounded-lg px-3 py-2 text-center"
                                  />
                                  <span>% 환불</span>
                                </>
                              )}

                              {!isDetailMode && (
                                <button
                                  type="button"
                                  onClick={() => handleRemoveRefund(refund.id)}
                                  className="ml-auto text-xs bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                                >
                                  삭제
                                </button>
                              )}
                            </div>
                          ))}
                        </div>
                        {errors[question.id] && (
                          <p className="text-sm text-red-500 mt-2">
                            {errors[question.id]}
                          </p>
                        )}
                      </div>
                    );
                  }

                  return (
                    <DynamicFormField
                      key={question.id}
                      question={{
                        ...question,
                        questionType: question.questionType || "text",
                      }}
                      value={formData[question.id]}
                      onChange={(value) => handleChange(question.id, value)}
                      error={errors[question.id]}
                      disabled={isDetailMode}
                    />
                  );
                })
              )}
            </div>
          </div>

          {/* Navigation Buttons */}
          <div className="flex justify-between items-center mt-8 pt-6 border-t-2 border-gray-200">
            {/* 왼쪽 */}
            <div className="flex items-center gap-4">
              <button
                onClick={handlePrevious}
                disabled={currentStep === minStep}
                className={`px-6 py-3 rounded-lg font-semibold transition-all ${currentStep === minStep ? "bg-gray-200 text-gray-400 cursor-not-allowed" : "bg-white text-black border-2 border-black hover:bg-black hover:text-white"}`}
              >
                이전
              </button>

              {/* 객실, 스페셜, 환불 규정 추가 예정*/}
              {currentQuestions.some((q) => q.questionType === "parlor") && (
                <button onClick={handleAddRoom}>객실 추가</button>
              )}

              {currentQuestions.some((q) => q.questionType === "special") && (
                <button onClick={handleAddSpecial}>스페셜 추가</button>
              )}
            </div>

            {/* 오른쪽 - 디테일 모드 분리*/}
            {!isDetailMode ? (
              <div className="flex gap-3">
                {currentStep !== 0 && (<button
                    onClick={handleSaveDraft}
                    disabled={submitting}
                    className="px-6 py-3 border-2 border-gray-300 rounded-lg font-semibold hover:border-black transition-all disabled:opacity-50"
                >
                  임시저장
                </button>)}

                {currentStep < maxStep ? (
                  <button
                    onClick={handleNext}
                    className="px-6 py-3 bg-black text-white rounded-lg font-semibold hover:bg-gray-800 transition-all"
                  >
                    {currentStep === 0 ? "시작하기" : "다음"}
                  </button>
                ) : (
                  <button
                    onClick={handleSubmit}
                    disabled={submitting}
                    className="px-6 py-3 bg-black text-white rounded-lg font-semibold hover:bg-gray-800 transition-all disabled:bg-gray-400 disabled:cursor-not-allowed"
                  >
                    {submitting ? "제출 중..." : "제출하기"}
                  </button>
                )}
              </div>
            ) : (
              <div className="flex gap-3">
                {currentStep < maxStep && (
                  <button
                    onClick={handleNext}
                    className="px-6 py-3 bg-black text-white rounded-lg font-semibold hover:bg-gray-800 transition-all"
                  >
                    다음
                  </button>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Back to Home */}
        <div className="text-center mt-6">
          <button
            onClick={() => {
              router.push("/");
            }}
            className="text-gray-600 hover:text-black transition-all"
          >
            포트폴리오 리스트로 돌아가기
          </button>
        </div>
      </div>
    </div>
  );
}
