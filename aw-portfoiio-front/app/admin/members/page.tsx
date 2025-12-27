'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import {useRecoilValue, useSetRecoilState} from "recoil";
import {userState} from "@/store/user";
import {PortfolioService} from "@/services/portfolios.service";
import {useRequest} from "@/hooks/useRequest";
import {MemberService} from "@/services/member.service";
import {Member, MemberContent} from "@/tpyes/member";
import Pagination from "@/components/Pagination";
import axios from "axios";
import api from "@/lib/axiosInstance";
import {tokenStore} from "@/services/tokenStore";


interface User {
    id: string;
    email: string;
    name: string;
    role: string;
}

export default function MembersPage() {
    const router = useRouter();

    //hooks
    const { request } = useRequest();

    //유저정보
    const currentUser = useRecoilValue(userState);
    const resetUser = useSetRecoilState(userState);

    //페이지
    const [page, setPage] = useState(0);

    //비밀번호 변경
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [passwordTargetMember, setPasswordTargetMember] = useState<MemberContent | null>(null);
    const [newPassword, setNewPassword] = useState("");

    const [members, setMembers] = useState<Member>();
    const [loading, setLoading] = useState(true);
    const [selectedMember, setSelectedMember] = useState<MemberContent | null>(null);

    //페이지 이동 함수
    const handlePageClick = (pageNum: number) => {
        setPage(pageNum);
    };


    const fetchMembers = async () => {
        try {
            setLoading(true);

            await request(
                () => MemberService.get({ page: page, size: 5 }),
                (res) => {
                    console.log("멤버 목록 조회", res);
                    setMembers(res.data);
                    //카테고리 select 목록
                },
                { ignoreErrorRedirect: true },
            );
        } catch (error) {
            console.error('Failed to fetch members:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteMember = async (memberId: string) => {
        if (!confirm('정말로 이 회원을 삭제하시겠습니까?')) {
            return;
        }

        await request(
            () => MemberService.delete(memberId),
            (res) => {
                alert('회원이 삭제되었습니다.');
                fetchMembers(); // 목록 새로고침
            },
            { ignoreErrorRedirect: true },
        );
    };

    const handleSubmitPasswordChange = async () => {
        if (!passwordTargetMember) return;

        if (newPassword.length !== 4 || !/^\d{4}$/.test(newPassword)) {
            alert("비밀번호 4자리를 입력해주세요.");
            return;
        }

        await request(
            () =>
                MemberService.post({
                    memberId: passwordTargetMember.id,
                    password: newPassword,
                }),
            () => {
                alert("비밀번호가 변경되었습니다.");
                setShowPasswordModal(false);
                setPasswordTargetMember(null);
                setNewPassword("");
            },
            { ignoreErrorRedirect: true },
        );
    };

    const handleLogout = async () => {
        await axios.delete("/api/refresh", {
            baseURL: api.defaults.baseURL,
            withCredentials: true,
        });

        tokenStore.clear();
        resetUser(null);
        localStorage.removeItem("login");

        router.push("/admin/login");
    };

    const formatDate = (dateString: string | null) => {
        if (!dateString) return 'N/A';
        return new Date(dateString).toLocaleString('ko-KR');
    };

    //로그인
    useEffect(() => {
        const login = localStorage.getItem("login");

        if (!login) {
            router.push("/admin/login");
            return;
        }

        if (currentUser) {
            if (currentUser?.role !== "SUPER_ADMIN") {
                alert('최고 관리자만 접근할 수 있습니다.');
                router.push("/admin/dashboard");
                return;
            }
        }

    }, [router, currentUser]);

    //페이지 이동시 조회
    useEffect(() => {
        fetchMembers();
    }, [page]);


    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-xl text-gray-600">로딩 중...</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                    <div className="flex justify-between items-center">
                        <div className="flex items-center gap-4">
                            <Link href="/admin/super" className="text-2xl font-bold text-black hover:text-gray-700">
                                관리자 페이지
                            </Link>
                            <span className="text-gray-400">|</span>
                            <h1 className="text-xl font-semibold text-gray-800">회원 관리</h1>
                        </div>
                        <div className="flex items-center gap-4">
                            <span className="text-gray-600">{currentUser?.email}님 (최고 관리자)</span>
                            <button onClick={handleLogout} className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors">
                                로그아웃
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="mb-6">
                    <h2 className="text-3xl font-bold text-gray-900 mb-2">회원 목록</h2>
                    <p className="text-gray-600">상호명으로 가입한 모든 회원들을 관리할 수 있습니다.</p>
                </div>

                {/* Stats */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                    <div className="bg-white rounded-lg shadow p-6">
                        <div className="flex items-center">
                            <div className="p-2 rounded-lg">
                                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                                    />
                                </svg>
                            </div>
                            <div className="ml-4">
                                <p className="text-sm font-medium text-gray-600">총 회원 수</p>
                                <p className="text-2xl font-bold text-gray-900">{members?.totalElements}</p>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow p-6">
                        <div className="flex items-center">
                            <div className="p-2 rounded-lg">
                                <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <div className="ml-4">
                                <p className="text-sm font-medium text-gray-600">오늘 가입</p>
                                <p className="text-2xl font-bold text-gray-900">{members?.todaySignupCount}</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Members Table */}
                <div className="bg-white shadow-sm rounded-lg overflow-hidden">
                    <div className="px-6 py-4 border-b border-gray-200">
                        <h3 className="text-lg font-medium text-gray-900">회원 목록</h3>
                    </div>

                    {members?.content.length === 0 ? (
                        <div className="px-6 py-12 text-center">
                            <p className="text-gray-500">등록된 회원이 없습니다.</p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상호명</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">가입일</th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">마지막
                                        로그인
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">IP
                                        주소
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">작업</th>
                                </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                {members?.content.map((member) => (
                                    <tr key={member.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div
                                                className="text-sm font-medium text-gray-900">{member.companyName}</div>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{formatDate(member.createdAt)}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{formatDate(member.lastLoginAt)}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{member.ipAddress || 'N/A'}</td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                            <button onClick={() => setSelectedMember(member)}
                                                    className="text-blue-600 hover:text-blue-900 mr-4">
                                                상세보기
                                            </button>
                                            <button onClick={() => handleDeleteMember(member.id)}
                                                    className="text-red-600 hover:text-red-900 mr-4">
                                                삭제
                                            </button>
                                            <button  onClick={() => {
                                                setPasswordTargetMember(member);
                                                setNewPassword("");
                                                setShowPasswordModal(true);
                                            }}
                                                    className="text-green-600 hover:text-green-900">
                                                비밀번호 변경
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                    <div className="my-6">
                    <Pagination
                            current={page}
                            totalPages={members?.totalPages ?? 0}
                            onChange={handlePageClick}
                        />
                    </div>
                </div>
            </div>

            {/* Member Detail Modal */}
            {selectedMember && (
                <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
                    <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                        <div className="mt-3">
                            <h3 className="text-lg font-medium text-gray-900 mb-4">회원 상세 정보</h3>
                            <div className="space-y-3">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">상호명</label>
                                    <p className="mt-1 text-sm text-gray-900">{selectedMember.companyName}</p>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">가입일</label>
                                    <p className="mt-1 text-sm text-gray-900">{formatDate(selectedMember.createdAt)}</p>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">마지막 로그인</label>
                                    <p className="mt-1 text-sm text-gray-900">{formatDate(selectedMember.lastLoginAt)}</p>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">IP 주소</label>
                                    <p className="mt-1 text-sm text-gray-900">{selectedMember.ipAddress || 'N/A'}</p>
                                </div>
                            </div>
                            <div className="flex justify-end mt-6">
                                <button onClick={() => setSelectedMember(null)} className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition-colors">
                                    닫기
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {showPasswordModal && passwordTargetMember && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md">
                        <h3 className="text-lg font-bold text-gray-900 mb-4">
                            비밀번호 변경
                        </h3>

                        <p className="text-sm text-gray-600 mb-2">
                            대상 회원: <strong>{passwordTargetMember.companyName}</strong>
                        </p>

                        <input
                            type="password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            placeholder="숫자 4자리"
                            className="w-full px-4 py-2 border rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-black"
                        />

                        <div className="flex justify-end gap-2">
                            <button
                                onClick={() => {
                                    setShowPasswordModal(false);
                                    setPasswordTargetMember(null);
                                }}
                                className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400"
                            >
                                취소
                            </button>
                            <button
                                onClick={handleSubmitPasswordChange}
                                className="px-4 py-2 bg-black text-white rounded-lg hover:bg-gray-800"
                            >
                                저장
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
