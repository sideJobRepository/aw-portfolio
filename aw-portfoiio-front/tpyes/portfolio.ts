//포토폴리오 세부
export interface PortfolioContent {
  id: string;
  title: string;
  description: string;
  slug: string;
  thumbnail?: string;
  domain?: string; // 미리보기용 도메인 URL
  isActive: boolean;
  order: number;
  categoryId?: string;
  mood?: string; // 분위기
  category?: {
    id: string;
    name: string;
    slug: string;
  };
  count?: {
    questions: number;
    submissions: number;
  };
}

//포토폴리오 목록 전체
export interface Portfolio {
  content: PortfolioContent[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

//포토폴리오 신규, 수정 form
export interface PortfolioForm {
  title: string;
  description: string;
  slug: string;
  thumbnail: string;
  isActive: boolean;
  order: number;
  categoryId: string;
  mood: string; // 분위기
  domain: string;
  thumbnailFile: File | null;
}
