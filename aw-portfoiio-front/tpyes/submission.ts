export interface SubmissionContent {
    id: string;
    portfolioId: string;
    companyName: string;
    password: string;
    isDraft: boolean;
    completedAt: string;
    updatedAt: string;
    responses: any;
    portfolio: {
        title: string;
        slug: string;
    };
}

export interface Submission {
    content: SubmissionContent[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}
