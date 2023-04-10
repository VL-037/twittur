package vincentlow.twittur.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;

import vincentlow.twittur.integration.BaseIntegrationTest;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountEmailRequest;
import vincentlow.twittur.model.request.UpdateAccountPasswordRequest;
import vincentlow.twittur.model.request.UpdateAccountPhoneNumberRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.AccountResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.repository.AccountRelationshipRepository;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.service.CacheService;

public class AccountControllerIntegrationTest extends BaseIntegrationTest {

  private final String ACCOUNT_CONTROLLER_DIR = "account-controller";

  private final String CREATE_ACCOUNT_REQUEST_JSON = "create-account-request";

  private final String UPDATE_ACCOUNT_REQUEST_JSON = "update-account-request";

  private final String UPDATE_ACCOUNT_EMAIL_REQUEST_JSON = "update-account-email-request";

  private final String UPDATE_ACCOUNT_PHONE_NUMBER_REQUEST_JSON = "update-account-phone-number-request";

  private final String UPDATE_ACCOUNT_PASSWORD_REQUEST_JSON = "update-account-password-request";

  private final String ACCOUNT_RELATIONSHIP_REQUEST_JSON = "account-relationship-request";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AccountRelationshipRepository accountRelationshipRepository;

  @Autowired
  private CacheService cacheService;

  private Account account1;

  private Account account2;

  private Account account3;

  private Account account4;

  private Account account5;

  private AccountRelationship accountRelationship1;

  private AccountRelationship accountRelationship2;

  @BeforeEach
  public void setUp() {

    paginationParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      paginationParams.add(entry.getKey(), entry.getValue());
    }

    account1 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account1", new TypeReference<>() {});
    account2 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account2", new TypeReference<>() {});
    account3 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account3", new TypeReference<>() {});
    account4 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account4", new TypeReference<>() {});
    account5 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account5", new TypeReference<>() {});
    accountRelationship1 =
        getEntityFromPath(ACCOUNT_RELATIONSHIP_ENTITY_DIR, "account-relationship1", new TypeReference<>() {});
    accountRelationship2 =
        getEntityFromPath(ACCOUNT_RELATIONSHIP_ENTITY_DIR, "account-relationship2", new TypeReference<>() {});
  }

  @AfterEach
  public void tearDown() {

    cacheService.flushAll();
    accountRelationshipRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void getAccounts_success() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    List<Account> accountList = new ArrayList<>();
    accountList.add(account1);
    accountList.add(account2);
    accountList.add(account3);
    accountList.add(account4);
    accountList.add(account5);
    accountRepository.saveAll(accountList);

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    successApiListResponseContentAssertion(response.getContent(), expectation.getContent());
  }

  @Test
  public void getAccounts_emptyContent_success() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    assertTrue(response.getContent()
        .isEmpty());
  }

  @Test
  public void getAccounts_pageNumberLessThan0_failed() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    paginationParams.set("pageNumber", "-1");

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.BAD_REQUEST, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccounts_pageSizeLessThan1_failed() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    paginationParams.set("pageSize", "0");

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.BAD_REQUEST, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccounts_pageSizeMoreThan100_failed() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    paginationParams.set("pageSize", "101");

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.BAD_REQUEST, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccountByUsername_success() throws Exception {

    ApiSingleResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + account1.getUsername()).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void getAccountByUsername_accountNotFound_failed() throws Exception {

    ApiSingleResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccount_success() throws Exception {

    UpdateAccountRequest updateAccountRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account1.getUsername()).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccount_accountNotFound_failed() throws Exception {

    UpdateAccountRequest updateAccountRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccount_usernameIsTaken_failed() throws Exception {

    UpdateAccountRequest updateAccountRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);
    accountRepository.save(account2);

    updateAccountRequest.setUsername(account2.getUsername());

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account1.getUsername()).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.CONFLICT, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountEmail_success() throws Exception {

    UpdateAccountEmailRequest updateAccountEmailRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_EMAIL_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account1.getUsername() + "/emailAddress")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountEmailRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountEmail_accountNotFound_failed() throws Exception {

    UpdateAccountEmailRequest updateAccountEmailRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_EMAIL_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/emailAddress")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountEmailRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountEmail_emailAddressIsTaken_failed() throws Exception {

    UpdateAccountEmailRequest updateAccountEmailRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_EMAIL_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);
    accountRepository.save(account2);

    updateAccountEmailRequest.setEmailAddress(account2.getEmailAddress());

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account1.getUsername() + "/emailAddress")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountEmailRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.CONFLICT, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountPhoneNumber_success() throws Exception {

    UpdateAccountPhoneNumberRequest updateAccountPhoneNumberRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_PHONE_NUMBER_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account1.getUsername() + "/phoneNumber")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountPhoneNumberRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountPhoneNumber_blankPhoneNumber_success() throws Exception {

    UpdateAccountPhoneNumberRequest updateAccountPhoneNumberRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_PHONE_NUMBER_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    accountRepository.save(account1);

    updateAccountPhoneNumberRequest.setPhoneNumber("");

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account1.getUsername() + "/phoneNumber")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountPhoneNumberRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountPhoneNumber_accountNotFound_failed() throws Exception {

    UpdateAccountPhoneNumberRequest updateAccountPhoneNumberRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_PHONE_NUMBER_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/phoneNumber")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountPhoneNumberRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountPassword_success() throws Exception {

    CreateAccountRequest createAccountRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, CREATE_ACCOUNT_REQUEST_JSON, new TypeReference<>() {});

    mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createAccountRequest)))
        .andReturn();

    UpdateAccountPasswordRequest updateAccountPasswordRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_PASSWORD_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + createAccountRequest.getUsername() + "/password")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountPasswordRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountPassword_accountNotFound_failed() throws Exception {

    UpdateAccountPasswordRequest updateAccountPasswordRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, UPDATE_ACCOUNT_PASSWORD_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/password")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateAccountPasswordRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void followAccount_success() throws Exception {

    AccountRelationshipRequest accountRelationshipRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, ACCOUNT_RELATIONSHIP_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account followed = accountRepository.save(account1);
    Account follower = accountRepository.save(account2);

    accountRelationshipRequest.setFollowerId(follower.getId());
    accountRelationshipRequest.setFollowedId(followed.getId());

    MvcResult result = mockMvc
        .perform(post(ApiPath.ACCOUNT + "/_follow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(accountRelationshipRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void followAccount_followerAccountNotFound_failed() throws Exception {

    AccountRelationshipRequest accountRelationshipRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, ACCOUNT_RELATIONSHIP_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account followed = accountRepository.save(account1);

    accountRelationshipRequest.setFollowerId(UNKNOWN_ID);
    accountRelationshipRequest.setFollowedId(followed.getId());

    MvcResult result = mockMvc
        .perform(post(ApiPath.ACCOUNT + "/_follow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(accountRelationshipRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void followAccount_followedAccountNotFound_failed() throws Exception {

    AccountRelationshipRequest accountRelationshipRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, ACCOUNT_RELATIONSHIP_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account follower = accountRepository.save(account2);

    accountRelationshipRequest.setFollowerId(follower.getId());
    accountRelationshipRequest.setFollowedId(UNKNOWN_ID);

    MvcResult result = mockMvc
        .perform(post(ApiPath.ACCOUNT + "/_follow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(accountRelationshipRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void unfollowAccount_success() throws Exception {

    AccountRelationshipRequest accountRelationshipRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, ACCOUNT_RELATIONSHIP_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account follower = accountRepository.save(account2);
    Account followed = accountRepository.save(account1);

    accountRelationship1.setFollower(follower);
    accountRelationship1.setFollowed(followed);
    AccountRelationship relationship = accountRelationshipRepository.save(accountRelationship1);

    accountRelationshipRequest.setFollowerId(relationship.getFollower()
        .getId());
    accountRelationshipRequest.setFollowedId(relationship.getFollowed()
        .getId());

    MvcResult result = mockMvc
        .perform(post(ApiPath.ACCOUNT + "/_unfollow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(accountRelationshipRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void unfollowAccount_followerAccountNotFound_failed() throws Exception {

    AccountRelationshipRequest accountRelationshipRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, ACCOUNT_RELATIONSHIP_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account followed = accountRepository.save(account1);

    accountRelationship1.setFollowed(followed);
    AccountRelationship relationship = accountRelationshipRepository.save(accountRelationship1);

    accountRelationshipRequest.setFollowerId(UNKNOWN_ID);
    accountRelationshipRequest.setFollowedId(relationship.getFollowed()
        .getId());

    MvcResult result = mockMvc
        .perform(post(ApiPath.ACCOUNT + "/_unfollow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(accountRelationshipRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void unfollowAccount_followedAccountNotFound_failed() throws Exception {

    AccountRelationshipRequest accountRelationshipRequest =
        getRequestFromPath(ACCOUNT_CONTROLLER_DIR, ACCOUNT_RELATIONSHIP_REQUEST_JSON, new TypeReference<>() {});
    ApiResponse expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account follower = accountRepository.save(account2);

    accountRelationship1.setFollower(follower);
    AccountRelationship relationship = accountRelationshipRepository.save(accountRelationship1);

    accountRelationshipRequest.setFollowerId(relationship.getFollower()
        .getId());
    accountRelationshipRequest.setFollowedId(UNKNOWN_ID);

    MvcResult result = mockMvc
        .perform(post(ApiPath.ACCOUNT + "/_unfollow")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(accountRelationshipRequest)))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccountFollowers_success() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account account = accountRepository.save(account1);
    Account follower1 = accountRepository.save(account2);
    Account follower2 = accountRepository.save(account3);

    accountRelationship1.setFollower(follower1);
    accountRelationship1.setFollowed(account);

    accountRelationship2.setFollower(follower2);
    accountRelationship2.setFollowed(account);

    accountRelationshipRepository.save(accountRelationship1);
    accountRelationshipRepository.save(accountRelationship2);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/followers")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    successApiListResponseContentAssertion(response.getContent(), expectation.getContent());
  }

  @Test
  public void getAccountFollowers_accountNotFound_failed() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account account = accountRepository.save(account1);
    Account follower1 = accountRepository.save(account2);
    Account follower2 = accountRepository.save(account3);

    accountRelationship1.setFollower(follower1);
    accountRelationship1.setFollowed(account);

    accountRelationship2.setFollower(follower2);
    accountRelationship2.setFollowed(account);

    accountRelationshipRepository.save(accountRelationship1);
    accountRelationshipRepository.save(accountRelationship2);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/followers")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccountFollowing_success() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account account = accountRepository.save(account1);
    Account following1 = accountRepository.save(account2);
    Account following2 = accountRepository.save(account3);

    accountRelationship1.setFollower(account);
    accountRelationship1.setFollowed(following1);

    accountRelationship2.setFollower(account);
    accountRelationship2.setFollowed(following2);

    accountRelationshipRepository.save(accountRelationship1);
    accountRelationshipRepository.save(accountRelationship2);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/following")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    successApiListResponseContentAssertion(response.getContent(), expectation.getContent());
  }

  @Test
  public void getAccountFollowing_accountNotFound_failed() throws Exception {

    ApiListResponse<AccountResponse> expectation =
        getExpectationFromPath(ACCOUNT_CONTROLLER_DIR, new TypeReference<>() {});

    Account account = accountRepository.save(account1);
    Account following1 = accountRepository.save(account2);
    Account following2 = accountRepository.save(account3);

    accountRelationship1.setFollower(account);
    accountRelationship1.setFollowed(following1);

    accountRelationship2.setFollower(account);
    accountRelationship2.setFollowed(following2);

    accountRelationshipRepository.save(accountRelationship1);
    accountRelationshipRepository.save(accountRelationship2);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/following")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .params(paginationParams))
        .andReturn();

    ApiListResponse<AccountResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }
}
