package com.health.pocketlife.controller;

import com.health.pocketlife.entity.Todo;
import com.health.pocketlife.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/getList")
    public List<Todo> getList(Principal principal, @RequestParam LocalDate date) {
        return repository.findAllByUserIdAndDoDate(principal.getName(), date);
    }

    @PostMapping("/create")
    public Todo create(@RequestBody Todo todo, Principal principal) {
        todo.setUserId(principal.getName());
        return repository.save(todo);
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteByTodoId(id);
    }
}