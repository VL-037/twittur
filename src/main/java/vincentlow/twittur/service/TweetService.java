package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;

public interface TweetService {

  Page<Tweet> findAccountTweets(String username, int pageNumber, int pageSize);

  Tweet findAccountTweetById(String username, String tweetId);

  Tweet createTweet(String username, CreateTweetRequest request);

  void initDummyTweets(String username);
}
