/**
 * ISO 날짜 문자열을 'YYYY.MM.DD' 형식으로 표시한다.
 * @param iso 서버가 내려준 ISO 날짜 문자열
 * @returns 'YYYY.MM.DD' (파싱 실패 시 원본 반환)
 */
export function formatDate(iso: string): string {
    if (!iso) return ''
    const date = new Date(iso)
    if (Number.isNaN(date.getTime())) return iso
    const yyyy = date.getFullYear()
    const mm = String(date.getMonth() + 1).padStart(2, '0')
    const dd = String(date.getDate()).padStart(2, '0')
    return `${yyyy}.${mm}.${dd}`
}
