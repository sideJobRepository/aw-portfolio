'use client';

import { useEffect, useMemo, useRef, useState } from 'react';
import { SubmissionService } from '@/services/submission.service';
import { useRequest } from '@/hooks/useRequest';

interface FieldOption {
    label: string;
    hasInput?: boolean;
}

interface RepeatableField {
    label: string;
    type: 'text' | 'file';
    placeholder?: string;
}

/** 옵션 스키마 (타입가드용) */
interface CheckboxOptions {
    checkboxes: FieldOption[];
    multiple?: boolean; // 기본 true
}

interface RepeatableOptions {
    fields: RepeatableField[];
}

interface AgreementOptions {
    agreementItems: string[];
}

interface CheckboxInputOptions {
    inputs: string[]; // 기본 문구들
}

interface CheckboxInputValue {
    checked: number[];
    inputs: string[];
}

type AnyOptions = CheckboxOptions | RepeatableOptions | AgreementOptions | CheckboxInputOptions | null;

interface DynamicFormFieldProps {
    question: {
        id: string;
        title: string;
        description?: string;
        thumbnail?: string;
        questionType: string; // 'notice' | 'text' | 'textarea' | 'file' | 'checkbox' | 'repeatable' ...
        options?: string;
        isRequired: boolean;
        minLength?: number;
        maxLength?: number;
    };
    value: any;
    onChange: (value: any) => void;
    error?: string;
    disabled?: boolean;
}

/** 안전 파싱 + 타입가드 */
function parseOptions(options?: string): AnyOptions {
    if (!options) return null;
    try {
        const parsed = JSON.parse(options);
        if (parsed && typeof parsed === 'object') {
            return parsed as AnyOptions;
        }
        return null;
    } catch (e) {
        console.error('Failed to parse options:', e);
        return null;
    }
}
function isCheckboxOptions(o: AnyOptions): o is CheckboxOptions {
    return !!o && Array.isArray((o as any).checkboxes);
}

function isAgreementOptions(o: AnyOptions): o is AgreementOptions {
    return !!o && Array.isArray((o as any).agreementItems);
}

function isCheckboxInputOptions(o: AnyOptions): o is CheckboxInputOptions {
    return !!o && Array.isArray((o as any).inputs);
}

export default function DynamicFormField({ question, value, onChange, error, disabled }: DynamicFormFieldProps) {
    //hooks
    const { request } = useRequest();

    //썸네일
    const [showPreview, setShowPreview] = useState(false);
    const previewRef = useRef<HTMLDivElement>(null);

    // 파일업로드 drag
    const inputRef = useRef<HTMLInputElement>(null);

    // 기본값
    const questionType = (question.questionType ?? 'text').toString().trim().toLowerCase();

    // 옵션 파싱 (메모)
    const parsedOptions = useMemo(() => parseOptions(question.options), [question.options]);

    //기존 파일 다운로드
    const downloadFile = async (value: { url: string; name: string }) => {
        console.log(value);
        const fullUrl = value.url;
        const parsedUrl = new URL(fullUrl);
        const filePath = parsedUrl.pathname.slice(1);

        await request(
            () => SubmissionService.fileGet(filePath),
            (res) => {
                console.log('res', res);
                const blob = new Blob([res.data]);

                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = value.name;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            },
            { ignoreErrorRedirect: true }
        );
    };

    //이미지 외부 영역
    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (previewRef.current && !previewRef.current.contains(e.target as Node)) {
                setShowPreview(false);
            }
        };
        if (showPreview) {
            document.addEventListener('mousedown', handleClickOutside);
        } else {
            document.removeEventListener('mousedown', handleClickOutside);
        }
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showPreview]);

    // 텍스트 입력
    if (questionType === 'text') {
        return (
            <div className="space-y-3">
                <label className="block">
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}

                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
                </label>
                <input
                    type="text"
                    value={value ?? ''}
                    onChange={(e) => onChange(e.target.value)}
                    className={`w-full px-4 py-3 border-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'}`}
                    placeholder={(parsedOptions as any)?.placeholder || `${question.title}을(를) 입력하세요`}
                    maxLength={question.maxLength}
                    disabled={disabled}
                />
                {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
        );
    }

    // 텍스트 영역
    if (questionType === 'textarea') {
        return (
            <div className="space-y-3">
                <label className="block">
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}

                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
                </label>
                <textarea
                    value={value ?? ''}
                    onChange={(e) => onChange(e.target.value)}
                    rows={6}
                    className={`w-full px-4 py-3 border-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'}`}
                    placeholder={(parsedOptions as any)?.placeholder || `${question.title}을(를) 입력하세요`}
                    maxLength={question.maxLength}
                    disabled={disabled}
                />
                {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
        );
    }

    // 멀티텍스트
    if (questionType === 'multi_text') {
        const placeholders = {
            placeholder1: (parsedOptions as any)?.placeholder1 || `${question.title} 을(를) 입력하세요`,
            placeholder2: (parsedOptions as any)?.placeholder2 || `${question.title} 을(를) 입력하세요`,
        };

        const values: [string, string] = Array.isArray(value) && value.length === 2 ? [value[0] ?? '', value[1] ?? ''] : ['', ''];

        return (
            <div className="space-y-3">
                <label className="block">
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}
                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
                </label>

                {/* 첫 번째 입력 */}
                <input
                    type="text"
                    value={values[0]}
                    onChange={(e) => {
                        const newValue: [string, string] = [e.target.value, values[1]];
                        onChange(newValue);
                    }}
                    className={`w-full px-4 py-3 border-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'}`}
                    placeholder={placeholders.placeholder1}
                    maxLength={question.maxLength}
                    disabled={disabled}
                />

                {/* 두 번째 입력 */}
                <input
                    type="text"
                    value={values[1]}
                    onChange={(e) => {
                        const newValue: [string, string] = [values[0], e.target.value];
                        onChange(newValue);
                    }}
                    className={`w-full px-4 py-3 border-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'}`}
                    placeholder={placeholders.placeholder2}
                    maxLength={question.maxLength}
                    disabled={disabled}
                />

                {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
        );
    }

    // 파일 업로드 (단일)
    if (questionType === 'file') {
        const hasUploadedFile = value && value.url;
        return (
            <div className="space-y-3">
                <label className="block">
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}

                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
                </label>
                {!disabled && value && (value.fileId || value instanceof File) && (
                    <button
                        type="button"
                        onClick={() => {
                            if (value.fileId) {
                                console.log('value.fileId', value.fileId);
                                // 기존 업로드된 파일 삭제
                                onChange({ deleteFileId: value.fileId });
                            } else {
                                // 새로 업로드한 파일 삭제
                                onChange(null);
                            }
                        }}
                        className="px-4 py-2 bg-gray-100 border-2 border-black rounded-lg text-sm font-semibold hover:bg-black hover:text-white transition-all"
                    >
                        삭제
                    </button>
                )}
                <div
                    onClick={() => {
                        if (!disabled) inputRef.current?.click();
                    }}
                    onDragOver={(e) => {
                        e.preventDefault();
                    }}
                    onDrop={(e) => {
                        e.preventDefault();
                        const file = e.dataTransfer.files?.[0];
                        if (file) onChange(file);
                    }}
                    className={`w-full px-4 py-8 border-2 rounded-lg text-center transition-all 
          ${disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer border-gray-300 hover:border-black'}`}
                >
                    <p className="text-sm text-gray-700 font-medium">{value?.name ?? '클릭하거나 파일을 드래그하세요.'}</p>
                    <input
                        ref={inputRef}
                        type="file"
                        accept="image/*,.pdf,.ai"
                        disabled={disabled}
                        onChange={(e) => {
                            const file = e.target.files?.[0];

                            if (file) onChange(file);
                        }}
                        className="hidden"
                    />
                </div>
                {hasUploadedFile && (
                    <div className="text-sm text-green-700">
                        <button onClick={() => downloadFile(value)} className="underline text-sm text-green-700">
                            기존 파일: {value.name} 다운로드
                        </button>
                    </div>
                )}
                {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
        );
    }

    // 파일 업로드 (다중)
    if (questionType === 'files') {
        const options = parsedOptions as any;
        const maxFiles = options?.maxFiles || 5;
        const maxSizeMB = options?.maxSizeMB || 10;

        // 현재 업로드된 파일들 (File 객체)
        const currentFiles: File[] = Array.isArray(value) ? value.filter((v) => v instanceof File) : value instanceof File ? [value] : [];

        // 기존 저장된 파일들 (서버에서 가져온 파일)
        const savedFiles = Array.isArray(value) ? value.filter((v) => v && v.url) : value && value.url ? [value] : [];

        return (
            <div className="space-y-3">
                <label className="block">
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}

                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
                    <span className="block text-xs text-gray-500 mt-1">
                        최대 {maxFiles}개, 파일당 최대 {maxSizeMB}MB
                    </span>
                </label>

                {/* 업로드된 파일 목록 */}
                {(currentFiles.length > 0 || savedFiles.length > 0) && (
                    <div className="space-y-2">
                        {/* 기존 저장된 파일 */}
                        {savedFiles.map((file: any, idx: number) => (
                            <div key={`saved-${idx}`} className="flex items-center gap-2 p-3 bg-green-50 border border-green-200 rounded-lg">
                                <span className="flex-1 text-sm">✓ {file.name}</span>
                                {!disabled && (
                                    <button
                                        type="button"
                                        onClick={() => {
                                            const deleteFileIds = (value as any)?.deleteFileIds || [];
                                            const newSavedFiles = savedFiles.filter((_: any, i: number) => i !== idx);
                                            onChange({
                                                files: [...newSavedFiles, ...currentFiles],
                                                deleteFileIds: [...deleteFileIds, file.fileId],
                                            });
                                        }}
                                        className="text-xs bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                                    >
                                        삭제
                                    </button>
                                )}
                            </div>
                        ))}

                        {/* 새로 추가한 파일 */}
                        {currentFiles.map((file: File, idx: number) => (
                            <div key={`new-${idx}`} className="flex items-center gap-2 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                                <span className="flex-1 text-sm">
                                    📎 {file.name} ({(file.size / 1024 / 1024).toFixed(2)}MB)
                                </span>
                                {!disabled && (
                                    <button
                                        type="button"
                                        onClick={() => {
                                            const newFiles = currentFiles.filter((_: File, i: number) => i !== idx);
                                            onChange([...savedFiles, ...newFiles]);
                                        }}
                                        className="text-xs bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                                    >
                                        삭제
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                )}

                {/* 파일 업로드 영역 */}
                {!disabled && currentFiles.length + savedFiles.length < maxFiles && (
                    <div
                        onClick={() => inputRef.current?.click()}
                        onDragOver={(e) => e.preventDefault()}
                        onDrop={(e) => {
                            e.preventDefault();
                            const droppedFiles = Array.from(e.dataTransfer.files);
                            const totalFiles = currentFiles.length + savedFiles.length + droppedFiles.length;

                            if (totalFiles > maxFiles) {
                                alert(`최대 ${maxFiles}개까지만 업로드 가능합니다.`);
                                return;
                            }

                            const oversized = droppedFiles.filter((f) => f.size > maxSizeMB * 1024 * 1024);
                            if (oversized.length > 0) {
                                alert(`파일당 최대 ${maxSizeMB}MB까지만 업로드 가능합니다.`);
                                return;
                            }

                            onChange([...savedFiles, ...currentFiles, ...droppedFiles]);
                        }}
                        className="w-full px-4 py-8 border-2 border-dashed border-gray-300 rounded-lg text-center cursor-pointer hover:border-black transition-all"
                    >
                        <p className="text-sm text-gray-700 font-medium">클릭하거나 파일을 드래그하세요</p>
                        <p className="text-xs text-gray-500 mt-1">
                            {currentFiles.length + savedFiles.length} / {maxFiles}개 업로드됨
                        </p>
                        <input
                            ref={inputRef}
                            type="file"
                            multiple
                            accept="image/*,.pdf,.ai"
                            disabled={disabled}
                            onChange={(e) => {
                                const selectedFiles = Array.from(e.target.files || []);
                                const totalFiles = currentFiles.length + savedFiles.length + selectedFiles.length;

                                if (totalFiles > maxFiles) {
                                    alert(`최대 ${maxFiles}개까지만 업로드 가능합니다.`);
                                    e.target.value = '';
                                    return;
                                }

                                const oversized = selectedFiles.filter((f) => f.size > maxSizeMB * 1024 * 1024);
                                if (oversized.length > 0) {
                                    alert(`파일당 최대 ${maxSizeMB}MB까지만 업로드 가능합니다.`);
                                    e.target.value = '';
                                    return;
                                }

                                onChange([...savedFiles, ...currentFiles, ...selectedFiles]);
                                e.target.value = '';
                            }}
                            className="hidden"
                        />
                    </div>
                )}

                {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
        );
    }

    // 체크박스 (조건부 입력)
    if (questionType === 'checkbox') {
        if (!isCheckboxOptions(parsedOptions)) {
            console.error('Invalid checkbox options:', parsedOptions);
            return (
                <div className="space-y-3">
                    <label className="block">
                        <div className="flex items-center gap-1 text-lg font-semibold text-black">
                            <span>{question.title}</span>
                            {question.isRequired && <span className="text-red-500">*</span>}

                            {question.thumbnail && (
                                <div className="relative inline-flex items-center gap-1">
                                    <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                        <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                            <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                        </svg>
                                    </span>

                                    {showPreview && (
                                        <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                            <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                        {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
                    </label>
                    <p className="text-sm text-red-500">체크박스 설정 오류: 관리자에게 문의하세요.</p>
                    <textarea
                        value={typeof value === 'string' ? value : ''}
                        onChange={(e) => onChange(e.target.value)}
                        rows={3}
                        placeholder="여기에 답변을 입력하세요..."
                        className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black"
                        disabled={disabled}
                    />
                </div>
            );
        }

        const isMultiple = parsedOptions.multiple !== false; // 기본 다중 선택
        const currentValue = value || (isMultiple ? { checked: [] as string[], inputs: {} as Record<string, string> } : { selected: '', inputs: {} as Record<string, string> });

        return (
            <div className="space-y-4 bg-white p-6 rounded-lg border-2 border-gray-200">
                <div>
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}

                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <p className="text-sm text-gray-600 mt-1">{question.description}</p>}
                    <p className="text-xs text-gray-500 mt-2">원하는 항목을 선택하고 정보를 입력해주세요</p>
                </div>

                <div className="space-y-3">
                    {parsedOptions.checkboxes.map((option, idx) => {
                        const isChecked = isMultiple ? currentValue.checked?.includes(option.label) : currentValue.selected === option.label;

                        return (
                            <div key={idx} className={`rounded-lg p-4 border-2 transition-all ${isChecked ? 'border-black' : 'border-gray-200'}`}>
                                <label className="flex items-start gap-3 cursor-pointer">
                                    <input
                                        type={isMultiple ? 'checkbox' : 'radio'}
                                        name={isMultiple ? undefined : `question-${question.id}-radio`}
                                        checked={!!isChecked}
                                        disabled={disabled}
                                        onChange={(e) => {
                                            if (isMultiple) {
                                                const newChecked = e.target.checked ? [...(currentValue.checked || []), option.label] : (currentValue.checked || []).filter((c: string) => c !== option.label);

                                                onChange({ ...currentValue, checked: newChecked });
                                            } else {
                                                const newInputs: Record<string, string> = {};
                                                if (option.hasInput && currentValue.inputs?.[option.label]) {
                                                    newInputs[option.label] = currentValue.inputs[option.label];
                                                }
                                                onChange({ selected: option.label, inputs: newInputs });
                                            }
                                        }}
                                        className="w-5 h-5 mt-0.5 text-black border-2 border-gray-400 rounded focus:ring-2 focus:ring-black cursor-pointer"
                                    />
                                    <span className="font-semibold text-black flex-1">{option.label}</span>
                                </label>

                                {option.hasInput && isChecked && (
                                    <input
                                        type="text"
                                        value={currentValue.inputs?.[option.label] || ''}
                                        disabled={disabled}
                                        onChange={(e) => {
                                            onChange({
                                                ...currentValue,
                                                inputs: {
                                                    ...currentValue.inputs,
                                                    [option.label]: e.target.value,
                                                },
                                            });
                                        }}
                                        placeholder={(option as any)?.placeholder || `${option.label} 주소나 계정을 입력하세요`}
                                        className="mt-3 w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-black"
                                    />
                                )}
                            </div>
                        );
                    })}
                </div>
                {error && <p className="text-sm text-red-500 mt-2">{error}</p>}
            </div>
        );
    }

    // 체크박스 input
    if (questionType === 'checkbox_input') {
        if (!isCheckboxInputOptions(parsedOptions)) {
            return <p className="text-sm text-red-500">입력형 체크박스 옵션 설정 오류</p>;
        }

        const defaults = parsedOptions.inputs;

        const currentValue: CheckboxInputValue = {
            checked: Array.isArray(value?.checked) ? value.checked : [],
            inputs: Array.isArray(value?.inputs) ? value.inputs : [...defaults],
        };

        const isChecked = (idx: number) => currentValue.checked.includes(idx);

        return (
            <div className="space-y-4">
                <div>
                    <div className="flex items-center gap-1 text-lg font-semibold text-black">
                        <span>{question.title}</span>
                        {question.isRequired && <span className="text-red-500">*</span>}

                        {question.thumbnail && (
                            <div className="relative inline-flex items-center gap-1">
                                <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                    <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                        <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                    </svg>
                                </span>

                                {showPreview && (
                                    <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                        <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    {question.description && <p className="text-sm text-gray-600 mt-1">{question.description}</p>}
                    <p className="text-xs text-gray-500 mt-2">원하는 항목을 선택하고 정보를 입력해주세요</p>
                </div>

                {defaults.map((defaultValue, idx) => {
                    const checked = isChecked(idx);

                    return (
                        <div key={idx} className={`flex items-center gap-3 p-3 border-2 rounded-lg ${checked ? 'border-black' : 'border-gray-300'}`}>
                            <input
                                type="checkbox"
                                checked={checked}
                                disabled={disabled}
                                onChange={(e) => {
                                    const newChecked = e.target.checked ? [...currentValue.checked, idx] : currentValue.checked.filter((i) => i !== idx);

                                    onChange({
                                        ...currentValue,
                                        checked: newChecked,
                                    });
                                }}
                                className="w-5 h-5"
                            />

                            <input
                                type="text"
                                value={currentValue.inputs[idx] ?? defaultValue}
                                disabled={disabled || !checked}
                                onChange={(e) => {
                                    const newInputs = [...currentValue.inputs];
                                    newInputs[idx] = e.target.value;

                                    onChange({
                                        ...currentValue,
                                        inputs: newInputs,
                                    });
                                }}
                                className={`flex-1 px-4 py-2 border-2 rounded-lg ${checked ? 'border-gray-300 focus:ring-2 focus:ring-black' : 'border-gray-200 bg-gray-100'}`}
                                placeholder={`항목 ${idx + 1}을 입력해주세요.`}
                            />
                        </div>
                    );
                })}

                {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
        );
    }

    // 동의 체크박스 (0단계 안내사항)
    if (questionType === 'agreement') {
        const agreementItems = isAgreementOptions(parsedOptions) ? parsedOptions.agreementItems : [];
        const currentValue = value || { agreed: false };

        return (
            <div className="space-y-6 bg-white rounded-lg border-gray-200">
                {question.thumbnail && (
                    <div className="w-full h-48 bg-gray-100 rounded-xl overflow-hidden">
                        <img src={question.thumbnail} alt={question.title} className="w-full h-full object-cover" loading="lazy" />
                    </div>
                )}

                <div className="text-left">
                    {/* <h2 className="text-2xl font-bold text-black mb-4">{question.title}</h2> */}
                    {question.description && <p className="text-gray-600 leading-relaxed mb-6">{question.description}</p>}
                </div>

                {/* 안내사항 리스트 */}
                {agreementItems.length > 0 && (
                    <div className="space-y-4">
                        {/* <h3 className="text-lg font-semibold text-black">안내사항</h3> */}
                        <div className="rounded-lg space-y-3">
                            {agreementItems.map((item: string, index: number) => (
                                <div key={index} className="flex items-start gap-3">
                                    <span className="flex-shrink-0 w-6 h-6 bg-black text-white rounded-full flex items-center justify-center text-sm font-semibold">{index + 1}</span>
                                    <p className="text-gray-700 leading-relaxed">{item}</p>
                                </div>
                            ))}
                        </div>
                    </div>
                )}

                {/* 동의 체크박스 */}
                <div className="border-gray-200 pt-6">
                    <label className="flex items-start gap-4 cursor-pointer border-gray-300 rounded-lg hover:border-black transition-all">
                        <input type="checkbox" disabled={disabled} checked={currentValue.agreed || false} onChange={(e) => onChange({ agreed: e.target.checked })} className="w-5 h-5 mt-1 text-black border-2 border-gray-400 rounded focus:ring-2 focus:ring-black cursor-pointer" />
                        <div className="flex-1">
                            <span className="text-lg font-semibold text-black">
                                개인정보 수집·이용에 대해 동의합니다.
                                {question.isRequired && <span className="text-red-500 ml-1">*</span>}
                            </span>
                            <p className="text-sm text-gray-600 mt-1">다음 단계로 진행하려면 반드시 동의해주세요.</p>
                        </div>
                    </label>
                </div>

                {error && <p className="text-sm text-red-500 text-center">{error}</p>}
            </div>
        );
    }

    // 기본: textarea (호환용)
    return (
        <div className="space-y-3">
            <label className="block">
                <div className="flex items-center gap-1 text-lg font-semibold text-black">
                    <span>{question.title}</span>
                    {question.isRequired && <span className="text-red-500">*</span>}

                    {question.thumbnail && (
                        <div className="relative inline-flex items-center gap-1">
                            <span className="text-xs text-gray-400 hover:text-black cursor-pointer" onClick={() => setShowPreview((prev) => !prev)}>
                                <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="30" height="30" viewBox="0 0 50 50">
                                    <path d="M25,2C12.318,2,2,12.318,2,25s10.318,23,23,23s23-10.318,23-23S37.682,2,25,2z M26.797,36.935 c0,0.216-0.144,0.358-0.358,0.358h-2.726c-0.217,0-0.359-0.143-0.359-0.358v-3.084c0-0.215,0.143-0.358,0.359-0.358h2.726 c0.215,0,0.358,0.144,0.358,0.358V36.935z M29.952,23.268l-2.403,3.3c-0.717,0.968-0.933,1.47-0.933,2.689v1.147 c0,0.215-0.143,0.358-0.358,0.358h-2.367c-0.215,0.004-0.358-0.14-0.358-0.355v-1.47c0-1.436,0.322-2.188,1.075-3.229l2.404-3.3 c1.254-1.721,1.684-2.546,1.684-3.766c0-2.044-1.434-3.335-3.479-3.335c-2.008,0-3.299,1.219-3.729,3.407 c-0.036,0.215-0.179,0.323-0.395,0.287l-2.259-0.395c-0.216-0.036-0.323-0.179-0.288-0.395c0.539-3.443,3.014-5.703,6.744-5.703 c3.872,0,6.49,2.546,6.49,6.097C31.78,20.327,31.172,21.582,29.952,23.268z"></path>
                                </svg>
                            </span>

                            {showPreview && (
                                <div ref={previewRef} className="absolute top-6 left-[-7rem] z-50 w-72 border border-gray-300 shadow-lg bg-white rounded-lg p-2">
                                    <img src={question.thumbnail} alt={question.title} className="w-full h-auto object-cover rounded" />
                                </div>
                            )}
                        </div>
                    )}
                </div>
                {question.description && <span className="block text-sm text-gray-600 mt-1">{question.description}</span>}
            </label>
            <textarea
                value={value ?? ''}
                onChange={(e) => onChange(e.target.value)}
                rows={6}
                className={`w-full px-4 py-3 border-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-black transition-all ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300'}`}
                placeholder="여기에 답변을 입력하세요..."
                disabled={disabled}
            />
            {error && <p className="text-sm text-red-500">{error}</p>}
        </div>
    );
}
