package vincentlow.twittur.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.response.AccountResponse;

public class ObjectMappingHelperTest {

  private Account account;

  @BeforeEach
  void setUp() {

    account = new Account();
  }

  @AfterEach
  void tearDown() {}

  @Test
  void toResponse() {

    AccountResponse result = ObjectMappingHelper.toResponse(account, AccountResponse.class);

    assertNotNull(result);
    assertEquals(result.getClass(), AccountResponse.class);
  }

  @Test
  void toResponse_nullSource() {

    AccountResponse result = ObjectMappingHelper.toResponse(null, AccountResponse.class);

    assertNull(result);
  }
}
