package vincentlow.twittur.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;
import vincentlow.twittur.service.DirectMessageService;

public class DirectMessageControllerTest {

  private final String SENDER_ID = "SENDER_ID";

  private final String RECIPIENT_ID = "RECIPIENT_ID";

  private final String MESSAGE = "MESSAGE";

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 4;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String DIRECT_MESSAGE_API_PATH = ApiPath.DIRECT_MESSAGE + "/" + SENDER_ID + "/" + RECIPIENT_ID;

  @InjectMocks
  private DirectMessageController directMessageController;

  @Mock
  private DirectMessageService directMessageService;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  private Map<String, String> params;

  private MultiValueMap<String, String> multiValueParams;

  private HttpStatus httpStatus;

  private DirectMessage directMessage;

  private List<DirectMessage> directMessageList;

  private Page<DirectMessage> directMessagePage;

  private DirectMessageRequest directMessageRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);
    mockMvc = standaloneSetup(directMessageController).build();

    objectMapper = new ObjectMapper();

    httpStatus = HttpStatus.OK;

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

    directMessagePage = new PageImpl<>(directMessageList, PAGE_REQUEST, directMessageList.size());

    directMessageRequest = DirectMessageRequest.builder()
        .message(MESSAGE)
        .build();

    params = new HashMap<>();
    params.put("pageNumber", String.valueOf(PAGE_NUMBER));

    multiValueParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      multiValueParams.add(entry.getKey(), entry.getValue());
    }

    when(directMessageService.sendMessage(SENDER_ID, RECIPIENT_ID, directMessageRequest)).thenReturn(directMessage);
    when(directMessageService.getDirectMessages(eq(SENDER_ID), eq(RECIPIENT_ID), eq(PAGE_NUMBER), anyInt()))
        .thenReturn(directMessagePage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(directMessageService);
  }

  @Test
  void sendDirectMessage() throws Exception {

    this.mockMvc
        .perform(
            post(DIRECT_MESSAGE_API_PATH).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(directMessageRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.senderId", equalTo(SENDER_ID)))
        .andExpect(jsonPath("$.data.recipientId", equalTo(RECIPIENT_ID)))
        .andExpect(jsonPath("$.data.message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(directMessageService).sendMessage(eq(SENDER_ID), eq(RECIPIENT_ID), any(DirectMessageRequest.class));
  }

  @Test
  void getDirectMessages() throws Exception {

    this.mockMvc
        .perform(
            get(DIRECT_MESSAGE_API_PATH).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParams(multiValueParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.content[0].senderId", equalTo(SENDER_ID)))
        .andExpect(jsonPath("$.content[0].recipientId", equalTo(RECIPIENT_ID)))
        .andExpect(jsonPath("$.content[0].message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(directMessageService).getDirectMessages(eq(SENDER_ID), eq(RECIPIENT_ID), eq(PAGE_NUMBER), anyInt());
  }
}
