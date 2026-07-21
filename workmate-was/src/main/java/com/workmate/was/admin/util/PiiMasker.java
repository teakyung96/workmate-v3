package com.workmate.was.admin.util;

/**
 * 개인정보 마스킹 유틸 (F6-01, §3.5). 관리자 목록에 원문 대신 마스킹 값을 노출한다.
 * 예: email k**@g***.com · phone 010****5678
 */
public final class PiiMasker {

    private PiiMasker() {
    }

    /**
     * 이메일 마스킹 — 로컬 첫 글자 + **, 도메인 첫 글자 + *** + TLD 유지.
     *
     * @param email 원문 이메일 (null·형식 이상 시 원문 그대로 반환)
     * @return 마스킹된 이메일 (예: kim@gmail.com → k**@g***.com)
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return email;
        }
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at + 1);
        if (local.isEmpty() || domain.isEmpty()) {
            return email;
        }
        String maskedLocal = local.charAt(0) + "**";
        int dot = domain.lastIndexOf('.');
        String maskedDomain = (dot > 0)
                ? domain.charAt(0) + "***" + domain.substring(dot)
                : domain.charAt(0) + "***";
        return maskedLocal + "@" + maskedDomain;
    }

    /**
     * 전화번호 마스킹 — 앞 3자리·뒤 4자리 유지, 가운데 마스킹.
     *
     * @param phone 원문 전화번호 (null·빈값 시 그대로 반환)
     * @return 마스킹된 번호 (예: 01012345678 → 010****5678)
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return phone;
        }
        if (digits.length() < 7) {
            return digits.charAt(0) + "****";
        }
        return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
    }
}
