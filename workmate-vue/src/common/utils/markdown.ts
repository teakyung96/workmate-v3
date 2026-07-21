import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'

// linkify: URL 자동 링크, breaks: 줄바꿈을 <br>로
const md = new MarkdownIt({ linkify: true, breaks: true })

/**
 * 마크다운 문자열을 안전한 HTML로 변환한다.
 * AI 응답은 외부(LLM) 생성물이므로 DOMPurify로 XSS를 살균한 뒤 렌더한다.
 *
 * @param text 마크다운 원문
 * @returns 살균된 HTML 문자열 (v-html용)
 */
export function renderMarkdown(text: string): string {
    return DOMPurify.sanitize(md.render(text))
}
