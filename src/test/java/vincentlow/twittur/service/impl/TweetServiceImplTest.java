package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.request.PushNotificationRequest;
import vincentlow.twittur.model.request.UpdateTweetRequest;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.TweetRepository;
import vincentlow.twittur.service.KafkaPublisherService;

public class TweetServiceImplTest {

  private String CREATOR_ID = "CREATOR_ID";

  private String USERNAME = "USERNAME";

  private String TWEET_ID = "TWEET_ID";

  private String MESSAGE = "MESSAGE";

  @InjectMocks
  private TweetServiceImpl tweetService;

  @Mock
  private TweetRepository tweetRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private KafkaPublisherService kafkaPublisherService;

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private Account creator;

  private Tweet tweet;

  private List<Tweet> tweetList;

  private Page<Tweet> tweetPage;

  private CreateTweetRequest createTweetRequest;

  private UpdateTweetRequest updateTweetRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    creator = new Account();
    creator.setId(CREATOR_ID);
    creator.setUsername(USERNAME);

    tweet = new Tweet();
    tweet.setId(TWEET_ID);
    tweet.setMessage(MESSAGE);
    tweet.setCreator(creator);

    tweetList = new ArrayList<>();
    tweetList.add(tweet);

    tweetPage = new PageImpl<>(tweetList, pageRequest, tweetList.size());

    createTweetRequest = CreateTweetRequest.builder()
        .message(MESSAGE)
        .build();

    updateTweetRequest = UpdateTweetRequest.builder()
        .message(MESSAGE)
        .build();

    when(accountRepository.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(creator);
    when(tweetRepository.findAllByCreatorIdAndMarkForDeleteFalse(CREATOR_ID, pageRequest)).thenReturn(tweetPage);

    when(tweetRepository.findByIdAndMarkForDeleteFalse(TWEET_ID)).thenReturn(tweet);

    when(accountRepository.save(any(Account.class))).thenReturn(creator);
    when(tweetRepository.save(any(Tweet.class))).thenReturn(tweet);
    doNothing().when(kafkaPublisherService)
        .pushNotification(any(PushNotificationRequest.class));

    when(tweetRepository.saveAll(anyList())).thenReturn(tweetList);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(tweetRepository, accountRepository, kafkaPublisherService);
  }

  @Test
  void findAccountTweets() {

    Page<Tweet> result = tweetService.findAccountTweets(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepository).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(tweetRepository).findAllByCreatorIdAndMarkForDeleteFalse(CREATOR_ID, pageRequest);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void findAccountTweetById() {

    Tweet result = tweetService.findAccountTweetById(USERNAME, TWEET_ID);

    verify(accountRepository).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(tweetRepository).findByIdAndMarkForDeleteFalse(TWEET_ID);

    assertNotNull(result);
    assertEquals(MESSAGE, result.getMessage());
    assertEquals(creator, result.getCreator());
  }

  @Test
  void createTweet() {

    Tweet result = tweetService.createTweet(USERNAME, createTweetRequest);

    verify(accountRepository).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepository).save(any(Account.class));
    verify(tweetRepository).save(any(Tweet.class));
    verify(kafkaPublisherService).pushNotification(any(PushNotificationRequest.class));

    assertNotNull(result);
    assertEquals(MESSAGE, result.getMessage());
    assertEquals(creator, result.getCreator());
  }

  @Test
  void initDummyTweets() {

    tweetService.initDummyTweets(USERNAME);

    verify(accountRepository).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(tweetRepository).saveAll(anyList());
    verify(accountRepository).save(any(Account.class));
  }

  @Test
  void updateAccountTweet() {

    Tweet result = tweetService.updateAccountTweet(USERNAME, TWEET_ID, updateTweetRequest);

    verify(accountRepository).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(tweetRepository).findByIdAndMarkForDeleteFalse(TWEET_ID);
    verify(tweetRepository).save(any(Tweet.class));

    assertNotNull(result);
    assertEquals(MESSAGE, result.getMessage());
    assertEquals(creator, result.getCreator());
  }

  @Test
  void deleteAccountTweet() {

    tweetService.deleteAccountTweet(USERNAME, TWEET_ID);

    verify(accountRepository).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(tweetRepository).findByIdAndMarkForDeleteFalse(TWEET_ID);
    verify(tweetRepository).save(any(Tweet.class));
  }
}
