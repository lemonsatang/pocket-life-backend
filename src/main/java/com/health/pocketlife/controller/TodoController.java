package com.health.pocketlife.controller;

import com.health.pocketlife.entity.Todo;
import com.health.pocketlife.repository.TodoRepository;
import com.health.pocketlife.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
@CrossOrigin(origins = "*")
public class TodoController {
    private final TodoRepository repository;
    private final TodoService service;

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

    @Transactional
    @PutMapping("/update")
    public Todo update (@RequestBody Todo todoDetail, Principal principal){
        Todo todo = repository.findByTodoId(todoDetail.getTodoId());
        todo.setUserId(principal.getName());
        todo.setContent(todoDetail.getContent());
        return todo;
    }

    @Transactional
    @PutMapping("/toggleDone")
    public Todo toggleDone (@RequestBody Todo todoDetail, Principal principal){
        Todo todo = repository.findByTodoId(todoDetail.getTodoId());
        todo.setIsDone(!todo.getIsDone());
        return todo;
    }

    @GetMapping("/getTodoDates")
    public List<Integer> getTodoDates(@RequestParam String yearMonth, Principal principal) {
        // "2026-01" -> 2026-01-01
        LocalDate start = LocalDate.parse(yearMonth + "-01");
        // 해당 월의 마지막 날 계산
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Todo> todos = repository.findByUserIdAndDoDateBetween(principal.getName(), start, end);

        return todos.stream()
                .map(todo ->
                    // doDate가 String이면 파싱, LocalDate면 바로 getDayOfMonth()
                    todo.getDoDate().getDayOfMonth()
                )
                .distinct() // 중복 제거 (같은 일정이 2개라도 점은 1개만)
                .sorted()   // 정렬 (선택사항)
                .collect(Collectors.toList());
    }

    // 공휴일 데이터 가져오기
    @GetMapping("/getHolidays")
    public Object getHolidays(@RequestParam String year, @RequestParam String month) {
        // Decoding 키를 그대로 넣되, 특수문자 + 를 %2B로 수동 변환
        String serviceKey = "Qyjd0vtp/%2BN1AkF7YJKuNhFFmiD0XxjDAkigKM9L0JrZtzKHeIalWTkuRSU2d1Bl%2BjOePxyx0rm%2BEnAUKxCBiA==";

        // 전체 URL 문자열 생성
        String urlStr = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
                + "?serviceKey=" + serviceKey
                + "&solYear=" + year
                + "&solMonth=" + month
                + "&_type=json";

        URI uri = URI.create(urlStr);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

}