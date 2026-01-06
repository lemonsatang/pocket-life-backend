package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Tx;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TxRepository extends JpaRepository<Tx, Long> {

    // 특정 달(시작~끝) 거래내역: 최신순
    List<Tx> findByUserIdAndTxDateBetweenOrderByTxDateDesc(String userId, LocalDate start, LocalDate end);

    // 최근 10개: 대시보드 “최근 거래” 용도
    List<Tx> findTop10ByUserIdOrderByTxDateDesc(String userId);
}
