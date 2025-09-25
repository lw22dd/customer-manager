//API返回的结果统一使用包装
type Result<T> = {
    code: number
    msg: string
    data?: T
}

type PagingResult<T> = {
    items: T[]
    totalCount: number
}

export type { Result, PagingResult }