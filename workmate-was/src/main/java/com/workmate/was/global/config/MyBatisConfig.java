package com.workmate.was.global.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 설정 클래스.
 * - @Mapper 애노테이션이 명시된 인터페이스만 스캔하여 JPA Repository와의 Bean 충돌을 방지한다.
 * - 현재는 무거운 조회가 없어 등록된 Mapper가 0개이며, 이후 필요 시 각 도메인 dao/ 아래 ~Mapper 인터페이스 + resources/mapper/[도메인명]/*.xml로 추가한다.
 */
@Configuration
@MapperScan(basePackages = "com.workmate.was.**.dao", annotationClass = Mapper.class)
public class MyBatisConfig {
}
