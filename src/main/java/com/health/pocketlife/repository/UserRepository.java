package com.health.pocketlife.repository;

import com.health.pocketlife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsrid(String usrid);
    boolean existsByUsrid(String usrid); // 아이디 중복검사

    Optional<User> findByUsrnmAndTel(String usrnm, String tel);
}
