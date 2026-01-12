package com.health.pocketlife.controller;

import com.health.pocketlife.entity.Todo;
import com.health.pocketlife.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
@CrossOrigin(origins = "*")
public class TodoController {
    private final TodoRepository repository;

    @GetMapping
    public List<Todo> getList(@RequestParam String userId, @RequestParam LocalDate date) {
        return repository.findAllByUserIdAndDoDate(userId, date);
    }

    @PostMapping("/create")
    public Todo create(@RequestBody Todo todo, Principal principal) {
        todo.setUserId(principal.getName());
        System.out.println("================ 아이디: "+principal.getName());
        return repository.save(todo);
    }
}