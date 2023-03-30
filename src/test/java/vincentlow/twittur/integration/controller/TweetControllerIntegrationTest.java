package vincentlow.twittur.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;

import vincentlow.twittur.integration.BaseIntegrationTest;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.request.UpdateTweetRequest;
import vincentlow.twittur.model.response.TweetResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.TweetRepository;

public class TweetControllerIntegrationTest extends BaseIntegrationTest {

  private final String TWEET_CONTROLLER_DIR = "tweet-controller";

  private final String CREATE_TWEET_REQUEST_JSON = "create-tweet-request";

  private final String UPDATE_TWEET_REQUEST_JSON = "update-tweet-request";

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TweetRepository tweetRepository;

  private Account account;

  private Tweet tweet1;

  private Tweet tweet2;

  private Tweet tweet3;

  @BeforeEach
  public void setUp() {

    paginationParams = new LinkedMultiValueMap<>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      paginationParams.add(entry.getKey(), entry.getValue());
    }

    account = getEntityFromPath(ACCOUNT_ENTITY_DIR, "account1", new TypeReference<>() {});

    tweet1 = getEntityFromPath(TWEET_ENTITY_DIR, "tweet1", new TypeReference<>() {});
    tweet2 = getEntityFromPath(TWEET_ENTITY_DIR, "tweet2", new TypeReference<>() {});
    tweet3 = getEntityFromPath(TWEET_ENTITY_DIR, "tweet3", new TypeReference<>() {});
  }

  @AfterEach
  public void tearDown() {

    tweetRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void getAccountTweets_success() throws Exception {

    ApiListResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    List<Tweet> tweetList = new ArrayList<>();
    tweetList.add(tweet1);
    tweetList.add(tweet2);
    tweetList.add(tweet3);
    tweetList.forEach(tweet -> tweet.setCreator(account));
    tweetRepository.saveAll(tweetList);

    MvcResult result = mockMvc
        .perform(
            get(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets").accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .params(paginationParams))
        .andReturn();

    ApiListResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiListResponseAssertion(response, expectation);
    successApiListResponseContentAssertion(response.getContent(), expectation.getContent());
  }

  @Test
  public void getAccountTweets_accountNotFound_failed() throws Exception {

    ApiListResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    List<Tweet> tweetList = new ArrayList<>();
    tweetList.add(tweet1);
    tweetList.add(tweet2);
    tweetList.add(tweet3);
    tweetList.forEach(tweet -> tweet.setCreator(account));
    tweetRepository.saveAll(tweetList);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME).accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .params(paginationParams))
        .andReturn();

    ApiListResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccountTweetById_success() throws Exception {

    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    tweet1.setCreator(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets/" + tweet1.getId())
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void getAccountTweetById_accountNotFound_failed() throws Exception {

    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    tweet1.setCreator(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc.perform(get(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/tweets/" + tweet1.getId())
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void getAccountTweetById_tweetNotFound_failed() throws Exception {

    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    MvcResult result = mockMvc
        .perform(get(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets/" + UNKNOWN_ID)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void postTweet_success() throws Exception {

    CreateTweetRequest createTweetRequest =
        getRequestFromPath(TWEET_CONTROLLER_DIR, CREATE_TWEET_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    MvcResult result = mockMvc.perform(post(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createTweetRequest)))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void postTweet_accountNotFound_failed() throws Exception {

    CreateTweetRequest createTweetRequest =
        getRequestFromPath(TWEET_CONTROLLER_DIR, CREATE_TWEET_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);

    MvcResult result = mockMvc.perform(post(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/tweets")
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectToContentString(createTweetRequest)))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountTweet_success() throws Exception {

    UpdateTweetRequest updateTweetRequest =
        getRequestFromPath(TWEET_CONTROLLER_DIR, UPDATE_TWEET_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets/" + tweet1.getId())
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateTweetRequest)))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void updateAccountTweet_accountNotFound_failed() throws Exception {

    UpdateTweetRequest updateTweetRequest =
        getRequestFromPath(TWEET_CONTROLLER_DIR, UPDATE_TWEET_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/tweets/" + tweet1.getId())
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateTweetRequest)))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void updateAccountTweet_tweetNotFound_failed() throws Exception {

    UpdateTweetRequest updateTweetRequest =
        getRequestFromPath(TWEET_CONTROLLER_DIR, UPDATE_TWEET_REQUEST_JSON, new TypeReference<>() {});
    ApiSingleResponse<TweetResponse> expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc
        .perform(put(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets/" + UNKNOWN_ID)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToContentString(updateTweetRequest)))
        .andReturn();

    ApiSingleResponse<TweetResponse> response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void deleteAccountTweet_success() throws Exception {

    ApiResponse expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc
        .perform(delete(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets/" + tweet1.getId())
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void deleteAccountTweet_accountNotFound_failed() throws Exception {

    ApiResponse expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc
        .perform(delete(ApiPath.ACCOUNT + "/@" + UNKNOWN_USERNAME + "/tweets/" + tweet1.getId())
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }

  @Test
  public void deleteAccountTweet_tweetNotFound_failed() throws Exception {

    ApiResponse expectation =
        getExpectationFromPath(TWEET_CONTROLLER_DIR, new TypeReference<>() {});

    account = accountRepository.save(account);
    tweet1 = tweetRepository.save(tweet1);

    MvcResult result = mockMvc
        .perform(delete(ApiPath.ACCOUNT + "/@" + account.getUsername() + "/tweets/" + UNKNOWN_ID)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiResponse response = getMvcResponse(result, new TypeReference<>() {});

    baseErrorApiResponseAssertion(HttpStatus.NOT_FOUND, response);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectation);
  }
}
