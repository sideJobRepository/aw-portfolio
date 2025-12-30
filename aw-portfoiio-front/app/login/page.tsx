"use client";

import { useRequest } from "@/hooks/useRequest";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AuthService } from "@/services/auth.service";
import { tokenStore } from "@/services/tokenStore";
import { useSetRecoilState } from "recoil";
import { userState } from "@/store/user";

export default function LoginPage() {
  //hooks
  const { request } = useRequest();

  const setUser = useSetRecoilState(userState);

  const [companyName, setCompanyName] = useState("");
  const [password, setPassword] = useState("");
  const [authError, setAuthError] = useState("");

  // 인증 처리
  const handleAuth = async () => {
    setAuthError("");

    if (!companyName.trim()) {
      setAuthError("상호명을 입력해주세요.");
      return;
    }
    if (password.length !== 4 || !/^\d{4}$/.test(password)) {
      setAuthError("비밀번호 4자리를 입력해주세요.");
      return;
    }

    try {
      await request(
        () => AuthService.UserLogin(companyName, password),
        (res) => {
          localStorage.removeItem("showLoginForm");
          tokenStore.set(res.data.token);
          setUser(res.data.user);
          localStorage.setItem("login", "true");

          if (res.data.isNewMember)
            alert("환영합니다! 새로운 회원으로 등록되었습니다.");

          window.location.href = "/";
        },
        { ignoreErrorRedirect: true },
      );
    } catch (error) {
      console.error("Auth error:", error);
      setAuthError("인증 처리 중 오류가 발생했습니다.");
    }
  };
  return (
    <div className="min-h-screen bg-white">
      <header className="bg-white border-b-2 border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <a href="/" className="block">
              <h1 className="text-2xl font-bold text-black">
                <img src="/logo.png" alt="로고" className="h-8" />
              </h1>
            </a>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-12">
        <div className="text-center mb-18">
          <h2 className="text-4xl text-black mb-4">
            당신의 감각에 맞는 디자인을 찾아보세요
          </h2>
          <p className="text-xl text-gray-600">쉽고 간편하게 만들어보세요</p>
        </div>
        <form
          onSubmit={(e) => {
            e.preventDefault(); // 새로고침 방지
            handleAuth(); // 확인 버튼과 동일한 동작
          }}
          className="max-w-md mx-auto mb-12 bg-white border-2 border-black rounded-lg p-8 shadow-lg"
        >
          <div className="text-center mb-6">
            <h3 className="text-2xl font-bold text-black mb-2">
              제출자 정보 입력
            </h3>
            <p className="text-gray-600">
              상호명과 4자리 비밀번호를 입력하세요
            </p>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">
                상호명 <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
                placeholder="상호명을 입력해주세요"
                className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">
                비밀번호 4자리 <span className="text-red-500">*</span>
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) =>
                  setPassword(e.target.value.replace(/[^0-9]/g, "").slice(0, 4))
                }
                placeholder="숫자 4자리"
                maxLength={4}
                className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all"
                required
              />
              <p className="text-sm text-gray-500 mt-1">
                숫자 4자리만 입력 가능합니다
              </p>
            </div>

            {authError && (
              <div className="p-4 bg-red-50 border-2 border-red-500 rounded-lg">
                <p className="text-sm text-red-700">{authError}</p>
              </div>
            )}

            <button
              type="submit"
              className="w-full px-6 py-3 bg-black text-white rounded-lg font-semibold hover:bg-gray-800 transition-all"
            >
              확인
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
