package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Tx;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TxRepository extends JpaRepository<Tx, Long> {

    // 특정 유저의 거래내역을 최신 날짜부터 전부 가져오기
    List<Tx> findByUserIdOrderByTxDateDesc(String userId);

    // 특정 유저의 거래내역 중 최근 10개만 가져오기(대시보드 "최근 거래"용)
    List<Tx> findTop10ByUserIdOrderByTxDateDesc(String userId);

    // 특정 유저 + 특정 달(시작~끝) 범위 거래내역 가져오기(월별 목록/합계용)
    List<Tx> findByUserIdAndTxDateBetweenOrderByTxDateDesc(
            String userId, LocalDate start, LocalDate end
    );
}
