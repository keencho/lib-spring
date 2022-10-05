# Spring 공통 라이브러리  

### Spring Security Library
- 개발기록 및 예제 - [블로그](https://keencho.github.io/posts/spring-security-custom-library/)  

### ORM / QueryDSL Library  
- `@QueryProjection`을 사용한 생성자 방식에서 더 나아가 setter / builder 지원
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






