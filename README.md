# Spring 공통 라이브러리  

### Spring Security Library
- 개발기록 및 예제 - [블로그](https://keencho.github.io/posts/spring-security-custom-library/)  

### ORM / QueryDSL Library  

#### 1. `@QueryProjection`을 사용한 생성자 방식에서 더 나아가 setter / builder 지원
```java
@Test
public void queryTest() {
    var q = Q.delivery;

    var deliveryDTO = new KcQDeliveryDTO();
    deliveryDTO.setFromAddress(q.fromAddress);
    deliveryDTO.setFromName(q.fromName);
    deliveryDTO.setFromNumber(q.fromNumber);
    deliveryDTO.build();

    var simpleDTO = KcQSimpleDTO.builder()
            .orderId(q.order.orderId)
            .deliveryId(q.deliveryId)
            .field(q.fromAddress)
            .deliveryDTO(deliveryDTO.build())
            .build();

    var bb = new BooleanBuilder();
    bb.and(q.fromAddress.startsWith("c"));

    var list = queryFactory
            .select(simpleDTO.build())
            .from(q)
            .where(predicate)
            .fetch();

    System.out.println(list.size());
}
```
- 개발기록 및 예제 - [블로그](https://keencho.github.io/posts/querydsl-qbuilder-qsetter-2/)  

##### 적용 방법 (gradle, io.ewerk.gradle.plugins.querydsl 사용한 경우)  
 ```gradle
 build.gradle 
 
 compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl

    doFirst {
        options.compilerArgs = ['-proc:only', '-processor', 'com.keencho.lib.spring.jpa.querydsl.KcQuerydslAnnotationProcessor']
    }
}
 ```  

#### 2. Custom JPARepository  
- 쿼리 날릴때마다 발생하는 중복코드를 줄이기 위한 custom repository
- 1번의 setter / builder와 연계하여 아래와 같은 방식으로 조회 가능  

```java
@Test
public void queryHandlerTest() {
    var q = Q.deliveryHistory;
    var dq = Q.delivery;

    var dDTO = KcQDeliveryDTO.builder()
        .fromAddress(dq.fromAddress)
        .fromName(dq.fromName)
        .build();

    var dto = KcQDeliveryHistoryDTO.builder()
        .id(q.id)
        .text(q.text)
        .deliveryDTO(dDTO.build())
        .build();

    var predicate = new BooleanBuilder();
    predicate.and(dq.fromName.startsWith("김"));

    var list = deliveryHistoryRepository
        .selectList(
            null,
            dto,
            (query) -> query.leftJoin(dq).on(dq.deliveryId.eq(q.deliveryId))
        );
    
        ...
}
```  

##### 적용방법  
KcJpaRepositoryFactoryBean 클래스를 bean class 로 등록해주어야 한다.  
```java
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = KcJpaRepositoryFactoryBean.class)
public class SpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringApplication.class, args);
    }
}
```






