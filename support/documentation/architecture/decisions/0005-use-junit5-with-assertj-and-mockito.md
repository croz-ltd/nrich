# Use JUnit 5 with AssertJ and Mockito

- Status: accepted
- Decision makers: agrancaric
- Date: 2020-05-11

## Context and Problem Statement

We need a consistent way of writing our tests, it's important to make tests easy to write and readable. Which frameworks should we use?

## Considered Options

- Spock
- JUnit 5 with Mockito and builtin assertions
- JUnit 5 with Mockito and AssertJ

## Decision Outcome

We have decided to use JUnit 5 with AssertJ and Mockito libraries for writing tests. JUnit is a standard library that is used extensively across the Java ecosystem and AssertJ will help us with the
part of JUnit we're not satisfied with (assertions). Since JUnit lacks the mocking inside the library Mockito will be used for mocking. Overall, JUnit 5 has come a long way from JUnit 4 and combined
with AssertJ and Mockito can make writing tests almost as easy as Spock without the need of introducing another language to the project and with better tooling support and extensions.

### Positive Consequences

- Wide adoption
- Familiar syntax
- Good tooling support
- Easy onboarding of new developers
- Fluid assertions
- Good integration with other libraries through use of extensions (`@ExtendWith`)
- Easy writing of parametrized tests

### Negative Consequences

- Relying on third party libraries instead of using the functionality provided by JUnit in case of assertions
- Using third party library for mocking
- Less concise syntax compared to Spock (requires use of `@Test` annotations, parametrized tests are still more verbose)

## Pros and Cons of the Options

### Spock

Pros
- Concise syntax
- Easy to read test method names (i.e. `should revolve validation message`)
- Easier to write parametrized tests
- All the necessary parts builtin (assertions, mocking)

Cons
- Introducing another language to code base (Groovy)
- Worse tooling support
- Not all the frameworks used have extensions for Spock unlike for JUnit
- Less fluent and powerful assertions than AssertJ

### JUnit 5 with Mockito and builtin assertions

Pros
- No need to rely on third party library for assertions

Cons
- Builtin assertions are lacking functionality
- Builtin assertions are not as readable

## Links

- [Spock](https://spockframework.org/)
- [JUnit 5](https://junit.org/junit5/)
- [Spock vs JUnit 5 - the ultimate feature comparison](https://blog.solidsoft.pl/2020/04/15/spock-vs-junit-5-the-ultimate-feature-comparison/)
