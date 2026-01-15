package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Cart;
import com.health.pocketlife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 날짜별 조회 (즐겨찾기 섞지 않고 순수하게 그 날짜 목록만)
    List<Cart> findAllByShoppingDate(LocalDate shoppingDate);

    // 날짜와 유저로 조회
    List<Cart> findAllByShoppingDateAndUser(LocalDate shoppingDate, User user);

    // 즐겨찾기 목록만 조회 (날짜 상관없음)
    List<Cart> findByIsFavoriteTrue();

    // 검색 등 기존 메서드 유지
    List<Cart> findByTextContaining(String text);
    List<Cart> findAllByText(String text);
    boolean existsByTextAndIsFavoriteTrue(String text);
}