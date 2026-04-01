# spring-boot-starter-page-jackson3

Auto-configuration for Jackson 3 deserialization of Spring Data `Page` objects in Spring Boot 4.

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.stevenpg/spring-boot-starter-page-jackson3)](https://central.sonatype.com/artifact/com.stevenpg/spring-boot-starter-page-jackson3)
[![javadoc](https://javadoc.io/badge2/com.stevenpg/spring-boot-starter-page-jackson3/javadoc.svg)](https://javadoc.io/doc/com.stevenpg/spring-boot-starter-page-jackson3)
[![Java 17+](https://img.shields.io/badge/Java-17+-blue)](https://adoptium.net/)

## The Problem

Spring Data's built-in `PageModule` only handles **serialization** of `Page` objects. When you consume paginated REST responses via `RestClient` or `HttpServiceProxyFactory`, Jackson 3 cannot deserialize JSON into `Page` because:

- `Page` is an interface — Jackson can't instantiate it
- `PageImpl` has no `@JsonCreator` constructor

This causes: `Type definition error: [simple type, class org.springframework.data.domain.Page]`

## Quick Start

**1. Add the dependency:**

```groovy
// Gradle
implementation 'com.stevenpg:spring-boot-starter-page-jackson3:0.0.1'
```

```kotlin
// Gradle (Kotlin DSL)
implementation("com.stevenpg:spring-boot-starter-page-jackson3:0.0.1")
```

```xml
<!-- Maven -->
<dependency>
    <groupId>com.stevenpg</groupId>
    <artifactId>spring-boot-starter-page-jackson3</artifactId>
    <version>0.0.1</version>
</dependency>
```

**2. Use `Page` as a return type — it just works:**

```java
@HttpExchange("/api/users")
interface UserClient {
    @GetExchange
    Page<User> getUsers(Pageable pageable);
}
```

That's it. The auto-configuration activates automatically when `spring-data-commons` and `spring-boot-jackson` are on your classpath. Zero configuration required.

## How It Works

1. `PageJackson3AutoConfiguration` registers a `JsonMapperBuilderCustomizer` bean
2. The customizer adds a `@JsonDeserialize(as = RestPage.class)` mixin on `Page.class`
3. `RestPage<T>` extends `PageImpl<T>` with a null-safe `@JsonCreator` constructor

### Handled JSON Formats

Standard Spring Data REST pagination envelope:

```json
{
  "content": [{"name": "Alice"}, {"name": "Bob"}],
  "number": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

Null and missing page metadata fields are handled gracefully (defaults to page 0, size 1, 0 total).

## Requirements

| Dependency          | Version |
|---------------------|---------|
| Spring Boot         | 4.0+    |
| Spring Data Commons | 4.0+    |
| Jackson             | 3.x     |
| Java                | 17+     |

## Contributing

Contributions are welcome!

### Getting Started

#### Prerequisites

- Java 17
- Gradle 8+

#### Development Setup

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/spring-boot-starter-page-jackson3.git
   cd spring-boot-starter-page-jackson3
   ```
3. Build the project:
   ```bash
   ./gradlew build
   ```
4. Run tests to ensure everything works:
   ```bash
   ./gradlew test
   ```

### Contributing Guidelines

- For bug fixes, submit a PR directly with a clear description
- For new features or significant changes, open an issue first to discuss the approach
- Ensure all tests pass before submitting
- Write meaningful commit messages

### Publishing

Run `./gradlew clean build publish jreleaserFullRelease` to publish a new version to Maven Central.
Requires JReleaser credentials configured in `$HOME/.jreleaser/config.properties`.

## License

Apache License 2.0
