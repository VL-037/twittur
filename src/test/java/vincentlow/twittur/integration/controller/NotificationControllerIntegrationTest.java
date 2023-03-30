package vincentlow.twittur.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.model.request.GetNotificationRequest;
import vincentlow.twittur.model.response.NotificationResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.NotificationRepository;

public class NotificationControllerIntegrationTest extends BaseIntegrationTest {

  private final String NOTIFICATION_CONTROLLER_DIR = "notification-controller";

  private final String GET_NOTIFICATION_REQUEST_JSON = "get-notification-request";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private NotificationRepository notificationRepository;

  private Account account;

  private Notification notification1;

  private Notification notification2;

  private Notification notification3;

  @BeforeEach
  public void setUp() {

    paginationParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      paginationParams.add(entry.getKey(), entry.getValue());
    }

    account = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account1", new TypeReference<>() {});

    notification1 = getEntityFromPath(NOTIFICATION_ENTITY_DIR, "notification1", new TypeReference<>() {});
    notification2 = getEntityFromPath(NOTIFICATION_ENTITY_DIR, "notification2", new TypeReference<>() {});
    notification3 = getEntityFromPath(NOTIFICATION_ENTITY_DIR, "notification3", new TypeReference<>() {});
  }

  @AfterEach
  public void tearDown() {

    notificationRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void getNotifications_success() throws Exception {

    GetNotificationRequest getNotificationRequest =
        getRequestFromPath(NOTIFICATION_CONTROLLER_DIR, GET_NOTIFICATION_REQUEST_JSON, new TypeReference<>() {});
    ApiListResponse<NotificationResponse> expectation =
        getExpectationFromPath(NOTIFICATION_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    notification1.setRecipient(account);
    notification2.setRecipient(account);
    notification3.setRecipient(account);

    List<Notification> notificationList = new ArrayList<>();
    notificationList.add(notification1);
    notificationList.add(notification2);
    notificationList.add(notification3);

    notificationRepository.saveAll(notificationList);

    getNotificationRequest.setRecipientId(account.getId());

    MvcResult mvcResult = mockMvc.perform(get(ApiPath.NOTIFICATION).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(getNotificationRequest)))
        .andReturn();

    ApiListResponse<NotificationResponse> response = getMvcResponse(mvcResult, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    successApiListResponseContentAssertion(response.getContent(), expectation.getContent());
  }

  @Test
  public void getNotifications_recipientAccountNotFound_success() throws Exception {

    GetNotificationRequest getNotificationRequest =
        getRequestFromPath(NOTIFICATION_CONTROLLER_DIR, GET_NOTIFICATION_REQUEST_JSON, new TypeReference<>() {});
    ApiListResponse<NotificationResponse> expectation =
        getExpectationFromPath(NOTIFICATION_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    notification1.setRecipient(account);
    notification2.setRecipient(account);
    notification3.setRecipient(account);

    List<Notification> notificationList = new ArrayList<>();
    notificationList.add(notification1);
    notificationList.add(notification2);
    notificationList.add(notification3);

    notificationRepository.saveAll(notificationList);

    getNotificationRequest.setRecipientId(UNKNOWN_ID);

    MvcResult mvcResult = mockMvc.perform(get(ApiPath.NOTIFICATION).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(getNotificationRequest)))
        .andReturn();

    ApiListResponse<NotificationResponse> response = getMvcResponse(mvcResult, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }
}
