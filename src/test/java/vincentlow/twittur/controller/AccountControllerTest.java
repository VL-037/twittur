package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.http.HttpStatus;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.AccountFollowerResponse;
import vincentlow.twittur.model.response.AccountResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.service.AccountService;

public class AccountControllerTest {

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String USERNAME = "USERNAME";

  @InjectMocks
  private AccountController accountController;

  @Mock
  private AccountService accountService;

  private Account account;

  private List<Account> accountList;

  private Page<Account> accountPage;

  private CreateAccountRequest createAccountRequest;

  private UpdateAccountRequest updateAccountRequest;

  private AccountRelationship accountRelationship;

  private AccountRelationshipRequest accountRelationshipRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    account = new Account();
    account.setUsername(USERNAME);

    accountList = new ArrayList<>();
    accountList.add(account);

    accountPage = new PageImpl<>(accountList, PAGE_REQUEST, accountList.size());

    createAccountRequest = CreateAccountRequest.builder()
        .build();

    updateAccountRequest = UpdateAccountRequest.builder()
        .build();

    accountRelationship = new AccountRelationship();

    when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(account);
    when(accountService.findAccounts(PAGE_NUMBER, PAGE_SIZE)).thenReturn(accountPage);
    when(accountService.findAccountByUsername(USERNAME)).thenReturn(account);
    when(accountService.updateAccountByUsername(eq(USERNAME), any(UpdateAccountRequest.class))).thenReturn(account);
    doNothing().when(accountService)
        .follow(any(AccountRelationshipRequest.class));
    doNothing().when(accountService)
        .unfollow(any(AccountRelationshipRequest.class));
    when(accountService.getAccountFollowers(USERNAME, PAGE_NUMBER, PAGE_SIZE)).thenReturn(accountPage);
    when(accountService.getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE)).thenReturn(accountPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountService);
  }

  @Test
  void createAccount() {

    ApiSingleResponse<AccountResponse> result = accountController.createAccount(createAccountRequest);

    verify(accountService).createAccount(createAccountRequest);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(USERNAME, result.getData()
        .getUsername());
  }

  @Test
  void getAccounts() {

    ApiListResponse<AccountResponse> result = accountController.getAccounts(PAGE_NUMBER, PAGE_SIZE);

    verify(accountService).findAccounts(PAGE_NUMBER, PAGE_SIZE);

    assertNotNull(result);
    assertNotNull(result.getContent());
    assertFalse(result.getContent()
        .isEmpty());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertTrue(result.getContent()
        .size() > 0);
  }

  @Test
  void getAccountByUsername() {

    ApiSingleResponse<AccountResponse> result = accountController.getAccountByUsername(USERNAME);

    verify(accountService).findAccountByUsername(USERNAME);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(USERNAME, result.getData()
        .getUsername());
  }

  @Test
  void updateAccount() {

    ApiSingleResponse<AccountResponse> result = accountController.updateAccount(USERNAME, updateAccountRequest);

    verify(accountService).updateAccountByUsername(USERNAME, updateAccountRequest);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertEquals(USERNAME, result.getData()
        .getUsername());
  }

  @Test
  void initDummyAccounts() {

    ApiResponse result = accountController.initDummyAccounts();

    verify(accountService).initDummyAccounts();

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }

  @Test
  void followAccount() {

    ApiResponse result = accountController.followAccount(accountRelationshipRequest);

    verify(accountService).follow(accountRelationshipRequest);

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }

  @Test
  void unfollowAccount() {

    ApiResponse result = accountController.unfollowAccount(accountRelationshipRequest);

    verify(accountService).unfollow(accountRelationshipRequest);

    assertNotNull(result);
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
  }

  @Test
  void getAccountFollowers() {

    ApiListResponse<AccountFollowerResponse> result =
        accountController.getAccountFollowers(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(accountService).getAccountFollowers(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    assertNotNull(result);
    assertNotNull(result.getContent());
    assertFalse(result.getContent()
        .isEmpty());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertTrue(result.getContent()
        .size() > 0);
  }

  @Test
  void getAccountFollowing() {

    ApiListResponse<AccountFollowerResponse> result =
        accountController.getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(accountService).getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    assertNotNull(result);
    assertNotNull(result.getContent());
    assertFalse(result.getContent()
        .isEmpty());
    assertEquals(HttpStatus.OK.value(), result.getCode());
    assertEquals(HttpStatus.OK.name(), result.getStatus());
    assertNull(result.getError());
    assertTrue(result.getContent()
        .size() > 0);
  }
}
