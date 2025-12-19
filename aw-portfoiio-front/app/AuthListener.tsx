"use client";
import { useEffect } from "react";
import { useSetRecoilState } from "recoil";
import { userState } from "@/store/user";
import axios from "axios";
import { refreshToken } from "@/lib/axiosInstance";

export default function AuthListener() {
  const setUser = useSetRecoilState(userState);

  const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL;

  // useEffect(() => {
  //   const handler = (e: Event) => {
  //     const custom = e as CustomEvent;
  //     if (custom.detail?.user) {
  //       setUser(custom.detail.user);
  //     }
  //   };
  //
  //   window.addEventListener("auth:refreshed", handler);
  //   return () => window.removeEventListener("auth:refreshed", handler);
  // }, [setUser]);

  useEffect(() => {
    const handler = (e: Event) => {
      const custom = e as CustomEvent;
      if (custom.detail?.user) {
        setUser(custom.detail.user);
      }
    };

    window.addEventListener("auth:refreshed", handler);

    // 최초 마운트 시 refresh 한 번 시도
    refreshToken().catch(() => {
      // 비로그인 상태
    });

    return () => {
      window.removeEventListener("auth:refreshed", handler);
    };
  }, [setUser]);

  return null;
}
