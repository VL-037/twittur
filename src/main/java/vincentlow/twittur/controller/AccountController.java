package vincentlow.twittur.controller;

import static vincentlow.twittur.utils.ObjectMappingHelper.toResponse;
import static vincentlow.twittur.utils.ValidatorUtil.validatePageableRequest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.UpdateAccountEmailRequest;
import vincentlow.twittur.model.request.UpdateAccountPasswordRequest;
import vincentlow.twittur.model.request.UpdateAccountPhoneNumberRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.AccountFollowerResponse;
import vincentlow.twittur.model.response.AccountResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;
import vincentlow.twittur.service.AccountService;

@Slf4j
@RestController
@RequestMapping(value = ApiPath.ACCOUNT, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController extends BaseController {

  @Autowired
  private AccountService accountService;

  @GetMapping
  public ResponseEntity<ApiListResponse<AccountResponse>> getAccounts(
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    try {
      validatePageableRequest(pageNumber, pageSize);

      Page<Account> accounts = accountService.findAccounts(pageNumber, pageSize);
      List<AccountResponse> response = accounts.stream()
          .map(account -> toResponse(account, AccountResponse.class))
          .collect(Collectors.toList());
      PageMetaData pageMetaData = getPageMetaData(accounts, pageNumber, pageSize);

      return toSuccessResponseEntity(toApiListResponse(response, pageMetaData));
    } catch (Exception e) {
      log.error("#getAccounts ERROR! with pageNumber: {}, pageSize: {}, and error: {}", pageNumber, pageSize,
          e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @GetMapping("/@{username}")
  public ResponseEntity<ApiSingleResponse<AccountResponse>> getAccountByUsername(
      @PathVariable("username") String username) {

    try {
      Account account = accountService.findAccountByUsername(username);
      AccountResponse response = toResponse(account, AccountResponse.class);

      return toSuccessResponseEntity(toApiSingleResponse(response));
    } catch (RuntimeException e) {
      log.error("#getAccountByUsername ERROR! with username: {}, and error: {}", username, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PutMapping("/@{username}")
  public ResponseEntity<ApiResponse> updateAccount(@PathVariable("username") String username,
      @RequestBody UpdateAccountRequest request) {

    try {
      accountService.updateAccountByUsername(username, request);
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#updateAccount ERROR! with username: {}, request: {}, and error: {}", username, request,
          e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PutMapping("/@{username}/emailAddress")
  public ResponseEntity<ApiResponse> updateAccountEmail(@PathVariable("username") String username,
      @RequestBody UpdateAccountEmailRequest request) {

    try {
      accountService.updateAccountEmailAddressByUsername(username, request);
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#updateAccountEmail ERROR! with username: {}, request: {}, and error: {}", username, request,
          e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PutMapping("/@{username}/phoneNumber")
  public ResponseEntity<ApiResponse> updateAccountPhoneNumber(@PathVariable("username") String username,
      @RequestBody UpdateAccountPhoneNumberRequest request) {

    try {
      accountService.updateAccountPhoneNumberByUsername(username, request);
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#updateAccountPhoneNumber ERROR! with username: {}, request: {}, and error: {}", username, request,
          e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PutMapping("/@{username}/password")
  public ResponseEntity<ApiResponse> updateAccountPassword(@PathVariable("username") String username,
      @RequestBody UpdateAccountPasswordRequest request) {

    try {
      accountService.updateAccountPasswordByUsername(username, request);
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#updateAccountPassword ERROR! with username: {}, request: {}, and error: {}", username, request,
          e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/init")
  public ResponseEntity<ApiResponse> initDummyAccounts() {

    try {
      accountService.initDummyAccounts();
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#initDummyAccounts ERROR! with error: {}", e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/_follow")
  public ResponseEntity<ApiResponse> followAccount(@RequestBody AccountRelationshipRequest request) {

    try {
      accountService.follow(request);
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#followAccount ERROR! with request: {}, and error: {}", request, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/_unfollow")
  public ResponseEntity<ApiResponse> unfollowAccount(@RequestBody AccountRelationshipRequest request) {

    try {
      accountService.unfollow(request);
      return toSuccessResponseEntity(successResponse);
    } catch (RuntimeException e) {
      log.error("#unfollowAccount ERROR! with request: {}, and error: {}", request, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @GetMapping("/@{username}/followers")
  public ApiListResponse<AccountFollowerResponse> getAccountFollowers(@PathVariable String username,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    try {
      validatePageableRequest(pageNumber, pageSize);

      Page<Account> followers = accountService.getAccountFollowers(username, pageNumber, pageSize);
      List<AccountFollowerResponse> response = followers.stream()
          .map(account -> toResponse(account, AccountFollowerResponse.class))
          .collect(Collectors.toList());
      PageMetaData pageMetaData = getPageMetaData(followers, pageNumber, pageSize);

      return toApiListResponse(response, pageMetaData);
    } catch (RuntimeException e) {
      log.error("#getAccountFollowers ERROR! with username: {}, pageNumber: {}, pageSize: {}, and error: {}", username,
          pageNumber, pageSize, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @GetMapping("/@{username}/following")
  public ApiListResponse<AccountFollowerResponse> getAccountFollowing(@PathVariable String username,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize) {

    try {
      validatePageableRequest(pageNumber, pageSize);

      Page<Account> followers = accountService.getAccountFollowing(username, pageNumber, pageSize);
      List<AccountFollowerResponse> response = followers.stream()
          .map(account -> toResponse(account, AccountFollowerResponse.class))
          .collect(Collectors.toList());
      PageMetaData pageMetaData = getPageMetaData(followers, pageNumber, pageSize);

      return toApiListResponse(response, pageMetaData);
    } catch (RuntimeException e) {
      log.error("#getAccountFollowing ERROR! with username: {}, pageNumber: {}, pageSize: {}, and error: {}", username,
          pageNumber, pageSize, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
