# Test structure

- Status: accepted
- Decision makers: agrancaric
- Date: 2020-05-11

## Context and Problem Statement

We need a consistent way of writing our tests, it's important to make tests readable and easy to modify. How should our test look like?

## Decision Outcome

To be able to write all tests in a consistent way we decided on some general rules for writing tests:

- Test method names will describe the behaviour being tested, they should start with the verb `should` after which the behaviour being tested is described. Some examples of correct method names
  are: `shouldNotEncryptNullData`, `shouldEncryptAndDecryptData`, `shouldThrowEncryptionOperationFailedExceptionWhenEncryptingInvalidData`
- The code inside tests will be seperated by following comments: `given`, `when`, `then`. If there are multiple cases in the test then continuation form will be used with `and then` while other
  comments will be without `and`. If there is no `when` then `expect` form will be used.
- If longer than a couple of lines generation of data for tests should be extracted in an util class with suffix `GeneratingUtil`
- All tests and methods will be without public modifier
- Mocking setup for the class being tested will use `@ExtendWith(MockitoExtension.class)`, `@Mock` and `@InjectMocks` annotations (if possible)
- Mocking of responses will start with `doReturn`, `doAnswer` etc.
- Unit tests will be used to cover most cases while integration test should also be present for final verification
- Code and branch coverage should be above 90%
- We will use static imports without prefix in tests

Some examples of valid tests are given bellow for reference:

```java

@ExtendWith(MockitoExtension.class)
class PoiCellHolderTest {

    @Mock
    private Cell cell;

    @InjectMocks
    private PoiCellHolder poiCellHolder;

    @Test
    void shouldReturnColumnIndex() {
        // given
        Integer value = 1;
        doReturn(value).when(cell).getColumnIndex();

        // when
        Integer result = poiCellHolder.getColumnIndex();

        // then
        assertThat(result).isEqualTo(value);
    }
}

@SpringJUnitConfig(EncryptTestConfiguration.class)
class EncryptDataAspectTest {

    private static final String VALUE_TO_ENCRYPT = "some text";

    @Autowired
    private EncryptDataAspectTestService encryptDataAspectTestService;

    @Test
    void shouldEncryptAndDecryptData() {
        // when
        EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncrypt(VALUE_TO_ENCRYPT);

        // then
        assertThat(result.getValue()).isNotEqualTo(VALUE_TO_ENCRYPT);

        // and when
        EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(VALUE_TO_ENCRYPT);
    }
}

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
class MessageSourceFieldErrorMessageResolverServiceTest {

    @Autowired
    private MessageSourceFieldErrorMessageResolverService fieldErrorMessageResolverService;

    @Test
    void shouldResolveValidationMessageForConstrainedProperty() {
        // given
        ConstrainedProperty constrainedProperty = createConstrainedProperty(FormConfigurationServiceNestedTestRequest.class);

        // when
        String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, Locale.ENGLISH);

        // then
        assertThat(message).isEqualTo("Invalid value");
    }
}

```

### Positive Consequences

- Consistent syntax
- Readable tests
- Helps onboarding of new developers

### Negative Consequences

- More verbose since it requires util classes for generation
- Since we're using comments to structure test methods they can be easily forgotten
- Can take some time getting used to
