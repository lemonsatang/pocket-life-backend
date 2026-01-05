package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Tx;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TxRepository extends JpaRepository<Tx, Long> {
    // 특정 유저의 해당 날짜 가계부 내역 조회
    List<Tx> findAllByUserIdAndTxDate(String userId, LocalDate txDate);
}