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

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Tweet;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.response.AccountResponse;
import vincentlow.twittur.model.response.TweetResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;
import vincentlow.twittur.service.AccountService;
import vincentlow.twittur.service.TweetService;

@RestController
@RequestMapping(value = "/api/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController extends BaseController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private TweetService tweetService;

  @GetMapping
  public ApiListResponse<AccountResponse> getAccounts(
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    validatePageableRequest(pageNumber, pageSize);

    Page<Account> accounts = accountService.findAccounts(pageNumber, pageSize);
    List<AccountResponse> response = accounts.stream()
        .map(account -> toResponse(account, AccountResponse.class))
        .collect(Collectors.toList());
    PageMetaData pageMetaData = getPageMetaData(accounts, pageNumber, pageSize);

    return toSuccessApiResponse(response, pageMetaData);
  }

  @GetMapping("/@{username}")
  public ApiSingleResponse<AccountResponse> getAccountByUsername(@PathVariable("username") String username,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    validatePageableRequest(pageNumber, pageSize);

    Account account = accountService.findAccountByUsername(username);
    Page<Tweet> tweets = tweetService.findAccountTweets(username, pageNumber, pageSize);

    AccountResponse response = toResponse(account, AccountResponse.class);

    List<TweetResponse> tweetResponses = tweets.getContent()
        .stream()
        .map(tweet -> toResponse(tweet, TweetResponse.class))
        .collect(Collectors.toList());
    response.setTweets(tweetResponses);

    return toSuccessApiResponse(response);
  }

  @PostMapping
  public ApiSingleResponse<AccountResponse> createAccount(
      @RequestBody CreateAccountRequest request) {

    Account account = accountService.createAccount(request);
    AccountResponse response = toResponse(account, AccountResponse.class);
    return toSuccessApiResponse(response);
  }

  @PostMapping("/init")
  public ApiResponse initDummyAccounts() {

    accountService.initDummyAccounts();
    return successResponse;
  }
}
