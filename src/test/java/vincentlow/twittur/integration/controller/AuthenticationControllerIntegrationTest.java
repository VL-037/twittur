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
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.LoginRequest;
import vincentlow.twittur.model.response.AuthenticationResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.TokenRepository;

public class AuthenticationControllerIntegrationTest extends BaseIntegrationTest {

  private final String AUTHENTICATION_CONTROLLER_DIR = "authentication-controller";

  private final String CREATE_ACCOUNT_REQUEST_JSON = "create-account-request";

  private final String LOGIN_REQUEST_JSON = "login-request";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TokenRepository tokenRepository;

  private Account account;

  @BeforeEach()
  public void setUp() {

    ignoredFields.add("accessToken");

    account = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account1", new TypeReference<>() {});
  }

  @AfterEach
  public void tearDown() {

    tokenRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void register_success() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void register_blankPhoneNumber_success() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void register_blankBio_success() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void register_usernameExists_failed() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account);

    createAccountRequest.setUsername(account.getUsername());

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.CONFLICT, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void register_emailAddressExists_failed() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account);

    createAccountRequest.setEmailAddress(account.getEmailAddress());

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.CONFLICT, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void login_success() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    LoginRequest loginRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, LOGIN_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    loginRequest.setUsername(createAccountRequest.getUsername());
    loginRequest.setPassword(createAccountRequest.getPassword());

    mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/login")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(loginRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void login_accountNotRegistered_failed() throws Exception {

    LoginRequest loginRequest =
        getRequestFromPath(AUTHENTICATION_CONTROLLER_DIR, LOGIN_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<AuthenticationResponse> expectation =
        getExpectationFromPath(AUTHENTICATION_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc.perform(post(ApiPath.AUTHENTICATION + "/login")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(loginRequest)))
        .andReturn();

    ApiSingleResponse<AuthenticationResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.FORBIDDEN, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

}
