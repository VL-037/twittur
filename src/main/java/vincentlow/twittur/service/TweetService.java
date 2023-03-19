package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.request.UpdateTweetRequest;

public interface TweetService {

  Page<Tweet> findAccountTweets(String username, int pageNumber, int pageSize);

  Tweet findAccountTweetById(String username, String tweetId);

  Tweet createTweet(String username, CreateTweetRequest request);

  void initDummyTweets(String username);

  Tweet updateAccountTweet(String username, String tweetId, UpdateTweetRequest request);

  void deleteAccountTweet(String username, String tweetId);
}
