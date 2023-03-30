package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.time.LocalDateTime;
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
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;
import vincentlow.twittur.repository.DirectMessageRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;

public class DirectMessageServiceImplTest {

  private String SENDER_ID = "SENDER_ID";

  private String RECIPIENT_ID = "RECIPIENT_ID";

  private String MESSAGE = "MESSAGE";

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  @InjectMocks
  private DirectMessageServiceImpl directMessageService;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  @Mock
  private DirectMessageRepository directMessageRepository;

  private DirectMessage senderToRecipientMessage;

  private DirectMessage recipientToSenderMessage;

  private Account sender;

  private Account recipient;

  private List<DirectMessage> senderToRecipientMessageList;

  private Page<DirectMessage> senderToRecipientMessagePage;

  private List<DirectMessage> recipientToSenderMessageList;

  private Page<DirectMessage> recipientToSenderMessagePage;

  private DirectMessageRequest directMessageRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    sender = new Account();
    sender.setId(SENDER_ID);

    recipient = new Account();
    recipient.setId(RECIPIENT_ID);

    senderToRecipientMessage = new DirectMessage();
    senderToRecipientMessage.setSender(sender);
    senderToRecipientMessage.setRecipient(recipient);
    senderToRecipientMessage.setMessage(MESSAGE);
    senderToRecipientMessage.setCreatedDate(LocalDateTime.now());

    senderToRecipientMessageList = new ArrayList<>();
    senderToRecipientMessageList.add(senderToRecipientMessage);

    senderToRecipientMessagePage =
        new PageImpl<>(senderToRecipientMessageList, PAGE_REQUEST, senderToRecipientMessageList.size());

    recipientToSenderMessage = new DirectMessage();
    recipientToSenderMessage.setSender(recipient);
    recipientToSenderMessage.setRecipient(sender);
    recipientToSenderMessage.setMessage(MESSAGE);
    recipientToSenderMessage.setCreatedDate(LocalDateTime.now());

    recipientToSenderMessageList = new ArrayList<>();
    recipientToSenderMessageList.add(recipientToSenderMessage);

    recipientToSenderMessagePage =
        new PageImpl<>(recipientToSenderMessageList, PAGE_REQUEST, recipientToSenderMessageList.size());

    directMessageRequest = DirectMessageRequest.builder()
        .message(MESSAGE)
        .build();

    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(SENDER_ID)).thenReturn(sender);
    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(RECIPIENT_ID)).thenReturn(recipient);
    when(directMessageRepository.save(any(DirectMessage.class))).thenReturn(senderToRecipientMessage);

    when(directMessageRepository.findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(SENDER_ID, RECIPIENT_ID,
        PAGE_REQUEST)).thenReturn(senderToRecipientMessagePage);
    when(directMessageRepository.findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(RECIPIENT_ID, SENDER_ID,
        PAGE_REQUEST)).thenReturn(recipientToSenderMessagePage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountRepositoryService, directMessageRepository);
  }

  @Test
  void sendMessage() {

    DirectMessage result = directMessageService.sendMessage(SENDER_ID, RECIPIENT_ID, directMessageRequest);

    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(SENDER_ID);
    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(RECIPIENT_ID);
    verify(directMessageRepository).save(any(DirectMessage.class));

    assertNotNull(result);
    assertEquals(sender, result.getSender());
    assertEquals(recipient, result.getRecipient());
    assertEquals(MESSAGE, result.getMessage());
  }

  @Test
  void getDirectMessages() {

    Page<DirectMessage> result =
        directMessageService.getDirectMessages(SENDER_ID, RECIPIENT_ID, PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(SENDER_ID);
    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(RECIPIENT_ID);
    verify(directMessageRepository).findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(SENDER_ID, RECIPIENT_ID,
        PAGE_REQUEST);
    verify(directMessageRepository).findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(RECIPIENT_ID, SENDER_ID,
        PAGE_REQUEST);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
