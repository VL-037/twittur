package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.repository.service.AccountRelationshipRepositoryService;
import vincentlow.twittur.repository.service.AccountRepositoryService;

@ExtendWith(SpringExtension.class)
public class AccountServiceImplTest {

  private final String ACCOUNT_ID = "ACCOUNT_ID";

  private final String FIRST_NAME = "FIRST_NAME";

  private final String LAST_NAME = "LAST_NAME";

  private final LocalDate DATE_OF_BIRTH = LocalDate.parse("2000-01-01");

  private final String USERNAME = "USERNAME";

  private final String ACCOUNT_NAME = "ACCOUNT_NAME";

  private final String BIO = "BIO";

  private final String EMAIL_ADDRESS = "EMAIL_ADDRESS";

  private final String PHONE_NUMBER = "+621234567890";

  private final String PASSWORD = "PASSWORD_PASSWORD";

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String ACCOUNT_RELATIONSHIP_ID = "ACCOUNT_RELATIONSHIP_ID";

  private final String FOLLOWER_ID = "FOLLOWER_ID";

  private final String FOLLOWED_ID = "FOLLOWED_ID";

  @InjectMocks
  private AccountServiceImpl accountService;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  @Mock
  private AccountRelationshipRepositoryService accountRelationshipRepositoryService;

  private Account account;

  private List<Account> accountList;

  private Page<Account> accountPage;

  private CreateAccountRequest createAccountRequest;

  private UpdateAccountRequest updateAccountRequest;

  private AccountRelationship accountRelationship;

  private AccountRelationshipRequest accountRelationshipRequest;

  private Page<Account> followersPage;

  private Page<Account> followingPage;

  @BeforeEach
  void setUp() {

    openMocks(this);

    String salt = BCrypt.gensalt();

    account = new Account();
    account.setId(ACCOUNT_ID);
    account.setUsername(USERNAME);
    account.setEmailAddress(EMAIL_ADDRESS);
    account.setSalt(salt);
    account.setPassword(BCrypt.hashpw(PASSWORD, salt));

    accountList = new ArrayList<>();
    accountList.add(account);

    accountPage = new PageImpl<>(accountList, pageRequest, accountList.size());

    createAccountRequest = CreateAccountRequest.builder()
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .dateOfBirth(DATE_OF_BIRTH)
        .username(USERNAME)
        .accountName(ACCOUNT_NAME)
        .emailAddress(EMAIL_ADDRESS)
        .phoneNumber(PHONE_NUMBER)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    updateAccountRequest = UpdateAccountRequest.builder()
        .username(USERNAME)
        .accountName(ACCOUNT_NAME)
        .emailAddress(EMAIL_ADDRESS)
        .phoneNumber(PHONE_NUMBER)
        .bio(BIO)
        .oldPassword(PASSWORD)
        .newPassword(PASSWORD)
        .confirmNewPassword(PASSWORD)
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

    followersPage = new PageImpl<>(followerList, pageRequest, followerList.size());

    List<Account> followingList = new ArrayList<>();
    Account following1 = new Account();
    Account following2 = new Account();
    followingList.add(following1);
    followingList.add(following2);

    followingPage = new PageImpl<>(followingList, pageRequest, followingList.size());

    when(accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(account);
    when(accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS)).thenReturn(account);
    when(accountRepositoryService.save(any(Account.class))).thenReturn(account);

    when(accountRepositoryService.findAll(any(PageRequest.class))).thenReturn(accountPage);

    doNothing().when(accountRepositoryService)
        .saveAll(anyList());

    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(FOLLOWER_ID)).thenReturn(follower);
    when(accountRepositoryService.findByIdAndMarkForDeleteFalse(FOLLOWED_ID)).thenReturn(account);

    when(accountRelationshipRepositoryService.findByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID))
        .thenReturn(accountRelationship);
    doNothing().when(accountRelationshipRepositoryService)
        .deleteById(ACCOUNT_RELATIONSHIP_ID);

    when(accountRepositoryService.findFollowers(account.getId(), pageRequest))
        .thenReturn(followersPage);
    when(accountRepositoryService.findFollowing(account.getId(), pageRequest))
        .thenReturn(followingPage);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountRepositoryService, accountRelationshipRepositoryService);
  }

  @Test
  void createAccount() {

    when(accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(null);
    when(accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS)).thenReturn(null);

    Account result = accountService.createAccount(createAccountRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS);
    verify(accountRepositoryService).save(any(Account.class));

    assertNotNull(result);
    assertEquals(USERNAME, result.getUsername());
  }

  @Test
  void findAccounts() {

    Page<Account> result = accountService.findAccounts(PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findAll(pageRequest);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void findAccountByUsername() {

    when(accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(account);

    Account result = accountService.findAccountByUsername(USERNAME);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);

    assertNotNull(result);
    assertEquals(USERNAME, result.getUsername());
  }

  @Test
  void updateAccountByUsername() {

    when(accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(account);

    Account result = accountService.updateAccountByUsername(USERNAME, updateAccountRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).save(any(Account.class));

    assertNotNull(result);
    assertEquals(USERNAME, result.getUsername());
    assertEquals(BIO, result.getBio());
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
    verify(accountRepositoryService).findFollowers(account.getId(), pageRequest);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(followersPage.getSize(), result.getSize());
  }

  @Test
  void getAccountFollowing() {

    Page<Account> result = accountService.getAccountFollowing(USERNAME, PAGE_NUMBER, PAGE_SIZE);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).findFollowing(account.getId(), pageRequest);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(followingPage.getSize(), result.getSize());
  }
}
