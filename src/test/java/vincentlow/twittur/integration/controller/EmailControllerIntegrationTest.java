package vincentlow.twittur.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;

import vincentlow.twittur.integration.BaseIntegrationTest;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.EmailRequest;
import vincentlow.twittur.model.response.EmailResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.EmailRepository;
import vincentlow.twittur.service.CacheService;

public class EmailControllerIntegrationTest extends BaseIntegrationTest {

  private final String EMAIL_CONTROLLER_DIR = "email-controller";

  private final String EMAIL_REQUEST_JSON = "email-request";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private EmailRepository emailRepository;

  @Autowired
  private CacheService cacheService;

  private Account account;

  @BeforeEach
  void setUp() {

    account = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account1", new TypeReference<>() {});
  }

  @AfterEach
  void tearDown() {

    cacheService.flushAll();
    emailRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void sendEmail_success() throws Exception {

    EmailRequest emailRequest = getRequestFromPath(EMAIL_CONTROLLER_DIR, EMAIL_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<EmailResponse> expectation =
        getExpectationFromPath(EMAIL_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    emailRequest.setRecipient(account.getEmailAddress());

    MvcResult result = mockMvc.perform(post(ApiPath.EMAIL).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(emailRequest)))
        .andReturn();

    ApiSingleResponse<EmailResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void sendEmail_accountNotFound_failed() throws Exception {

    EmailRequest emailRequest = getRequestFromPath(EMAIL_CONTROLLER_DIR, EMAIL_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<EmailResponse> expectation =
        getExpectationFromPath(EMAIL_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    emailRequest.setRecipient(UNKNOWN_EMAIL_ADDRESS);

    MvcResult result = mockMvc.perform(post(ApiPath.EMAIL).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(emailRequest)))
        .andReturn();

    ApiSingleResponse<EmailResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }
}
