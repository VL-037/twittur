package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;
import vincentlow.twittur.model.response.DirectMessageResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.service.DirectMessageService;

public class DirectMessageControllerTest {

  private final String SENDER_ID = "SENDER_ID";

  private final String RECIPIENT_ID = "RECIPIENT_ID";

  private final String MESSAGE = "MESSAGE";

  private final int PAGE_NUMBER = 0;

  @InjectMocks
  private DirectMessageController directMessageController;

  @Mock
  private DirectMessageService directMessageService;

  private DirectMessage directMessage;

  private List<DirectMessage> directMessageList;

  private DirectMessageRequest directMessageRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    Account sender = new Account();
    sender.setId(SENDER_ID);

    Account recipient = new Account();
    recipient.setId(RECIPIENT_ID);

    directMessage = new DirectMessage();
    directMessage.setSender(sender);
    directMessage.setRecipient(recipient);
    directMessage.setMessage(MESSAGE);

    directMessageList = new ArrayList<>();
    directMessageList.add(directMessage);

    directMessageRequest = DirectMessageRequest.builder()
        .message(MESSAGE)
        .build();

    when(directMessageService.sendMessage(SENDER_ID, RECIPIENT_ID, directMessageRequest)).thenReturn(directMessage);
    when(directMessageService.getDirectMessages(eq(SENDER_ID), eq(RECIPIENT_ID), eq(PAGE_NUMBER), anyInt()))
        .thenReturn(directMessageList);
  }

  @AfterEach
  void tearDown() {}

  @Test
  void sendDirectMessage() {

    ApiSingleResponse<DirectMessageResponse> result =
        directMessageController.sendDirectMessage(SENDER_ID, RECIPIENT_ID, directMessageRequest);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(SENDER_ID, result.getData()
        .getSenderId());
    assertEquals(RECIPIENT_ID, result.getData()
        .getRecipientId());
    assertEquals(MESSAGE, result.getData()
        .getMessage());
  }

  @Test
  void getDirectMessages() {

    ApiListResponse<DirectMessageResponse> result =
        directMessageController.getDirectMessages(SENDER_ID, RECIPIENT_ID, PAGE_NUMBER);

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
