import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * 조건부 클래스명을 병합하는 헬퍼 (shadcn-vue 표준).
 * clsx로 조건부/배열 클래스를 문자열로 합치고, twMerge로 Tailwind 클래스 충돌을 정리한다.
 * (예: cn('px-2', isActive && 'px-4') → 'px-4' — 뒤 값이 앞을 덮어씀)
 *
 * @param inputs - 클래스명(문자열·객체·배열·falsy 값 혼용 가능)
 * @returns 병합·정리된 최종 클래스 문자열
 */
export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}
