//API返回的结果统一使用包装
type Result<T> = {
    code: number
    msg: string
    data?: T
}

// 分页结果接口
interface PagingResult<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
}

export type { Result, PagingResult }