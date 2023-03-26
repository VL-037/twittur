package vincentlow.twittur.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.repository.NotificationRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;

public class PushNotificationListenerTest {

  private final String TOPIC = "TOPIC";

  private final int PARTITION = 0;

  private final long OFFSET = 0;

  private final String KEY = "KEY";

  private final String VALUE = "VALUE";

  private final String ACCOUNT_ID = "ACCOUNT_ID";

  @InjectMocks
  private PushNotificationListener pushNotificationListener;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  private ConsumerRecord<String, String> record;

  private Notification notification;

  private Account follower;

  private List<Account> followerList;

  @BeforeEach
  void setUp() throws JsonProcessingException {

    openMocks(this);

    notification = new Notification();
    notification.setSenderId(ACCOUNT_ID);

    follower = new Account();

    followerList = new ArrayList<>();
    followerList.add(follower);

    record = new ConsumerRecord<>(TOPIC, PARTITION, OFFSET, KEY, VALUE);

    when(objectMapper.readValue(VALUE, Notification.class)).thenReturn(notification);

    when(accountRepositoryService.findAllFollowers(ACCOUNT_ID)).thenReturn(followerList);
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(objectMapper, notificationRepository, accountRepositoryService);
  }

  @Test
  void processPushNotification() throws JsonProcessingException {

    pushNotificationListener.processPushNotification(record);

    verify(objectMapper).readValue(record.value(), Notification.class);
    verify(accountRepositoryService).findAllFollowers(ACCOUNT_ID);
    verify(notificationRepository, times(followerList.size())).save(any(Notification.class));
  }
}
