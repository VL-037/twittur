package vincentlow.twittur.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.constant.NotificationType;
import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.model.request.GetNotificationRequest;
import vincentlow.twittur.service.NotificationService;

public class NotificationControllerTest {

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String TITLE = "TITLE";

  private final String MESSAGE = "MESSAGE";

  private final String IMAGE_URL = "IMAGE_URL";

  private final String REDIRECT_URL = "REDIRECT_URL";

  private final NotificationType NOTIFICATION_TYPE = NotificationType.NEW_TWEET;

  private final boolean HAS_READ = false;

  private final String RECIPIENT_ID = "RECIPIENT_ID";

  @InjectMocks
  private NotificationController notificationController;

  @Mock
  private NotificationService notificationService;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  private Map<String, String> params;

  private MultiValueMap<String, String> multiValueParams;

  private HttpStatus httpStatus;

  private Notification notification;

  private List<Notification> notificationList;

  private Page<Notification> notificationPage;

  private GetNotificationRequest getNotificationRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);
    mockMvc = standaloneSetup(notificationController).build();

    objectMapper = new ObjectMapper();

    httpStatus = HttpStatus.OK;

    notification = new Notification();
    notification.setTitle(TITLE);
    notification.setMessage(MESSAGE);
    notification.setImageUrl(IMAGE_URL);
    notification.setRedirectUrl(REDIRECT_URL);
    notification.setType(NOTIFICATION_TYPE);
    notification.setHasRead(HAS_READ);

    notificationList = new ArrayList<>();
    notificationList.add(notification);

    notificationPage = new PageImpl<>(notificationList, PAGE_REQUEST, notificationList.size());

    getNotificationRequest = GetNotificationRequest.builder()
        .recipientId(RECIPIENT_ID)
        .build();

    params = new HashMap<>();
    params.put("pageNumber", String.valueOf(PAGE_NUMBER));

    multiValueParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      multiValueParams.add(entry.getKey(), entry.getValue());
    }

    when(notificationService.getNotifications(eq(RECIPIENT_ID), eq(PAGE_NUMBER), anyInt()))
        .thenReturn(notificationPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(notificationService);
  }

  @Test
  void getNotifications() throws Exception {

    this.mockMvc
        .perform(
            get(ApiPath.NOTIFICATION).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getNotificationRequest))
                .queryParams(multiValueParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.content[0].title", equalTo(TITLE)))
        .andExpect(jsonPath("$.content[0].message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.content[0].imageUrl", equalTo(IMAGE_URL)))
        .andExpect(jsonPath("$.content[0].redirectUrl", equalTo(REDIRECT_URL)))
        .andExpect(jsonPath("$.content[0].type", equalTo(NOTIFICATION_TYPE.toString())))
        .andExpect(jsonPath("$.content[0].hasRead", equalTo(HAS_READ)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(notificationService).getNotifications(RECIPIENT_ID, PAGE_NUMBER, PAGE_SIZE);
  }
}
