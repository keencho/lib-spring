# Spring 공통 라이브러리  

### Spring Security Library
- 개발기록 및 예제 - [블로그](https://keencho.github.io/posts/spring-security-custom-library/)  

### ORM / QueryDSL Library  

#### 1. 빌더 타입의 프로젝션 지원
```java
@Test
public void queryTest() {
    var q = Q.delivery;

    var simpleDTO = KcQSimpleDTO.builder()
            .orderId(q.order.orderId)
            .deliveryId(q.deliveryId)
            .field(q.fromAddress)
            .build();

    var bb = new BooleanBuilder();
    bb.and(q.fromAddress.startsWith("c"));

    var list = queryFactory
            .select(simpleDTO.build())
            .from(q)
            .where(predicate)
            .fetch();
}
```
- 개발기록 및 예제 - [블로그](https://keencho.github.io/posts/querydsl-qbuilder-qsetter-2/)

#### 2. Custom JPARepository  
- 쿼리 날릴때마다 발생하는 중복코드를 줄이기 위한 custom repository
- 1번의 빌더 타입의 프로젝션과 연계하여 아래와 같은 방식으로 조회 가능  

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
        .deliveryDTO(dDTO)
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

### 의존성 추가 / AnnotationProcessor 등록
Jitpack 추가 필요

```gradle
dependencies {
    implementation 'com.github.keencho:lib-spring:version'
    annotationProcessor 'com.github.keencho:lib-spring:version'
}
```

```xml
<dependency>
    <groupId>com.github.keencho</groupId>
    <artifactId>lib-spring</artifactId>
    <version>version</version>
</dependency>

...

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>

            <configuration>
                <source>17</source>
                <target>17</target>

                <generatedSourcesDirectory>target/generated-sources/querydsl</generatedSourcesDirectory>
                <annotationProcessors>
                    <annotationProcessor>
                        com.keencho.lib.spring.jpa.querydsl.KcQuerydslAnnotationProcessor
                    </annotationProcessor>
                </annotationProcessors>
            </configuration>
        </plugin>

    </plugins>
</build>
```
