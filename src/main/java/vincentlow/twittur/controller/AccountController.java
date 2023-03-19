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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.AccountResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;
import vincentlow.twittur.service.AccountService;

@RestController
@RequestMapping(value = "/api/v1/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController extends BaseController {

  @Autowired
  private AccountService accountService;

  @PostMapping
  public ApiSingleResponse<AccountResponse> createAccount(
      @RequestBody CreateAccountRequest request) {

    try {
      Account account = accountService.createAccount(request);
      AccountResponse response = toResponse(account, AccountResponse.class);

      return toSuccessApiResponse(response);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @GetMapping
  public ApiListResponse<AccountResponse> getAccounts(
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    try {
      validatePageableRequest(pageNumber, pageSize);

      Page<Account> accounts = accountService.findAccounts(pageNumber, pageSize);
      List<AccountResponse> response = accounts.stream()
          .map(account -> toResponse(account, AccountResponse.class))
          .collect(Collectors.toList());
      PageMetaData pageMetaData = getPageMetaData(accounts, pageNumber, pageSize);

      return toSuccessApiResponse(response, pageMetaData);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @GetMapping("/@{username}")
  public ApiSingleResponse<AccountResponse> getAccountByUsername(@PathVariable("username") String username) {

    try {
      Account account = accountService.findAccountByUsername(username);
      AccountResponse response = toResponse(account, AccountResponse.class);

      return toSuccessApiResponse(response);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PutMapping("/@{username}")
  public ApiSingleResponse<AccountResponse> updateAccount(@PathVariable("username") String username,
      @RequestBody UpdateAccountRequest request) {

    try {
      Account account = accountService.updateAccountByUsername(username, request);
      AccountResponse response = toResponse(account, AccountResponse.class);

      return toSuccessApiResponse(response);
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/init")
  public ApiResponse initDummyAccounts() {

    try {
      accountService.initDummyAccounts();
      return successResponse;
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/_follow")
  public ApiResponse followAccount(@RequestBody AccountRelationshipRequest request) {

    try {
      accountService.follow(request);
      return successResponse;
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/_unfollow")
  public ApiResponse unfollowAccount(@RequestBody AccountRelationshipRequest request) {

    try {
      accountService.unfollow(request);
      return successResponse;
    } catch (RuntimeException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
