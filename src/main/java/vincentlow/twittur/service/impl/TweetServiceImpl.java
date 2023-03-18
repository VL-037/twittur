package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;
import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.util.StringUtils;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.response.exception.NotFoundException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.TweetRepository;
import vincentlow.twittur.service.AccountService;
import vincentlow.twittur.service.TweetService;

@Service
public class TweetServiceImpl implements TweetService {

  private final String DUMMY_REQUESTS_PATH = "dummy_requests/tweets.json";

  @Autowired
  private AccountService accountService;

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public Page<Tweet> findAccountTweets(String username, int pageNumber, int pageSize) {

    Account account;
    try {
      account = accountService.findAccountByUsername(username);
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }

    validateAccount(account);

    try {
      return tweetRepository.findAllByCreatorId(account.getId(), PageRequest.of(pageNumber, pageSize));
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  @Override
  public Tweet findAccountTweetById(String username, String tweetId) {

    Account account;
    try {
      account = accountService.findAccountByUsername(username);
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }

    validateAccount(account);

    try {
      return tweetRepository.findById(tweetId)
          .orElseThrow(() -> new NotFoundException(ExceptionMessage.TWEET_NOT_FOUND));
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  @Override
  public Tweet createTweet(String username, CreateTweetRequest request) {

    Tweet tweet = convertToTweet(request);

    validateState(Objects.nonNull(tweet), ErrorCode.TWEET_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(tweet.getMessage()),
        ErrorCode.MESSAGE_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(tweet.getMessage()
        .length() <= 250, ErrorCode.MESSAGE_MAXIMAL_LENGTH_IS_250.getMessage());

    Account account;
    try {
      account = accountService.findAccountByUsername(username);
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }

    validateAccount(account);

    try {
      tweet.setCreator(account);
      tweet.setCreatedBy(account.getId());
      tweet.setUpdatedBy(account.getId());

      account.setTweetsCount(account.getTweetsCount() + 1);
      accountRepository.save(account);

      return tweetRepository.save(tweet);
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  @Override
  public void initDummyTweets(String username) {

    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource requestJson = new ClassPathResource(DUMMY_REQUESTS_PATH);

    Account account;
    try {
      account = accountService.findAccountByUsername(username);
    } catch (Exception e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }

    validateAccount(account);

    try {
      List<CreateTweetRequest> requests = mapper.readValue(requestJson.getInputStream(), new TypeReference<>() {});
      List<Tweet> tweets = requests.stream()
          .map(request -> convertToTweet(request))
          .collect(Collectors.toList());

      tweets.forEach(tweet -> {
        tweet.setCreator(account);
        tweet.setCreatedBy(account.getId());
        tweet.setUpdatedBy(account.getId());
      });

      List<Tweet> saved = tweetRepository.saveAll(tweets);
      account.setTweetsCount(account.getTweetsCount() + saved.size());
      accountRepository.save(account);
    } catch (IOException e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  private Tweet convertToTweet(CreateTweetRequest request) {

    Tweet tweet = new Tweet();
    tweet.prePersist();
    tweet.setMessage(request.getMessage());
    return tweet;
  }
}
