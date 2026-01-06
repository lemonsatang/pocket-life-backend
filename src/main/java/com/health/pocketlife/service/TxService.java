package com.health.pocketlife.service;

import com.health.pocketlife.entity.Tx;
import com.health.pocketlife.repository.TxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TxService {

    private final TxRepository txRepository;

    //  월(예: 2026-01) 거래내역 전체 조회
    public List<Tx> listMonth(String userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);           // 그 달 1일
        LocalDate end = ym.atEndOfMonth();       // 그 달 마지막날
        return txRepository.findByUserIdAndTxDateBetweenOrderByTxDateDesc(userId, start, end);
    }

    //  대시보드 "최근 거래 10개"
    public List<Tx> latest10(String userId) {
        return txRepository.findTop10ByUserIdOrderByTxDateDesc(userId);
    }

    //  거래 1건 저장(수입/지출 등록)
    @Transactional
    public Tx create(String userId, Tx tx) {
        // 사용자가 본인 데이터만 넣도록 서버에서 userId를 강제로 세팅
        tx.setUserId(userId);
        return txRepository.save(tx);
    }

    //  거래 1건 수정(본인 것만 수정)
    @Transactional
    public Tx update(String userId, Long txId, Tx req) {
        Tx tx = txRepository.findById(txId)
                .orElseThrow(() -> new IllegalArgumentException("거래가 없어: txId=" + txId));

        // 다른 사람 데이터 수정 방지
        if (!userId.equals(tx.getUserId())) {
            throw new IllegalArgumentException("본인 거래만 수정할 수 있어");
        }

        // 필요한 값만 덮어쓰기
        tx.setTxDate(req.getTxDate());
        tx.setType(req.getType());
        tx.setCategory(req.getCategory());
        tx.setTitle(req.getTitle());
        tx.setMemo(req.getMemo());
        tx.setAmount(req.getAmount());

        return tx;
    }

    //  거래 1건 삭제(본인 것만 삭제)
    @Transactional
    public void delete(String userId, Long txId) {
        Tx tx = txRepository.findById(txId)
                .orElseThrow(() -> new IllegalArgumentException("거래가 없어: txId=" + txId));

        if (!userId.equals(tx.getUserId())) {
            throw new IllegalArgumentException("본인 거래만 삭제할 수 있어");
        }
        txRepository.delete(tx);
    }

    //  월 요약(수입/지출/합계) - 화면 오른쪽 "요약" 영역용
    public Summary summaryMonth(String userId, int year, int month) {
        List<Tx> list = listMonth(userId, year, month);

        long income = 0; // 수입 합
        long expense = 0; // 지출 합

        for (Tx tx : list) {
            if ("INCOME".equalsIgnoreCase(String.valueOf(tx.getType()))) income += tx.getAmount();
            if ("EXPENSE".equalsIgnoreCase(String.valueOf(tx.getType()))) expense += tx.getAmount();
        }

        long net = income - expense; // 남은 금액(수입-지출)
        return new Summary(income, expense, net);
    }

    //  요약 응답용(간단 DTO)
    public record Summary(long income, long expense, long net) {}
}
