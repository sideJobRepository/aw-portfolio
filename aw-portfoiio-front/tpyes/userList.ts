export interface UserContent {
    id: string;
    email: string;
    name: string;
    role: string;
    createdAt: string;
}

export interface UserList {
    content: UserContent[];
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}
