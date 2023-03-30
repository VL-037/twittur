package vincentlow.twittur.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;
import vincentlow.twittur.model.response.DirectMessageResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.DirectMessageRepository;

public class DirectMessageControllerIntegrationTest extends BaseIntegrationTest {

  private final String DIRECT_MESSAGE_CONTROLLER_DIR = "direct-message-controller";

  private final String DIRECT_MESSAGE_REQUEST_JSON = "direct-message-request";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private DirectMessageRepository directMessageRepository;

  private Account account1;

  private Account account2;

  private DirectMessage directMessage1;

  private DirectMessage directMessage2;

  private DirectMessage directMessage3;

  private DirectMessage directMessage4;

  @BeforeEach
  public void setUp() {

    paginationParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      paginationParams.add(entry.getKey(), entry.getValue());
    }

    ignoredFields.add("senderId");
    ignoredFields.add("recipientId");

    account1 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account1", new TypeReference<>() {});
    account2 = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account2", new TypeReference<>() {});

    directMessage1 = getEntityFromPath(DIRECT_MESSAGE_ENTITY_DIR, "direct-message1", new TypeReference<>() {});
    directMessage2 = getEntityFromPath(DIRECT_MESSAGE_ENTITY_DIR, "direct-message2", new TypeReference<>() {});
    directMessage3 = getEntityFromPath(DIRECT_MESSAGE_ENTITY_DIR, "direct-message3", new TypeReference<>() {});
    directMessage4 = getEntityFromPath(DIRECT_MESSAGE_ENTITY_DIR, "direct-message4", new TypeReference<>() {});
  }

  @AfterEach
  public void tearDown() {

    directMessageRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void sendDirectMessage_success() throws Exception {

    DirectMessageRequest directMessageRequest =
        getRequestFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, DIRECT_MESSAGE_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<DirectMessageResponse> expectation =
        getExpectationFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, new TypeReference<>() {});

    account1 = accountRepository.save(account1);
    account2 = accountRepository.save(account2);

    MvcResult result = mockMvc.perform(post(ApiPath.DIRECT_MESSAGE + "/" + account1.getId() + "/" + account2.getId())
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(directMessageRequest)))
        .andReturn();

    ApiSingleResponse<DirectMessageResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void sendDirectMessage_senderNotFound_failed() throws Exception {

    DirectMessageRequest directMessageRequest =
        getRequestFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, DIRECT_MESSAGE_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<DirectMessageResponse> expectation =
        getExpectationFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, new TypeReference<>() {});

    account1 = accountRepository.save(account1);
    account2 = accountRepository.save(account2);

    MvcResult result = mockMvc.perform(post(ApiPath.DIRECT_MESSAGE + "/" + UNKNOWN_ID + "/" + account2.getId())
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(directMessageRequest)))
        .andReturn();

    ApiSingleResponse<DirectMessageResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void sendDirectMessage_recipientNotFound_failed() throws Exception {

    DirectMessageRequest directMessageRequest =
        getRequestFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, DIRECT_MESSAGE_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<DirectMessageResponse> expectation =
        getExpectationFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, new TypeReference<>() {});

    account1 = accountRepository.save(account1);
    account2 = accountRepository.save(account2);

    MvcResult result = mockMvc.perform(post(ApiPath.DIRECT_MESSAGE + "/" + account1.getId() + "/" + UNKNOWN_ID)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(directMessageRequest)))
        .andReturn();

    ApiSingleResponse<DirectMessageResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getDirectMessages_success() throws Exception {

    ApiListResponse<DirectMessageResponse> expectation =
        getExpectationFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, new TypeReference<>() {});

    account1 = accountRepository.save(account1);
    account2 = accountRepository.save(account2);

    directMessage1.setSender(account1);
    directMessage1.setRecipient(account2);

    directMessage2.setSender(account2);
    directMessage2.setRecipient(account1);

    directMessage3.setSender(account1);
    directMessage3.setRecipient(account2);

    directMessage4.setSender(account2);
    directMessage4.setRecipient(account1);

    List<DirectMessage> directMessageList = new ArrayList<>();
    directMessageList.add(directMessage1);
    directMessageList.add(directMessage2);
    directMessageList.add(directMessage3);
    directMessageList.add(directMessage4);

    directMessageList = directMessageRepository.saveAll(directMessageList);

    MvcResult result = mockMvc.perform(get(ApiPath.DIRECT_MESSAGE + "/" + account1.getId() + "/" + account2.getId())
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiListResponse<DirectMessageResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    successApiListResponseContentAssertion(response.getContent(), expectation.getContent());

    // assert recipient & sender
    for (int i = 0; i < directMessageList.size(); i++) {
      DirectMessage dm = directMessageList.get(i);
      DirectMessageResponse dmResp = response.getContent()
          .get(i);

      assertEquals(dm.getSender()
          .getId(), dmResp.getSenderId());
      assertEquals(dm.getRecipient()
          .getId(), dmResp.getRecipientId());
    }
  }

  @Test
  public void getDirectMessages_senderNotFound_failed() throws Exception {

    ApiListResponse<DirectMessageResponse> expectation =
        getExpectationFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, new TypeReference<>() {});

    account1 = accountRepository.save(account1);
    account2 = accountRepository.save(account2);

    directMessage1.setSender(account1);
    directMessage1.setRecipient(account2);

    directMessage2.setSender(account2);
    directMessage2.setRecipient(account1);

    directMessage3.setSender(account1);
    directMessage3.setRecipient(account2);

    directMessage4.setSender(account2);
    directMessage4.setRecipient(account1);

    List<DirectMessage> directMessageList = new ArrayList<>();
    directMessageList.add(directMessage1);
    directMessageList.add(directMessage2);
    directMessageList.add(directMessage3);
    directMessageList.add(directMessage4);

    directMessageList = directMessageRepository.saveAll(directMessageList);

    MvcResult result = mockMvc.perform(get(ApiPath.DIRECT_MESSAGE + "/" + UNKNOWN_ID + "/" + account2.getId())
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiListResponse<DirectMessageResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getDirectMessages_recipientNotFound_failed() throws Exception {

    ApiListResponse<DirectMessageResponse> expectation =
        getExpectationFromPath(DIRECT_MESSAGE_CONTROLLER_DIR, new TypeReference<>() {});

    account1 = accountRepository.save(account1);
    account2 = accountRepository.save(account2);

    directMessage1.setSender(account1);
    directMessage1.setRecipient(account2);

    directMessage2.setSender(account2);
    directMessage2.setRecipient(account1);

    directMessage3.setSender(account1);
    directMessage3.setRecipient(account2);

    directMessage4.setSender(account2);
    directMessage4.setRecipient(account1);

    List<DirectMessage> directMessageList = new ArrayList<>();
    directMessageList.add(directMessage1);
    directMessageList.add(directMessage2);
    directMessageList.add(directMessage3);
    directMessageList.add(directMessage4);

    directMessageList = directMessageRepository.saveAll(directMessageList);

    MvcResult result = mockMvc.perform(get(ApiPath.DIRECT_MESSAGE + "/" + account1.getId() + "/" + UNKNOWN_ID)
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiListResponse<DirectMessageResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }
}
