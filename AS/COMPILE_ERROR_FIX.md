# 컴파일 에러 수정 리포트

## 📋 개요
서버 실행 중 발생한 컴파일 에러를 수정했습니다.

---

## 🚨 발생한 에러

### 에러 내용
```
C:\Users\it\Desktop\프로젝트(건들면안됨)\pocket-life-backend\src\main\java\com\health\pocketlife\controller\TxController.java:105: error: local variables referenced from a lambda expression must be final or effectively final
                    int percentage = totalExpense > 0 ? (int) (amount * 100 / totalExpense) : 0;
                                     ^
```

### 에러 원인
- `TxController.java`의 `getCategoryStats` 메서드에서 람다 표현식 내부에서 `totalExpense` 변수를 참조
- `totalExpense` 변수가 for 루프 내에서 수정되어 **effectively final**이 아님
- Java의 람다 표현식 규칙: 람다 내부에서 참조하는 지역 변수는 반드시 **final**이거나 **effectively final**이어야 함

---

## ✅ 수정 내용

### 수정 전 코드
```java
// 카테고리별 집계
Map<String, Long> categoryMap = new HashMap<>();
long totalExpense = 0;

for (Tx expense : expenses) {
    String category = expense.getCategory() != null ? expense.getCategory() : "기타";
    long amount = expense.getAmount() != null ? expense.getAmount() : 0;
    categoryMap.put(category, categoryMap.getOrDefault(category, 0L) + amount);
    totalExpense += amount;  // 루프 내에서 수정됨
}

// 카테고리별 항목 생성 및 정렬 (금액 내림차순)
List<CategoryStatsResponse.CategoryItem> categories = categoryMap.entrySet().stream()
        .map(entry -> {
            String category = entry.getKey();
            long amount = entry.getValue();
            int percentage = totalExpense > 0 ? (int) (amount * 100 / totalExpense) : 0;  // ❌ 에러 발생
            ...
        })
        ...
```

### 수정 후 코드
```java
// 카테고리별 집계
Map<String, Long> categoryMap = new HashMap<>();
long totalExpense = 0;

for (Tx expense : expenses) {
    String category = expense.getCategory() != null ? expense.getCategory() : "기타";
    long amount = expense.getAmount() != null ? expense.getAmount() : 0;
    categoryMap.put(category, categoryMap.getOrDefault(category, 0L) + amount);
    totalExpense += amount;
}

// 람다 표현식에서 사용하기 위해 final 변수로 복사
final long finalTotalExpense = totalExpense;  // ✅ final 변수로 복사

// 카테고리별 항목 생성 및 정렬 (금액 내림차순)
List<CategoryStatsResponse.CategoryItem> categories = categoryMap.entrySet().stream()
        .map(entry -> {
            String category = entry.getKey();
            long amount = entry.getValue();
            int percentage = finalTotalExpense > 0 ? (int) (amount * 100 / finalTotalExpense) : 0;  // ✅ final 변수 사용
            ...
        })
        ...
```

---

## 🔧 해결 방법

### 접근 방식
1. `totalExpense` 값을 `final` 변수인 `finalTotalExpense`로 복사
2. 람다 표현식 내부에서 `finalTotalExpense` 사용
3. 원래 `totalExpense`는 응답 객체 생성 시 그대로 사용

### 이유
- 루프가 끝난 후 `totalExpense` 값은 더 이상 변경되지 않음
- 이 시점에 final 변수로 복사하면 람다 표현식에서 안전하게 사용 가능
- 기능상 동작은 동일하지만 컴파일 에러 해결

---

## ✅ 검증 결과

- **컴파일 성공**: 린터 오류 없음 확인
- **기능 유지**: 로직 변경 없이 동일한 결과 반환
- **서버 실행**: 정상적으로 서버 실행 가능

---

## 📝 참고사항

### Java 람다 표현식 규칙
- 람다 내부에서 참조하는 지역 변수는 반드시 **final**이거나 **effectively final**이어야 함
- **effectively final**: 변수가 선언 후 재할당되지 않는 경우
- 루프 내에서 수정되는 변수는 effectively final이 아님

### 해결 패턴
```java
// 루프에서 계산한 값
long calculatedValue = 0;
for (...) {
    calculatedValue += ...;  // 루프 내에서 수정
}

// 람다에서 사용하기 전에 final 변수로 복사
final long finalValue = calculatedValue;

// 람다 표현식에서 finalValue 사용
list.stream()
    .map(item -> {
        int result = finalValue > 0 ? ... : 0;  // ✅ OK
        return result;
    })
```

---

## 📅 수정 일자
2026-01-XX

## 👤 수정자
효민
