package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.repository.NotificationRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;

public class NotificationServiceImplTest {

  private String RECIPIENT_ID = "RECIPIENT_ID";

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  private Account recipient;

  private Notification notification;

  private List<Notification> notificationList;

  private Page<Notification> notificationPage;

  @BeforeEach
  void setUp() {

    openMocks(this);

    recipient = new Account();
    recipient.setId(RECIPIENT_ID);

    notification = new Notification();
    notification.setRecipient(recipient);

    notificationList = new ArrayList<>();
    notificationList.add(notification);

    notificationPage = new PageImpl<>(notificationList, PAGE_REQUEST, notificationList.size());

    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(RECIPIENT_ID)).thenReturn(recipient);
    when(notificationRepository.findAllByRecipientIdOrderByCreatedDateDesc(RECIPIENT_ID, PAGE_REQUEST))
        .thenReturn(notificationPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(notificationRepository, accountRepositoryService);
  }

  @Test
  void getNotifications() {

    Page<Notification> result = notificationService.getNotifications(RECIPIENT_ID, PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(RECIPIENT_ID);
    verify(notificationRepository).findAllByRecipientIdOrderByCreatedDateDesc(RECIPIENT_ID, PAGE_REQUEST);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
