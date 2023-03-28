package vincentlow.twittur.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;

import vincentlow.twittur.model.constant.KafkaConstant;
import vincentlow.twittur.model.constant.NotificationType;
import vincentlow.twittur.model.request.PushNotificationRequest;

public class KafkaPublisherServiceImplTest {

  private final String SENDER_ID = "SENDER_ID";

  private final String TITLE = "TITLE";

  private final String MESSAGE = "MESSAGE";

  private final String IMAGE_URL = "IMAGE_URL";

  private final NotificationType NOTIFICATION_TYPE = NotificationType.NEW_TWEET;

  private final String REDIRECT_URL = "REDIRECT_URL";

  @InjectMocks
  private KafkaPublisherServiceImpl kafkaPublisherService;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  private PushNotificationRequest pushNotificationRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    pushNotificationRequest = new PushNotificationRequest();
    pushNotificationRequest.setSenderId(SENDER_ID);
    pushNotificationRequest.setTitle(TITLE);
    pushNotificationRequest.setMessage(MESSAGE);
    pushNotificationRequest.setImageUrl(IMAGE_URL);
    pushNotificationRequest.setType(NOTIFICATION_TYPE);
    pushNotificationRequest.setRedirectUrl(REDIRECT_URL);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(kafkaTemplate);
  }

  @Test
  void pushNotification() {

    kafkaPublisherService.pushNotification(pushNotificationRequest);

    verify(kafkaTemplate).send(KafkaConstant.PUSH_NOTIFICATION, pushNotificationRequest);
  }
}
