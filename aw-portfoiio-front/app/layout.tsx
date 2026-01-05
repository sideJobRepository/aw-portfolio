import type { Metadata } from 'next';
import './globals.css';
import RecoilProvider from '@/app/RecoilProvider';
import AuthListener from '@/app/AuthListener';

export const metadata: Metadata = {
    title: '언제나 디자인',
    description: '언제나 디자인 포트폴리오',
    openGraph: {
        title: '언제나 디자인',
        description: '언제나 디자인 포트폴리오',
        url: 'https://www.alwaysdesign.co.kr',
    },
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="ko">
            <meta httpEquiv="Content-Security-Policy" content="upgrade-insecure-requests"></meta>
            <body>
                <RecoilProvider>
                    <AuthListener />
                    {children}
                </RecoilProvider>
            </body>
        </html>
    );
}
