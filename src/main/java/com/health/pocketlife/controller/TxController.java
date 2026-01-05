package com.health.pocketlife.controller;

import com.health.pocketlife.entity.Tx;
import com.health.pocketlife.repository.TxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tx")
@CrossOrigin(origins = "*")
public class TxController {
    private final TxRepository repository;

    @GetMapping
    public List<Tx> getList(@RequestParam String userId, @RequestParam LocalDate date) {
        return repository.findAllByUserIdAndTxDate(userId, date);
    }

    @PostMapping
    public Tx create(@RequestBody Tx tx) {
        return repository.save(tx);
    }
}