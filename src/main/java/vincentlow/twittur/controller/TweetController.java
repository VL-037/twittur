package vincentlow.twittur.controller;

import static vincentlow.twittur.utils.ObjectMappingHelper.toResponse;
import static vincentlow.twittur.utils.ValidatorUtil.validatePageableRequest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateTweetRequest;
import vincentlow.twittur.model.response.TweetResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;
import vincentlow.twittur.service.TweetService;

@RestController
@RequestMapping(value = "/api/v1/accounts/@{username}/tweets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TweetController extends BaseController {

  @Autowired
  private TweetService tweetService;

  @GetMapping
  public ApiListResponse<TweetResponse> getAccountTweets(@PathVariable String username,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "5") int pageSize) {

    validatePageableRequest(pageNumber, pageSize);

    Page<Tweet> tweets = tweetService.findAccountTweets(username, pageNumber, pageSize);
    List<TweetResponse> response = tweets.stream()
        .map(tweet -> toResponse(tweet, TweetResponse.class))
        .collect(Collectors.toList());
    PageMetaData pageMetaData = getPageMetaData(tweets, pageNumber, pageSize);

    return toSuccessApiResponse(response, pageMetaData);
  }

  @GetMapping("/{tweetId}")
  public ApiSingleResponse<TweetResponse> getAccountTweetById(@PathVariable String username,
      @PathVariable String tweetId) {

    Tweet tweet = tweetService.findAccountTweetById(username, tweetId);
    TweetResponse response = toResponse(tweet, TweetResponse.class);

    return toSuccessApiResponse(response);
  }

  @PostMapping
  public ApiSingleResponse<TweetResponse> postTweet(@PathVariable String username,
      @RequestBody CreateTweetRequest request) {

    Tweet tweet = tweetService.createTweet(username, request);
    TweetResponse response = toResponse(tweet, TweetResponse.class);

    return toSuccessApiResponse(response);
  }

  @PostMapping("/init")
  public ApiResponse initDummyTweets(@PathVariable String username) {

    tweetService.initDummyTweets(username);
    return successResponse;
  }
}
