# 00. 디자인 시스템 — shadcn-vue + Tailwind

- **작성일**: 2026-07-21
- **결정 근거**: [ADR-0002](../project/adr/0002-frontend-structure-and-ui.md)

v3의 UI 기반은 **shadcn-vue(Reka UI) + Tailwind v4** 다.
기존 [02_DESIGN_GUIDE](02_DESIGN_GUIDE.md)(v2에서 계승한 컬러·타이포·컴포넌트 톤)는 **버리지 않고** 여기 테마 토큰으로 흡수한다.

---

## 1. 구성 방식

- **shadcn-vue**: 완성된 컴포넌트 소스를 프로젝트로 복사해 소유 (설치형 라이브러리 아님).
    - 기반: **Reka UI**(구 `radix-vue`) — 접근성(키보드·스크린리더) 확보된 UI 프리미티브.
    - 배치: `common/components/ui/` (components.json 경로 설정).
- **Tailwind v4**: `@tailwindcss/vite` 플러그인. 유틸리티 클래스로 스타일링. 순수 CSS·인라인 style 지양.

## 2. 디자인 토큰 매핑

[02_DESIGN_GUIDE](02_DESIGN_GUIDE.md)의 값을 **shadcn-vue 테마(CSS 변수)** 로 옮긴다.

| 디자인 가이드        | shadcn-vue 테마 토큰              |
| -------------------- | --------------------------------- |
| 브랜드 컬러(주/보조) | `--primary`, `--secondary`        |
| 배경·전경            | `--background`, `--foreground`    |
| 경계·강조            | `--border`, `--accent`, `--muted` |
| 위험(삭제 등)        | `--destructive`                   |
| 모서리 반경          | `--radius`                        |
| 타이포(폰트·크기)    | Tailwind `theme` 확장             |

→ 라이트/다크 모드는 CSS 변수 오버라이드로 대응.

## 3. 컴포넌트 배치 원칙

- shadcn-vue 원자 컴포넌트(button·input·dialog·select 등) → `common/components/ui/`
- 이를 조합한 프로젝트 공통 부품(안내창·페이징·파일업로드 등) → `common/components/{feedback,data,form,layout}/`
- 기능 전용 컴포넌트 → 해당 `modules/{기능}/` 안

## 4. 셋업 명령

```bash
# workmate-vue 안에서
npx shadcn-vue@latest init          # components.json 생성 (ui 경로를 common/components/ui 로)
npx shadcn-vue@latest add button dialog input select ...   # 필요한 것만 추가
```

> 최신 설치 절차는 https://www.shadcn-vue.com/docs/installation/vite 확인.
