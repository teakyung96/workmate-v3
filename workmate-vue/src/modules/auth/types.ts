/** 세션에 보관되는 로그인 사용자 (WEB LoginUser와 대응) */
export interface LoginUser {
    userSeq: number
    userName: string
    /** 권한 문자열 (예: 'ADMIN' | 'USER') */
    role: string
}

/** 회원가입 요청 (WEB SignupForm과 대응) */
export interface SignupRequest {
    email: string
    password: string
    userName: string
    phone: string
}
