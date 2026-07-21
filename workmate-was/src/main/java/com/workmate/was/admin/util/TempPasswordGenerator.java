package com.workmate.was.admin.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 임시 비밀번호 생성기 (F6-03). 비밀번호 정책(8자+ · 영문 · 숫자 · 특수)을 항상 충족하도록 생성한다.
 * 혼동하기 쉬운 문자(0/O, 1/l/I)는 제외한다.
 */
public final class TempPasswordGenerator {

    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIALS = "!@#$%^&*";
    private static final String ALL = LETTERS + DIGITS + SPECIALS;
    private static final int LENGTH = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    private TempPasswordGenerator() {
    }

    /**
     * 정책 충족 임시 비밀번호를 생성한다 (영문·숫자·특수 각 1자 이상 보장 + 나머지 무작위, 셔플).
     *
     * @return 12자 임시 비밀번호 평문
     */
    public static String generate() {
        List<Character> chars = new ArrayList<>();
        chars.add(pick(LETTERS));
        chars.add(pick(DIGITS));
        chars.add(pick(SPECIALS));
        for (int i = chars.size(); i < LENGTH; i++) {
            chars.add(pick(ALL));
        }
        Collections.shuffle(chars, RANDOM);
        StringBuilder sb = new StringBuilder(LENGTH);
        chars.forEach(sb::append);
        return sb.toString();
    }

    private static char pick(String pool) {
        return pool.charAt(RANDOM.nextInt(pool.length()));
    }
}
