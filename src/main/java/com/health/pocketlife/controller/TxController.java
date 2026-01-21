package com.health.pocketlife.controller;

import com.health.pocketlife.dto.*;
import com.health.pocketlife.entity.Tx;
import com.health.pocketlife.repository.TxRepository;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/tx")
public class TxController {
    private final TxRepository txRepository;
    public TxController(TxRepository txRepository){this.txRepository=txRepository;}

    // “로그인한 사람”을 기준으로 조회/저장(Principal.getName() = 로그인 아이디/유저명)
    private String uid(Principal p){ return p.getName(); }

    @GetMapping
    public List<Tx> list(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, Principal p){
        if (year == null || month == null) {
            return txRepository.findAllByUserIdOrderByTxDateDesc(uid(p));
        }
        LocalDate s=LocalDate.of(year,month,1), e=s.plusMonths(1).minusDays(1);
        return txRepository.findByUserIdAndTxDateBetweenOrderByTxDateDesc(uid(p),s,e);
    }

    @GetMapping("/latest")
    public List<Tx> latest(@RequestParam(defaultValue = "desc") String order,
    Principal p
) {
        if (order.equals("asc")) {
            return txRepository.findTop10ByUserIdOrderByTxDateAscIdAsc(uid(p));
        }
        return txRepository.findTop10ByUserIdOrderByTxDateDescIdDesc(uid(p));
    }

    @GetMapping("/summary")
    public TxDTO.TxSummary summary(@RequestParam int year, @RequestParam int month, Principal p){
        List<Tx> list = list(year, month, p);
        long totalIncome = 0;
        long totalExpense = 0;

        for(Tx t : list){
            // Enum 값을 비교할 때는 == 을 사용하는 것이 가장 정확합니다.
            if(t.getType() == Tx.Type.INCOME) {
                totalIncome += t.getAmount();
            } else if(t.getType() == Tx.Type.EXPENSE) {
                totalExpense += t.getAmount();
            }
        }
        return new TxDTO.TxSummary(totalIncome, totalExpense, totalIncome - totalExpense);
    }

    @PostMapping
    public Tx create(@RequestBody TxDTO.TxRequest req, Principal p){
        Tx t=new Tx(); t.setUserId(uid(p)); t.setTxDate(req.txDate()); t.setTitle(req.title()); t.setCategory(req.category()); t.setMemo(req.memo()); t.setAmount(req.amount()); t.setType(Tx.Type.valueOf(req.type()));
        return txRepository.save(t);
    }

    @PutMapping("/{id}")
    public Tx update(@PathVariable Long id, @RequestBody TxDTO.TxRequest req, Principal p){
        Tx t=txRepository.findById(id).orElseThrow(); if(!t.getUserId().equals(uid(p))) throw new RuntimeException("내 데이터 아님");
        t.setTxDate(req.txDate()); t.setTitle(req.title()); t.setCategory(req.category()); t.setMemo(req.memo()); t.setAmount(req.amount()); t.setType(Tx.Type.valueOf(req.type()));
        return txRepository.save(t);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal p){
        Tx t=txRepository.findById(id).orElseThrow(); if(!t.getUserId().equals(uid(p))) throw new RuntimeException("내 데이터 아님");
        txRepository.deleteById(id);
    }

    /**
     * [추가] 2026-01-XX / 효민
     * 무엇: 카테고리별 지출 통계 API 엔드포인트 추가
     * 어디서: TxController.java
     * 왜: 가계부 페이지에서 카테고리별 지출 분석 기능 제공, 프론트엔드에서 날짜별 API를 여러 번 호출하는 대신 기간 범위를 한 번에 처리하여 성능 개선
     * 어떻게: GET /api/tx/category-stats?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD 형식으로 호출, 지출(EXPENSE)만 필터링하여 카테고리별 집계 후 금액 내림차순 정렬
     */
    @GetMapping("/category-stats")
    public ResponseEntity<CategoryStatsResponse> getCategoryStats(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            Principal p) {
        
        List<Tx> transactions = txRepository.findByUserIdAndTxDateBetweenOrderByTxDateDesc(uid(p), startDate, endDate);
        
        // 지출(EXPENSE)만 필터링
        List<Tx> expenses = transactions.stream()
                .filter(tx -> tx.getType() == Tx.Type.EXPENSE)
                .collect(Collectors.toList());
        
        // 카테고리별 집계
        Map<String, Long> categoryMap = new HashMap<>();
        long totalExpense = 0;
        
        for (Tx expense : expenses) {
            String category = expense.getCategory() != null ? expense.getCategory() : "기타";
            long amount = expense.getAmount() != null ? expense.getAmount() : 0;
            categoryMap.put(category, categoryMap.getOrDefault(category, 0L) + amount);
            totalExpense += amount;
        }
        
        /**
         * [수정] 2026-01-XX / 효민
         * 무엇: 람다 표현식에서 사용하기 위해 final 변수로 복사
         * 어디서: TxController.java의 getCategoryStats 메서드
         * 왜: Java 람다 표현식 규칙상 참조하는 지역 변수는 final이거나 effectively final이어야 하는데, totalExpense가 루프에서 수정되어 컴파일 에러 발생
         * 어떻게: 루프 종료 후 totalExpense 값을 final 변수 finalTotalExpense로 복사하여 람다 내부에서 사용
         */
        final long finalTotalExpense = totalExpense;
        
        // 카테고리별 항목 생성 및 정렬 (금액 내림차순)
        List<CategoryStatsResponse.CategoryItem> categories = categoryMap.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    long amount = entry.getValue();
                    int percentage = finalTotalExpense > 0 ? (int) (amount * 100 / finalTotalExpense) : 0;
                    
                    return CategoryStatsResponse.CategoryItem.builder()
                            .category(category)
                            .amount(amount)
                            .percentage(percentage)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getAmount(), a.getAmount())) // 금액 내림차순
                .collect(Collectors.toList());
        
        CategoryStatsResponse response = CategoryStatsResponse.builder()
                .totalExpense(totalExpense)
                .categories(categories)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
