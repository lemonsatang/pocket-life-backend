package com.health.pocketlife.controller;

import com.health.pocketlife.dto.*;
import com.health.pocketlife.entity.Tx;
import com.health.pocketlife.repository.TxRepository;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/tx")
public class TxController {
    private final TxRepository txRepository;
    public TxController(TxRepository txRepository){this.txRepository=txRepository;}

    // “로그인한 사람”을 기준으로 조회/저장(Principal.getName() = 로그인 아이디/유저명)
    private String uid(Principal p){ return p.getName(); }

    @GetMapping public List<Tx> list(@RequestParam int year,@RequestParam int month, Principal p){
        LocalDate s=LocalDate.of(year,month,1), e=s.plusMonths(1).minusDays(1);
        return txRepository.findByUserIdAndTxDateBetweenOrderByTxDateDesc(uid(p),s,e);
    }

    @GetMapping("/latest") public List<Tx> latest(Principal p){ return txRepository.findTop10ByUserIdOrderByTxDateDesc(uid(p)); }

    @GetMapping("/summary") public TxDTO.TxSummary summary(@RequestParam int year, @RequestParam int month, Principal p){
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

    @PostMapping public Tx create(@RequestBody TxDTO.TxRequest req, Principal p){
        Tx t=new Tx(); t.setUserId(uid(p)); t.setTxDate(req.txDate()); t.setTitle(req.title()); t.setCategory(req.category()); t.setMemo(req.memo()); t.setAmount(req.amount()); t.setType(Tx.Type.valueOf(req.type()));
        return txRepository.save(t);
    }

    @PutMapping("/{id}") public Tx update(@PathVariable Long id, @RequestBody TxDTO.TxRequest req, Principal p){
        Tx t=txRepository.findById(id).orElseThrow(); if(!t.getUserId().equals(uid(p))) throw new RuntimeException("내 데이터 아님");
        t.setTxDate(req.txDate()); t.setTitle(req.title()); t.setCategory(req.category()); t.setMemo(req.memo()); t.setAmount(req.amount()); t.setType(Tx.Type.valueOf(req.type()));
        return txRepository.save(t);
    }

    @DeleteMapping("/{id}") public void delete(@PathVariable Long id, Principal p){
        Tx t=txRepository.findById(id).orElseThrow(); if(!t.getUserId().equals(uid(p))) throw new RuntimeException("내 데이터 아님");
        txRepository.deleteById(id);
    }
}
