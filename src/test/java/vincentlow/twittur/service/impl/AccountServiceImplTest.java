package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.UpdateAccountEmailRequest;
import vincentlow.twittur.model.request.UpdateAccountPasswordRequest;
import vincentlow.twittur.model.request.UpdateAccountPhoneNumberRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.repository.service.AccountRelationshipRepositoryService;
import vincentlow.twittur.repository.service.AccountRepositoryService;

public class AccountServiceImplTest {

  private final String ACCOUNT_ID = "ACCOUNT_ID";

  private final String FIRST_NAME = "FIRST_NAME";

  private final String LAST_NAME = "LAST_NAME";

  private final LocalDate DATE_OF_BIRTH = LocalDate.parse("2000-01-01");

  private final String USERNAME = "USERNAME";

  private final String ACCOUNT_NAME = "ACCOUNT_NAME";

  private final String BIO = "BIO";

  private final String EMAIL_ADDRESS = "EMAIL_ADDRESS";

  private final String NEW_EMAIL_ADDRESS = "NEW_EMAIL_ADDRESS";

  private final String PHONE_NUMBER = "+621234567890";

  private final String PASSWORD = "PASSWORD_PASSWORD";

  private final String NEW_PASSWORD = "NEW_PASSWORD_PASSWORD";

  private final String CONFIRM_NEW_PASSWORD = NEW_PASSWORD;

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final int TWEETS_COUNT = 0;

  private final int FOLLOWERS_COUNT = 0;

  private final int FOLLOWING_COUNT = 0;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String ACCOUNT_RELATIONSHIP_ID = "ACCOUNT_RELATIONSHIP_ID";

  private final String FOLLOWER_ID = "FOLLOWER_ID";

  private final String FOLLOWED_ID = "FOLLOWED_ID";

  @InjectMocks
  private AccountServiceImpl accountService;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  @Mock
  private AccountRelationshipRepositoryService accountRelationshipRepositoryService;

  @Mock
  private PasswordEncoder passwordEncoder;

  private Account account;

  private List<Account> accountList;

  private Page<Account> accountPage;

  private UpdateAccountRequest updateAccountRequest;

  private UpdateAccountEmailRequest updateAccountEmailRequest;

  private UpdateAccountPhoneNumberRequest updateAccountPhoneNumberRequest;

  private UpdateAccountPasswordRequest updateAccountPasswordRequest;

  private AccountRelationship accountRelationship;

  private AccountRelationshipRequest accountRelationshipRequest;

  private Page<Account> followersPage;

  private Page<Account> followingPage;

  @BeforeEach
  void setUp() {

    openMocks(this);

    account = new Account();
    account.setId(ACCOUNT_ID);
    account.setFirstName(FIRST_NAME);
    account.setLastName(LAST_NAME);
    account.setDateOfBirth(DATE_OF_BIRTH);
    account.setUsername(USERNAME);
    account.setAccountName(ACCOUNT_NAME);
    account.setBio(BIO);
    account.setEmailAddress(EMAIL_ADDRESS);
    account.setPhoneNumber(PHONE_NUMBER);
    account.setPassword(PASSWORD);
    account.setTweets(Collections.EMPTY_LIST);
    account.setFollowers(Collections.EMPTY_LIST);
    account.setFollowing(Collections.EMPTY_LIST);
    account.setSentMessages(Collections.EMPTY_LIST);
    account.setReceivedMessages(Collections.EMPTY_LIST);
    account.setNotifications(Collections.EMPTY_LIST);

    accountList = new ArrayList<>();
    accountList.add(account);

    accountPage = new PageImpl<>(accountList, PAGE_REQUEST, accountList.size());

    updateAccountRequest = UpdateAccountRequest.builder()
        .username(USERNAME)
        .accountName(ACCOUNT_NAME)
        .bio(BIO)
        .build();

    updateAccountEmailRequest = UpdateAccountEmailRequest.builder()
        .emailAddress(NEW_EMAIL_ADDRESS)
        .build();

    updateAccountPhoneNumberRequest = UpdateAccountPhoneNumberRequest.builder()
        .phoneNumber(PHONE_NUMBER)
        .build();

    updateAccountPasswordRequest = UpdateAccountPasswordRequest.builder()
        .oldPassword(PASSWORD)
        .newPassword(NEW_PASSWORD)
        .confirmNewPassword(CONFIRM_NEW_PASSWORD)
        .build();

    Account follower = new Account();
    follower.setId(FOLLOWER_ID);

    accountRelationship = new AccountRelationship();
    accountRelationship.setId(ACCOUNT_RELATIONSHIP_ID);
    accountRelationship.setFollower(follower);
    accountRelationship.setFollowed(account);

    accountRelationshipRequest = AccountRelationshipRequest.builder()
        .followerId(FOLLOWER_ID)
        .followedId(FOLLOWED_ID)
        .build();

    List<Account> followerList = new ArrayList<>();
    Account follower1 = new Account();
    Account follower2 = new Account();
    followerList.add(follower1);
    followerList.add(follower2);

    followersPage = new PageImpl<>(followerList, PAGE_REQUEST, followerList.size());

    List<Account> followingList = new ArrayList<>();
    Account following1 = new Account();
    Account following2 = new Account();
    followingList.add(following1);
    followingList.add(following2);

    followingPage = new PageImpl<>(followingList, PAGE_REQUEST, followingList.size());

    when(accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(account);
    when(accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS)).thenReturn(account);
    when(accountRepositoryService.save(any(Account.class))).thenReturn(account);

    when(accountRepositoryService.findAll(any(PageRequest.class))).thenReturn(accountPage);

    when(passwordEncoder.matches(eq(PASSWORD), anyString())).thenReturn(true);

    doNothing().when(accountRepositoryService)
        .saveAll(anyList());

    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(FOLLOWER_ID)).thenReturn(follower);
    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(FOLLOWED_ID)).thenReturn(account);

    when(accountRelationshipRepositoryService.findByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID))
        .thenReturn(accountRelationship);
    doNothing().when(accountRelationshipRepositoryService)
        .deleteById(ACCOUNT_RELATIONSHIP_ID);

    when(accountRepositoryService.findFollowers(account.getId(), PAGE_REQUEST))
        .thenReturn(followersPage);
    when(accountRepositoryService.findFollowing(account.getId(), PAGE_REQUEST))
        .thenReturn(followingPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountRepositoryService, accountRelationshipRepositoryService);
  }

  @Test
  void findAccounts() {

    Page<Account> result = accountService.findAccounts(PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findAll(PAGE_REQUEST);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void findAccountByUsername() {

    Account result = accountService.findAccountByUsername(USERNAME);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);

    assertNotNull(result);
    assertEquals(FIRST_NAME, result.getFirstName());
    assertEquals(LAST_NAME, result.getLastName());
    assertEquals(DATE_OF_BIRTH, result.getDateOfBirth());
    assertEquals(USERNAME, result.getUsername());
    assertEquals(ACCOUNT_NAME, result.getAccountName());
    assertEquals(BIO, result.getBio());
    assertEquals(EMAIL_ADDRESS, result.getEmailAddress());
    assertEquals(PHONE_NUMBER, result.getPhoneNumber());
    assertNotNull(result.getTweets());
    assertNotNull(result.getFollowers());
    assertNotNull(result.getFollowing());
    assertNotNull(result.getSentMessages());
    assertNotNull(result.getReceivedMessages());
    assertNotNull(result.getNotifications());
    assertEquals(TWEETS_COUNT, result.getTweetsCount());
    assertEquals(FOLLOWERS_COUNT, result.getFollowersCount());
    assertEquals(FOLLOWING_COUNT, result.getFollowingCount());
  }

  @Test
  void updateAccountByUsername() {

    accountService.updateAccountByUsername(USERNAME, updateAccountRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).save(any(Account.class));
  }

  @Test
  void updateAccountEmailAddressByUsername() {

    when(accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(anyString())).thenReturn(null);

    accountService.updateAccountEmailAddressByUsername(USERNAME, updateAccountEmailRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).findByEmailAddressAndMarkForDeleteFalse(anyString());
    verify(accountRepositoryService).save(any(Account.class));
  }

  @Test
  void updateAccountPhoneNumberByUsername() {

    accountService.updateAccountPhoneNumberByUsername(USERNAME, updateAccountPhoneNumberRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).save(any(Account.class));
  }

  @Test
  void updateAccountPasswordByUsername() {

    accountService.updateAccountPasswordByUsername(USERNAME, updateAccountPasswordRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).save(any(Account.class));
  }

  @Test
  void initDummyAccounts() {

    accountService.initDummyAccounts();

    verify(accountRepositoryService, times(5)).findByUsernameAndMarkForDeleteFalse(anyString());
    verify(accountRepositoryService, times(5)).findByEmailAddressAndMarkForDeleteFalse(anyString());
    verify(accountRepositoryService).saveAll(anyList());
  }

  @Test
  void follow() {

    account.setId(FOLLOWED_ID);

    accountService.follow(accountRelationshipRequest);

    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(FOLLOWER_ID);
    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(FOLLOWED_ID);
    verify(accountRelationshipRepositoryService).save(any(AccountRelationship.class));
    verify(accountRepositoryService).saveAll(anyList());
  }

  @Test
  void unfollow() {

    account.setId(FOLLOWED_ID);

    accountService.unfollow(accountRelationshipRequest);

    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(FOLLOWER_ID);
    verify(accountRepositoryService).findByIdAndMarkForDeleteFalse(FOLLOWED_ID);
    verify(accountRelationshipRepositoryService).findByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID);
    verify(accountRelationshipRepositoryService).deleteById(ACCOUNT_RELATIONSHIP_ID);
    verify(accountRepositoryService).saveAll(anyList());
  }

  @Test
  void getAccountFollowers() {

    Page<Account> result = accountService.getAccountFollowers(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).findFollowers(account.getId(), PAGE_REQUEST);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(followersPage.getSize(), result.getSize());
  }

  @Test
  void getAccountFollowing() {

    Page<Account> result = accountService.getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).findFollowing(account.getId(), PAGE_REQUEST);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(followingPage.getSize(), result.getSize());
  }
}
