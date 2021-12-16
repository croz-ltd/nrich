package net.croz.nrich.validation.constraint.demo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Demo test introduced for easier definition of branch protection rules.
 *
 * TODO jzrilic: delete later
 */
public class DemoTest {

  @SuppressWarnings("ConstantConditions")
  @Test
  void shouldFail() {
    // expect
    assertThat("demo").isEmpty();
  }
}
