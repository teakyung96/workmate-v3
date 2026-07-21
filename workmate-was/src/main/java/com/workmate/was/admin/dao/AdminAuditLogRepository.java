package com.workmate.was.admin.dao;

import com.workmate.was.admin.vo.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 관리자 감사 로그 저장 Repository (append-only — 조회·삭제 미노출).
 */
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
}
