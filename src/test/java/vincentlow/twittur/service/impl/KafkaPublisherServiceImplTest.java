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
import vincentlow.twittur.model.request.PushNotificationRequest;

public class KafkaPublisherServiceImplTest {

  @InjectMocks
  private KafkaPublisherServiceImpl kafkaPublisherService;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  private PushNotificationRequest pushNotificationRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    pushNotificationRequest = PushNotificationRequest.builder()
        .build();
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
