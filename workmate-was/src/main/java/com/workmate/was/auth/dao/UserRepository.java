package com.workmate.was.auth.dao;

import com.workmate.was.auth.vo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 조회/저장 Repository.
 * findByEmail 파라미터에도 AesCryptoConverter 가 적용되어 암호문 기준으로 동등 비교된다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * 이름 부분 검색 (관리자 목록, F6-01).
     * email/phone 은 결정적 AES 암호문이라 부분검색이 불가능하므로 user_name 기준으로만 LIKE 검색한다.
     */
    Page<User> findByUserNameContainingIgnoreCase(String userName, Pageable pageable);
}
