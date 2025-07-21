# 1. 글로벌 코드 컨벤션

---

### 1. Enum 네이밍

1. 관행을 의식하지 않고 해당 도메인에 어울리는 이름으로 이해하기 쉽게 작성한다.
    - 필요한 경우 용어 사전에도 등록한다.

### 2. 변수 네이밍

1. 줄임말을 사용하지 않는다.

### 3. 메서드 네이밍

1. 줄임말을 사용하지 않는다.
2. 데이터를 확실하게 가져올 수 없고 호출자가 체크해야 한다면, **find** 접두사로 작성한다.
    - ex) Optional: 호출자에게 존재 여부를 위임한다.
3. 데이터를 확실하게 가져올 수 있을 때 접두사로 **get** 접두사로 작성한다.

### 3. final 키워드 사용

1. 클래스 필드 변수를 제외하고는 **`final`** 키워드를 ****사용하지 않는다.

### 4. 어노테이션 순서

1. 컨트롤러는 예외 사항이 존재하기 때문에 컨트롤러 섹션을 참고한다.
2. 어노테이션의 순서는 상단에서 하단으로 어노테이션 길이가 짧은 순서대로 배치한다. 
    - 피라미드 구조
    
    ```java
    @Slf4j
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/places")
    ```
    

### 5. 파라미터 순서

1. 실제 사용 순서대로 파라미터 순서를 정렬한다.
    
    ```java
    public PlaceResponse a(String b, String c){
    		System.out.println(b);
    		System.out.println(c);
    }
    ```
    

### 6. Bean 주입 방식

1. 생성자 주입은 lombok의 **`@RequiredArgsConstructor`** 를 사용한다.

### 7. 개행

1. 빈 Record의 첫 줄은 개행한다.
2. 클래스의 첫 줄은 개행한다.
3. 한 줄에는 한개의 점만 올 수 있도록 2번째 점부터 개행한다.
4. **`Stream()`** 은 예외적으로 3번을 회피할 수 있고 **`Stream()`** 다음에는 첫 줄을 개행한다.
5. 아래 상황과 같은 닫는 괄호는 개행한다.
    
    아래 상황의 경우 아래 규칙으로 개행한다.
    
    ```java
    // 이렇게 개행하지 않는다.
    return new EventDayResponses(
                    eventDays.stream()
                            .map(EventDayResponse::from)
                            .toList());
    
    // 이렇게 마지막 괄호와 세미콜론을 개행하여 작성한다.
    return new EventDayResponses(
                    eventDays.stream()
                            .map(EventDayResponse::from)
                            .toList()
                    );
    ```
    
6. 인터페이스에 내용이 있다면 첫 줄을 개행한다.

### 8. 래퍼 타입 vs 원시 타입

1. 기본적으로 래퍼 타입을 사용한다.
2. null이 들어오지 않는다는 확신이 생긴다면 primitive type 사용 한다.

### 9. 패키지 구조

1. 아래 예시를 따라서 패키지 구조를 작성한다.

```
festival
- controller
- service
- domain
- infrastructure // 구현체들
- exception
- dto
```

### 10. 예외 처리

1. 최상위 예외인 **`BusinessException`** 를 각 도메인에서 상속한 커스텀 예외를 만들어 사용한다.

# 2. Controller 컨벤션

---

### 1. 응답

1. ResponseEntity를 사용하지 않고 @ResponseStatus를 사용하여 status code를 정의한다.
2. 200 OK도 명시적으로 작성한다.

### 2. API 엔드포인트 네이밍

1. 복수형으로 작성한다.

### 3. 메서드 파라미터 선언부 줄바꿈

1. 파라미터가 없는 경우 개행하지 않고 파라미터가 있는 경우 반드시 파라미터 선언부부터 개행한다.
    
    ```java
    // 파라미터 없는 경우
    @GetMapping("/search")
    public ResponseEntity<List<Place>> searchPlaces() {
    }
    
    // 파라미터 있는 경우
    @GetMapping("/search")
    public ResponseEntity<List<Place>> searchPlaces(
        @RequestParam String keyword,
        @RequestParam int page
    ) {
    }
    ```
    

### 4. 어노테이션 순서

1. 상단에는 컨트롤러 관련 어노테이션을 작성하고 하단에는 스웨거 관련 어노테이션을 작성한다.
2. **메서드의 경우** 컨트롤러 관련 어노테이션에 대해 아래 두 가지의 컨벤션을 지키면서 작성한다.
    - GetMapping()이 가장 상단에 작성한다.
    - ResponseStatus()을 그 하단에 작성한다.
        
        ```java
        @GetMapping("/{placeId}/announcements")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "특정 플레이스의 모든 공지 조회")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        })
        ```
        
3. 위의 두 가지 컨벤션에 해당하지 않는다면 **1. 글로벌 코드 컨벤션**을 지켜서 짧은 순서대로 작성한다. 

# 3. API 문서 컨벤션

---

### 1. Swagger

1. Controller와 DTO에만 스웨거 관련 어노테이션을 작성한다.
    - 인터페이스를 분리하여 작성하지 않는다.
2. **`@Tag`** 와 **`@Operation`** 을 사용한다.

# 4. DTO 컨벤션

---

### 1. 네이밍

1. 요청 DTO는 **`XXXRequest`** 로 작성한다.
2. 응답 DTO는 **`XXXResponse`** 로 작성한다.
3. 컬렉션으로 감싼 응답 DTO는 **`XXXResponses`** 로 작성한다.
4. responses의 필드는 **`responses`** 로 작성한다.

### 2. 선언

1. DTO는 **`record`** 로 작성한다.
2. 각 필드 변수는 매번 줄바꿈한다.
3. 생성자 대신 정적 팩터리 메서드를 사용하여 생성한다.
4. **`organizationId`** 는 응답으로 작성하지 않는다.

# 5. Entity 컨벤션

---

### 1. 필드

1. **createdAt**는 필드의 가장 마지막에 작성한다.
2. id는 항상 래퍼타입을 사용한다.
3. 연관관계는 id만 작성하지 않고 entity의 연관관계를 매핑하는 정보까지 작성한다.
    
    ```java
    private Long organizationId // x
    private Organization organization // o
    ```
    

### 2. EqualsHashCode 여부

1. 사용하지 않고 사용 시점이 왔을 때 의논하도록 한다.

### 3. 생성자 규칙

1. Builder 패턴은 사용하지 않는다.
2. id 없는 생성자를 직접 만들어서 사용한다.
3. 모든 매개변수를 개행하여 작성한다.
    - 컨트롤러와 동일하게 작성한다.
        
        ```java
        public Place(
                String title, 
                String description, 
                PlaceCategory category, 
                String location,
                String host,
                LocalTime startTime, 
                LocalTime endTime
        ) {
            this.title = title;
            this.description = description;
            this.category = category;
            this.location = location;
            this.host = host;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        ```
        

### 4. DB 컬럼 타입

1. 문자열 타입으로는 **`VARCHAR`** 를 사용하고 추후 **`TEXT`** 를 사용할 일이 있다면 의논한다.

### 5. Repository 네이밍

1. repository은 **`XXXJpaRepository`** 로 작성한다.

### 6. 기능

1. 정렬은 repository 혹은 service에 스스로 판단한 기준에 따라 작성한다.

# 6. 테스트 코드 컨벤션

---

### 1. 테스트 코드 작성 대상

1. service, repository, 도메인 로직은 단위 테스트로 작성한다.
2. **`RestAssured`** 를 사용하여 API를 테스트한다.

### 2. 테스트 코드 작성 컨벤션

1. **`@DisplayName`** 은 사용하지 않는다.
2. 테스트 메서드는 한글로 작성한다.
3. 테스트 메서드가 숫자로 시작한다면 `_` 로 시작한다.
4. 테스트 클래스에는 `@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)` 를 작성한다.
5. 테스트하려는 메서드마다 `@Nested` 를 작성한다.
6. `@Nested` 클래스 명은 테스트하려는 메서드 명과 동일하게 작성한다.
7. 테스트 메서드 명은 `성공 - 사유/조건`, `실패 - 사유` 로 작성한다.
8. 테스트 결과 값과 기댓값 변수의 이름은 `expected` , `result` 로 작성한다.
9. 모킹과 모킹된 객체 주입은 `@Mock` , `@InjectMocks` 를 사용한다.
10. **`@LocalServerPort`** 는 필드 순서의 가장 마지막에 작성한다.
11. **`HttpStatus`** 를 사용하여 상태 코드 검증을 작성한다.
12. dto 필드를 검증할 때, 필드의 수 검증도 작성한다.
13. 컬렉션의 사이즈 검증은 `$`  + `hasSize()` 로 검증을 작성한다.
14. 아래 방식으로 작성한다.
    
    ```
    // X
        Organization organization = organizationJpaRepository.save(
            OrganizationFixture.create(
                OrganizationFixture.randomName(),
                OrganizationFixture.randomRegistrationNumber(),
                OrganizationFixture.randomPastDate(),
                OrganizationFixture.randomAddress(),
                OrganizationFixture.randomEmail(),
                OrganizationFixture.randomBoolean(),
                OrganizationFixture.randomDescription()
            )
        );
    
    // O
        Organization checkOrganization = OrganizationFixture.create(
            OrganizationFixture.randomName(),
            OrganizationFixture.randomRegistrationNumber(),
            OrganizationFixture.randomPastDate(),
            OrganizationFixture.randomAddress(),
            OrganizationFixture.randomEmail(),
            OrganizationFixture.randomBoolean(),
            OrganizationFixture.randomDescription()
        );
        organizationJpaRepository.save(checkOrganization);
    ```
    

### 3. 개행

1. **Mockito.give()**뒤 ****첫 점부터 개행한다.
2. **`assertThat()`**은 다음의 규칙으로 개행한다.
    
    ```java
    // 기본적으로 하나의 체이닝은 개행하지 않는다.
    assertThat(responses.eventDays()).hasSize(2);
    
    // 만약, 한 줄 길이가 넘어간다면 개행한다.
    assertThat(responses.eventDays().get(0).date()) 
    .isEqualTo(LocalDate.of(2025, 10, 26));
    
    // 만약, 여러개의 체이닝인 경우 개행한다. (극단적 예시)
    assertThat(~~)
    		.hasSize()
    		.hasEmpty();
    ```
    
3. given() 앞에서 개행한다.
    
    ```java
    // X
    RestAssured.given()
    
    // O
    RestAssured
        .given()
    ```
    

### 4. Test Fixture

1. 클래스 네이밍은 **`XXXFixture`** 로 작성한다.
2. 메서드 네이밍은 접두사 **`create`** 로 작성하고 오버로딩한다.
3. 모든 값들을 상단에 필드 변수로 작성한다.
4. 만약, 랜덤 값을 가진 TestFixture가 필요하다면 그때 다시 논의한다. 상황에 따라 결정할 것이다.

```java
public class PlaceFixture {

    private static final String DEFAULT_TITLE = "기본 제목";
    private static final String DEFAULT_DESCRIPTION = "기본 설명";
    private static final PlaceCategory DEFAULT_CATEGORY = PlaceCategory.BOOTH;
    private static final String DEFAULT_LOCATION = "기본 장소";
    private static final String DEFAULT_HOST = "기본 주최";
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(10, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);

    public static Place create() {
        return new Place(
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_CATEGORY,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place create(String title) {
        return new Place(
                title,
                DEFAULT_DESCRIPTION,
                DEFAULT_CATEGORY,
                DEFAULT_LOCATION,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place create(String title, PlaceCategory category, String location) {
        return new Place(
                title,
                DEFAULT_DESCRIPTION,
                category,
                location,
                DEFAULT_HOST,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME
        );
    }

    public static Place createCustom(
            String title,
            String description,
            PlaceCategory category,
            String location,
            String host,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return new Place(
		        title,
	         description, 
	         category, 
	         location, 
	         host, 
	         startTime, 
	         endTime
         );
    }
}
```

1. 다른 Fixture를 사용할 때도 정적 필드로 선언한다.
2. 나머지는 줄이 넘어갔을 때 개행한다.

# 7. 설정 정보 컨벤션

---

### 1. application 설정

- `application.yml` 을 사용하고 `properties`는 사용하지 않는다.