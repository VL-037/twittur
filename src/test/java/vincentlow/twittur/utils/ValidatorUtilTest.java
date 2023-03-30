package vincentlow.twittur.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vincentlow.twittur.model.response.exception.BadRequestException;

public class ValidatorUtilTest {

  private String ERROR_MESSAGE = "ERROR_MESSAGE";

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  void validateArgument_throwsException() {

    Exception result =
        assertThrows(BadRequestException.class, () -> ValidatorUtil.validateArgument(false, ERROR_MESSAGE));

    assertNotNull(result);
    assertEquals(BadRequestException.class, result.getClass());
    assertEquals(ERROR_MESSAGE, result.getMessage());
  }

  @Test
  void validateState_throwsException() {

    Exception result =
        assertThrows(BadRequestException.class, () -> ValidatorUtil.validateState(false, ERROR_MESSAGE));

    assertNotNull(result);
    assertEquals(BadRequestException.class, result.getClass());
    assertEquals(ERROR_MESSAGE, result.getMessage());
  }
}
