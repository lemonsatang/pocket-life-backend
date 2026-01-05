package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 특정 유저의 해당 날짜 할일 목록 조회
    List<Todo> findAllByUserIdAndDoDate(String userId, LocalDate doDate);
}