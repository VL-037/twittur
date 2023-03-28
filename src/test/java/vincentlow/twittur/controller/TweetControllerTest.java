package vincentlow.twittur.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.request.UpdateTweetRequest;
import vincentlow.twittur.service.TweetService;

public class TweetControllerTest {

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 5;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String MESSAGE = "MESSAGE";

  private final String USERNAME = "USERNAME";

  private final String TWEET_ID = "TWEET_ID";

  private final String TWEET_API_PATH = ApiPath.ACCOUNT + "/@" + USERNAME + "/tweets";

  @InjectMocks
  private TweetController tweetController;

  @Mock
  private TweetService tweetService;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  private Map<String, String> params;

  private MultiValueMap<String, String> multiValueParams;

  private HttpStatus httpStatus;

  private Tweet tweet;

  private List<Tweet> tweetList;

  private Page<Tweet> tweetPage;

  private CreateTweetRequest createTweetRequest;

  private UpdateTweetRequest updateTweetRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);
    mockMvc = standaloneSetup(tweetController).build();

    objectMapper = new ObjectMapper();

    httpStatus = HttpStatus.OK;

    tweet = new Tweet();
    tweet.setMessage(MESSAGE);

    tweetList = new ArrayList<>();
    tweetList.add(tweet);

    tweetPage = new PageImpl<>(tweetList, PAGE_REQUEST, tweetList.size());

    createTweetRequest = CreateTweetRequest.builder()
        .message(MESSAGE)
        .build();

    updateTweetRequest = UpdateTweetRequest.builder()
        .message(MESSAGE)
        .build();

    params = new HashMap<>();
    params.put("pageNumber", String.valueOf(PAGE_NUMBER));
    params.put("pageSize", String.valueOf(PAGE_SIZE));

    multiValueParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      multiValueParams.add(entry.getKey(), entry.getValue());
    }

    when(tweetService.findAccountTweets(USERNAME, PAGE_NUMBER, PAGE_SIZE)).thenReturn(tweetPage);
    when(tweetService.findAccountTweetById(USERNAME, TWEET_ID)).thenReturn(tweet);
    when(tweetService.createTweet(USERNAME, createTweetRequest)).thenReturn(tweet);
    doNothing().when(tweetService)
        .initDummyTweets(USERNAME);
    when(tweetService.updateAccountTweet(USERNAME, TWEET_ID, updateTweetRequest)).thenReturn(tweet);
    doNothing().when(tweetService)
        .deleteAccountTweet(USERNAME, TWEET_ID);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(tweetService);
  }

  @Test
  void getAccountTweets() throws Exception {

    this.mockMvc.perform(get(TWEET_API_PATH).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .queryParams(multiValueParams))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.content[0].message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(tweetService).findAccountTweets(USERNAME, PAGE_NUMBER, PAGE_SIZE);
  }

  @Test
  void getAccountTweetById() throws Exception {

    this.mockMvc
        .perform(get(TWEET_API_PATH + "/" + TWEET_ID).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(tweetService).findAccountTweetById(USERNAME, TWEET_ID);
  }

  @Test
  void postTweet() throws Exception {

    this.mockMvc.perform(post(TWEET_API_PATH).accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createTweetRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(tweetService).createTweet(USERNAME, createTweetRequest);
  }

  @Test
  void initDummyTweets() throws Exception {

    this.mockMvc.perform(post(TWEET_API_PATH + "/init").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(tweetService).initDummyTweets(USERNAME);
  }

  @Test
  void updateAccountTweet() throws Exception {

    this.mockMvc
        .perform(put(TWEET_API_PATH + "/" + TWEET_ID).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateTweetRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.message", equalTo(MESSAGE)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(tweetService).updateAccountTweet(USERNAME, TWEET_ID, updateTweetRequest);
  }

  @Test
  void deleteAccountTweet() throws Exception {

    this.mockMvc
        .perform(delete(TWEET_API_PATH + "/" + TWEET_ID).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(tweetService).deleteAccountTweet(USERNAME, TWEET_ID);
  }
}
