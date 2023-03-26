package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
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

import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.request.UpdateTweetRequest;
import vincentlow.twittur.model.response.TweetResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.service.TweetService;

public class TweetControllerTest {

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 5;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String MESSAGE = "MESSAGE";

  private final String USERNAME = "USERNAME";

  private final String TWEET_ID = "TWEET_ID";

  @InjectMocks
  private TweetController tweetController;

  @Mock
  private TweetService tweetService;

  private Tweet tweet;

  private List<Tweet> tweetList;

  private Page<Tweet> tweetPage;

  private CreateTweetRequest createTweetRequest;

  private UpdateTweetRequest updateTweetRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

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
  void getAccountTweets() {

    ApiListResponse<TweetResponse> result = tweetController.getAccountTweets(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(tweetService).findAccountTweets(USERNAME, PAGE_NUMBER, PAGE_SIZE);

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

  @Test
  void getAccountTweetById() {

    ApiSingleResponse<TweetResponse> result = tweetController.getAccountTweetById(USERNAME, TWEET_ID);

    verify(tweetService).findAccountTweetById(USERNAME, TWEET_ID);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(MESSAGE, result.getData()
        .getMessage());
  }

  @Test
  void postTweet() {

    ApiSingleResponse<TweetResponse> result = tweetController.postTweet(USERNAME, createTweetRequest);

    verify(tweetService).createTweet(USERNAME, createTweetRequest);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(MESSAGE, result.getData()
        .getMessage());
  }

  @Test
  void initDummyTweets() {

    ApiResponse result = tweetController.initDummyTweets(USERNAME);

    verify(tweetService).initDummyTweets(USERNAME);

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }

  @Test
  void updateAccountTweet() {

    ApiSingleResponse<TweetResponse> result =
        tweetController.updateAccountTweet(USERNAME, TWEET_ID, updateTweetRequest);

    verify(tweetService).updateAccountTweet(USERNAME, TWEET_ID, updateTweetRequest);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(MESSAGE, result.getData()
        .getMessage());
  }

  @Test
  void deleteAccountTweet() {

    ApiResponse result = tweetController.deleteAccountTweet(USERNAME, TWEET_ID);

    verify(tweetService).deleteAccountTweet(USERNAME, TWEET_ID);

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }
}
