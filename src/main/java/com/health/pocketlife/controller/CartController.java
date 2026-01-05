package com.health.pocketlife.controller;

import com.health.pocketlife.entity.Cart;
import com.health.pocketlife.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
// [핵심 수정] 프론트엔드와 통일하기 위해 주소 변경 (shopping -> cart)
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*") // 이 설정 덕분에 WebConfig 없어도 됨
public class CartController {
    private final CartRepository repository;

    // 1. 해당 날짜의 목록만 조회
    @GetMapping
    public List<Cart> getList(@RequestParam LocalDate date) {
        return repository.findAllByShoppingDate(date);
    }

    // 2. 즐겨찾기 목록 별도 조회
    @GetMapping("/favorites")
    public List<Cart> getFavorites() {
        return repository.findByIsFavoriteTrue();
    }

    @GetMapping("/search")
    public List<Cart> search(@RequestParam String text) {
        return repository.findByTextContaining(text);
    }

    // 3. 생성 로직 (숨겨진 즐겨찾기 재활용 포함)
    @PostMapping
    @Transactional
    public Cart create(@RequestBody Cart item) {
        List<Cart> sameNameItems = repository.findAllByText(item.getText());

        for (Cart exists : sameNameItems) {
            // A. 이미 오늘 목록에 있으면 -> 개수 증가
            if (exists.getShoppingDate() != null
                    && exists.getShoppingDate().equals(item.getShoppingDate())
                    && !exists.getIsBought()) {
                exists.setCount(exists.getCount() + 1);
                return repository.save(exists);
            }

            // B. 즐겨찾기해놔서 DB에 있지만 날짜가 없는(숨겨진) 항목 재활용
            if (exists.getShoppingDate() == null) {
                exists.setShoppingDate(item.getShoppingDate());
                exists.setIsBought(false);
                exists.setCount(1);
                return repository.save(exists);
            }
        }

        // C. 아예 없으면 새로 생성
        boolean isFav = repository.existsByTextAndIsFavoriteTrue(item.getText());
        item.setIsFavorite(isFav);
        item.setCount(1);
        return repository.save(item);
    }

    @PutMapping("/{id}")
    @Transactional
    public Cart update(@PathVariable Long id, @RequestBody Cart item) {
        Cart target = repository.findById(id).orElseThrow(() -> new RuntimeException("Error"));

        if (target.getIsFavorite() != item.getIsFavorite()) {
            repository.findAllByText(target.getText()).forEach(c -> c.setIsFavorite(item.getIsFavorite()));
        }

        target.setIsBought(item.getIsBought());
        target.setShoppingDate(item.getShoppingDate());
        target.setText(item.getText());
        target.setCount(item.getCount());
        target.setIsFavorite(item.getIsFavorite());
        return repository.saveAndFlush(target);
    }

    // 4. 삭제 로직 (즐겨찾기 보호)
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        Cart target = repository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));

        if (target.getIsFavorite()) {
            // 즐겨찾기라면 날짜만 지워서 리스트에서 숨김 (DB 삭제 X)
            target.setShoppingDate(null);
            target.setIsBought(false);
            target.setCount(1);
            repository.save(target);
        } else {
            // 즐겨찾기가 아니면 진짜 삭제
            repository.delete(target);
        }
    }
}