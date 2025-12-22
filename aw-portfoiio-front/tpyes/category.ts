// 카테고리 세부
export interface CategoryContent {
  id: string;
  name: string;
  slug: string;
  order: number;
  createdAt: string;
  updatedAt: string;
  count?: {
    portfolios: number;
  };
}

//카테고리 전체
export interface Category {
  content: CategoryContent[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

//카테고리 전체
export interface CategorySelect {
  id: string;
  name: string;
  order: number;
}
