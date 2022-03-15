# Use Java version 8

- Status: accepted
- Decision makers: agrancaric, jzrilic, mfolnovic, pzrinscak
- Date: 2020-05-11

## Context and Problem Statement

We would like a language that is familiar to most of the developers in our company and whose usage won't impede adoption of these libraries. We are limiting our choices to JVM languages. Which JVM
language should be chosen?

## Considered Options

- Groovy
- Kotlin
- Java 8
- Latest Java version

## Decision Outcome

We've chosen Java version 8 since that appears to be the most used version across our company and the rest of the JVM ecosystem (most notably Spring Framework but other popular frameworks also).
Since the introduction of lambdas Java has become easier to use, and it won't slow down our development much and static typing and tooling support will enable us to produce better code. It will also
give us a larger number of potential contributors. Using version 8 means our library will be able to be used on most of the projects.

### Positive Consequences

- Wide adoption
- Familiar syntax
- Good tooling support
- Easy onboarding of new developers

### Negative Consequences

- Lots of boilerplate code in comparison with other considered JVM languages (getters/setters, constructors, public modifiers etc.)
- Slower development

## Pros and Cons of the Options

### Groovy

Pros
- Concise syntax
- Known inside the team
- Syntactically similar to Java

Cons
- Dynamically typed (although that can be resolved by using @CompileStatic annotations across the code base)
- Worse tooling support than other considered languages
- Lower adoption than Java and lately even Kotlin
- Seen as more of a scripting language which might limit usage
- Possible issues with building native images (GraalVM)

### Kotlin

Pros
- Concise syntax
- Statically typed
- Tooling support
- Easier development of reactive code with coroutines
- Built-in null safety

Cons
- Lower adoption than Java
- Lower knowledge base inside the team and company compared to other considered languages
- Syntax defers significantly from Java, making the onboarding process slower

### Latest Java version

Pros
- It will make development easier since latest language features aim to reduce boilerplate code

Cons
- Most of the projects use lower Java versions and that will limit adoption of these libraries.

## Links

- [Java InfoQ Trends Report—September 2020](https://www.infoq.com/articles/java-jvm-trends-2020/)
- [Kotlin](https://kotlinlang.org/)
- [Groovy](https://groovy-lang.org/)
