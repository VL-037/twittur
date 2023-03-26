package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.model.request.GetNotificationRequest;
import vincentlow.twittur.model.response.NotificationResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.service.NotificationService;

public class NotificationControllerTest {

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String RECIPIENT_ID = "RECIPIENT_ID";

  @InjectMocks
  private NotificationController notificationController;

  @Mock
  private NotificationService notificationService;

  private Notification notification;

  private List<Notification> notificationList;

  private Page<Notification> notificationPage;

  private GetNotificationRequest getNotificationRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    notification = new Notification();

    notificationList = new ArrayList<>();
    notificationList.add(notification);

    notificationPage = new PageImpl<>(notificationList, PAGE_REQUEST, notificationList.size());

    getNotificationRequest = GetNotificationRequest.builder()
        .recipientId(RECIPIENT_ID)
        .build();

    when(notificationService.getNotifications(eq(RECIPIENT_ID), eq(PAGE_NUMBER), anyInt()))
        .thenReturn(notificationPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(notificationService);
  }

  @Test
  void getNotifications() {

    ApiListResponse<NotificationResponse> result =
        notificationController.getNotifications(getNotificationRequest, PAGE_NUMBER);

    verify(notificationService).getNotifications(eq(RECIPIENT_ID), eq(PAGE_NUMBER), anyInt());

    assertNotNull(result);
    assertNotNull(result.getContent());
    assertFalse(result.getContent()
        .isEmpty());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertTrue(result.getContent()
        .size() > 0);
  }
}
