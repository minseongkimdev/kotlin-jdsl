# Kotlin JDSL

Kotlin JDSL is DSL for JPA Criteria API without generated metamodel and reflection. It helps you write a JPA query like
writing an SQL statement.

### Background

There are several libraries in the easy way to use JPA. However, those libraries have to use APT. If you use APT, there is a problem that you have to compile again when the name or type of entity field is changed. So, in order not to use APT, we created this library using the KProperty created by the kotlin compiler.

## Quick start

### Hibernate

Add Hibernate Kotlin JDSL and Hibernate to dependencies

```kotlin
dependencies {
    implementation("com.linecorp.kotlin-jdsl:hibernate-kotlin-jdsl:x.y.z")
    implementation("org.hibernate:hibernate-core:x.y.z")
}
```

Create QueryFactory using EntityManager

```kotlin
val queryFactory: QueryFactory = QueryFactoryImpl(
    criteriaQueryCreator = CriteriaQueryCreatorImpl(entityManager),
    subqueryCreator = SubqueryCreatorImpl()
)
```

Query using it

```kotlin
queryFactory.listQuery<Entity> {
    select(entity(Book::class))
    from(entity(Book::class))
    where(column(Book::id).equal(1000))
}
```

### Spring Data
If you use Spring Boot & Data Frameworks
See [more](./spring/README.md)

## Usage

You can easily write query using Entity associations.

![](document/image/Visual1.gif)

If you want to return the DTO, use the DTO as the return type.

![](document/image/Visual2.gif)
### Query

QueryFactory allows you to create JPA queries using DSL just like SQL queries.

```kotlin
val books: List<Book> = queryFactory.listQuery {
    select(entity(Book::class))
    from(entity(Book::class))
    where(column(Book::author).equal("Shakespeare"))
}
```

If you want to select the DTO, select columns in the order of constructor parameters.

```kotlin
val books: List<Row> = queryFactory.listQuery {
    select(column(Book::author), count(column(Book::id)))
    from(entity(Book::class))
    groupBy(column(Book::author))
}
```

### Expression

Kotlin JDSL supports various expressions.

#### Aggregation

```kotlin
val max = max(column(Book::price))
val count = count(column(Book::price))
val greatest = greatest(column(Book::createdAt))
```

#### Case When

```kotlin
val case = case(
    `when`(column(Book::name).like("A%")).then(liternal(1)),
    `when`(column(Book::name).like("B%")).then(liternal(2)),
    // ...
    `else` = literal(999)
)
```

#### Subquery

```kotlin
val authorIds = queryFactory.subquery<Long> {
    select(column(Book::authorId))
    from(entity(Book::class))
    // ...
}

val authors: List<Author> = queryFactory.listQuery {
    // ...
    where(column(Author::id).`in`(authorIds))
}
```

### Predicate

Kotlin JDSL supports various predicates.

```kotlin
val condition = and(
    column(Book::author).equal("Shakespeare"),
    column(Book::price).lessThanOrEqualTo(100.toBigDecimal()),
    column(Book::status).`in`(SALE, OUT_OF_STOCK),
    column(Book::createdAt).between(Time.of("2001-01-01"), Time.of("2010-12-31")),
)
```

### Join

```kotlin
val books = queryFactory.listQuery<Book> {
    select(entity(Book::class))
    from(entity(Book::class))
    join(Book::author)
    // ...
}
```

#### Fetch

```kotlin
val books = queryFactory.listQuery<Book> {
    select(entity(Book::class))
    from(entity(Book::class))
    fetch(Book::author)
    // ...
}
```

If join and fetch are used together for the same entity, only fetch is applied.

```kotlin
val books = queryFactory.listQuery<Book> {
    select(entity(Book::class))
    from(entity(Book::class))
    join(Book::author) // Join is ignored
    fetch(Book::author) // Only fetch is applied
    // ...
}
```

#### Cross Join

```kotlin
val books = queryFactory.listQuery<Book> {
    select(entity(Book::class))
    from(entity(Book::class))
    join(entity(Author::class) on(column(Book::authorId).equal(column(Author::id))))
    // ...
}
```

#### Alias

There may be models with the two associations of same type. In this case, separate the Entity using alias.

```kotlin
val orders = queryFactory.listQuery<Order> {
    select(entity(Order::class))
    from(entity(Order::class))
    join(entity(Order::class), entity(Address::class, alias = "shippingAddress", on(Order::shippingAddress)))
    join(entity(Order::class), entity(Address::class, alias = "receiverAddress", on(Order::receiverAddress)))
    // ...
}
```

## How it works

Kotlin's property reference provides KProperty interface. KProperty is created in java file at kotlin
compile time. Since KProperty has the name of property, we can use it to write the expression of the Critical API.

If you type the JPA query as below,

```kotlin
queryFactory.listQuery<Book> {
    select(entity(Book::class))
    from(entity(Book::class))
    where(column(Book::name).equal("Hamlet").and(column(Book::author).equal("Shakespeare")))
}
```

Kotlin compiler creates PropertyReference.

```java
final class ClassKt$books$1 extends PropertyReference1Impl {
    public static final KProperty1 INSTANCE = new ClassKt$books$1();

    books$1() {
        super(Book.class, "name", "getName()Ljava/lang/String;", 0);
    }

    @Nullable
    public Object get(@Nullable Object receiver) {
        return ((Book) receiver).getName();
    }
}

final class ClassKt$books$2 extends PropertyReference1Impl {
    public static final KProperty1 INSTANCE = new ClassKt$books$2();

    ClassKt$books$2() {
        super(Book.class, "author", "getAuthor()Ljava/lang/String;", 0);
    }

    @Nullable
    public Object get(@Nullable Object receiver) {
        return ((Book) receiver).getAuthor();
    }
}
```

## Support

If you have any questions, please make Issues. And PR is always welcome.

## We Are Hiring
Are you ready to join us? - <https://careers.linecorp.com/ko/jobs/862>

## How to contribute
See [CONTRIBUTING](CONTRIBUTING.md).
If you believe you have discovered a vulnerability or have an issue related to security, please contact the maintainer directly or send us an [email](mailto:dl_oss_dev@linecorp.com) before sending a pull request.

## License
```
   Copyright 2021 LINE Corporation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
See [LICENSE](LICENSE) for more details.