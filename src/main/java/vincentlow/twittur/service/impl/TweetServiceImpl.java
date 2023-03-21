package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;
import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;
import static vincentlow.twittur.utils.ValidatorUtil.validateTweet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
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
import vincentlow.twittur.model.constant.NotificationType;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.request.PushNotificationRequest;
import vincentlow.twittur.model.request.UpdateTweetRequest;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.TweetRepository;
import vincentlow.twittur.service.KafkaPublisherService;
import vincentlow.twittur.service.TweetService;

@Service
public class TweetServiceImpl implements TweetService {

  private final String DUMMY_REQUESTS_PATH = "dummy_requests/tweets.json";

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private KafkaPublisherService kafkaPublisherService;

  @Override
  public Page<Tweet> findAccountTweets(String username, int pageNumber, int pageSize) {

    Account account = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    return tweetRepository.findAllByCreatorIdAndMarkForDeleteFalse(account.getId(),
        PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Tweet findAccountTweetById(String username, String tweetId) {

    Account account = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    Tweet tweet = tweetRepository.findByIdAndMarkForDeleteFalse(tweetId);
    return validateTweet(tweet, ExceptionMessage.TWEET_NOT_FOUND);
  }

  @Override
  public Tweet createTweet(String username, CreateTweetRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getMessage()),
        ErrorCode.MESSAGE_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getMessage()
        .length() <= 250, ErrorCode.MESSAGE_MAXIMAL_LENGTH_IS_250.getMessage());

    Account creator = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(creator, ExceptionMessage.ACCOUNT_NOT_FOUND);

    Tweet tweet = new Tweet();
    BeanUtils.copyProperties(request, tweet);

    LocalDateTime now = LocalDateTime.now();
    tweet.setCreator(creator);
    tweet.setCreatedBy(creator.getId());
    tweet.setCreatedDate(now);
    tweet.setUpdatedBy(creator.getId());
    tweet.setUpdatedDate(now);

    creator.setTweetsCount(creator.getTweetsCount() + 1);
    accountRepository.save(creator);

    tweet = tweetRepository.save(tweet);

    String notificationTitle = String.format("%s Tweeted:", creator.getAccountName());
    String notificationMessage = String.format(tweet.getMessage());
    String notificationImageUrl = "IMAGE_URL";
    String notificationRedirectUrl = String.format("/@%s/tweets/%s", creator.getUsername(), tweet.getId());

    PushNotificationRequest pushNotificationRequest = PushNotificationRequest.builder()
        .senderId(creator.getId())
        .title(notificationTitle)
        .message(notificationMessage)
        .imageUrl(notificationImageUrl)
        .redirectUrl(notificationRedirectUrl)
        .type(NotificationType.NEW_TWEET)
        .build();

    kafkaPublisherService.pushNotification(pushNotificationRequest);
    return tweet;
  }

  @Override
  public void initDummyTweets(String username) {

    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource requestJson = new ClassPathResource(DUMMY_REQUESTS_PATH);

    Account account = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    try {
      List<CreateTweetRequest> requests = mapper.readValue(requestJson.getInputStream(), new TypeReference<>() {});
      List<Tweet> tweets = requests.stream()
          .map(request -> convertToTweet(request))
          .collect(Collectors.toList());

      LocalDateTime now = LocalDateTime.now();
      tweets.forEach(tweet -> {
        tweet.setCreator(account);
        tweet.setCreatedBy(account.getId());
        tweet.setCreatedDate(now);
        tweet.setUpdatedBy(account.getId());
        tweet.setUpdatedDate(now);
      });

      List<Tweet> saved = tweetRepository.saveAll(tweets);
      account.setTweetsCount(account.getTweetsCount() + saved.size());
      accountRepository.save(account);
    } catch (IOException e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  @Override
  public Tweet updateAccountTweet(String username, String tweetId, UpdateTweetRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getMessage()),
        ErrorCode.MESSAGE_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getMessage()
        .length() <= 250, ErrorCode.MESSAGE_MAXIMAL_LENGTH_IS_250.getMessage());

    Account account = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    Tweet tweet = tweetRepository.findByIdAndMarkForDeleteFalse(tweetId);
    validateTweet(tweet, ExceptionMessage.TWEET_NOT_FOUND);

    BeanUtils.copyProperties(request, tweet);
    tweet.setUpdatedBy(account.getId());
    tweet.setUpdatedDate(LocalDateTime.now());

    return tweetRepository.save(tweet);
  }

  @Override
  public void deleteAccountTweet(String username, String tweetId) {

    Account account = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    Tweet tweet = tweetRepository.findByIdAndMarkForDeleteFalse(tweetId);
    validateTweet(tweet, ExceptionMessage.TWEET_NOT_FOUND);

    tweet.setMarkForDelete(true);
    tweet.setUpdatedBy(account.getId());
    tweet.setUpdatedDate(LocalDateTime.now());
    tweetRepository.save(tweet);
  }

  private Tweet convertToTweet(CreateTweetRequest request) {

    Tweet tweet = new Tweet();
    tweet.setMessage(request.getMessage());
    return tweet;
  }
}
