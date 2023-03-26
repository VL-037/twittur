package vincentlow.twittur.repository.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import com.fasterxml.jackson.core.type.TypeReference;

import vincentlow.twittur.model.constant.CacheKey;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.service.CacheService;

public class AccountRepositoryServiceImplTest {

  private final String ACCOUNT_ID = "ACCOUNT_ID";

  private final String USERNAME = "USERNAME";

  private final String EMAIL_ADDRESS = "EMAIL_ADDRESS";

  private final int PAGE_NUMBER = 0;

  private final int PAGE_SIZE = 10;

  private final PageRequest PAGE_REQUEST = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

  private final String FIND_ALL_ACCOUNTS_PATTERN = CacheKey.FIND_ALL_ACCOUNTS_PATTERN;

  private final String FIND_ONE_ACCOUNT_PATTERN = CacheKey.FIND_ONE_ACCOUNT_PATTERN;

  private final String FIND_ONE_ACCOUNT_BY_ID_PATTERN = String.format(CacheKey.FIND_ONE_ACCOUNT, ACCOUNT_ID);

  private final String FIND_ONE_ACCOUNT_BY_USERNAME_PATTERN = String.format(CacheKey.FIND_ONE_ACCOUNT, USERNAME);

  private final String FIND_ONE_ACCOUNT_BY_EMAIL_ADDRESS_PATTERN =
      String.format(CacheKey.FIND_ONE_ACCOUNT, EMAIL_ADDRESS);

  private final String FIND_ALL_ACCOUNTS_KEY = String.format(CacheKey.FIND_ALL_ACCOUNTS, PAGE_NUMBER, PAGE_SIZE);

  private final String FIND_ACCOUNT_FOLLOWERS_KEY = String.format(CacheKey.FIND_ACCOUNT_FOLLOWERS, ACCOUNT_ID);

  private final String FIND_ACCOUNT_FOLLOWING_KEY = String.format(CacheKey.FIND_ACCOUNT_FOLLOWING, ACCOUNT_ID);

  @InjectMocks
  private AccountRepositoryServiceImpl accountRepositoryService;

  @Mock
  private CacheService cacheService;

  @Mock
  private AccountRepository accountRepository;

  private Account account;

  private List<Account> accountList;

  private Page<Account> accountPage;

  private List<Account> followerList;

  private Page<Account> followerPage;

  private List<Account> followingList;

  private Page<Account> followingPage;

  @BeforeEach
  void setUp() {

    openMocks(this);

    account = new Account();
    account.setId(ACCOUNT_ID);
    account.setUsername(USERNAME);
    account.setEmailAddress(EMAIL_ADDRESS);

    accountList = new ArrayList<>();
    accountList.add(account);

    accountPage = new PageImpl<>(accountList, PAGE_REQUEST, accountList.size());

    Account follower = new Account();

    followerList = new ArrayList<>();
    followerList.add(follower);

    followerPage = new PageImpl<>(followerList, PAGE_REQUEST, followerList.size());

    Account following = new Account();

    followingList = new ArrayList<>();
    followingList.add(following);

    followingPage = new PageImpl<>(followingList, PAGE_REQUEST, followingList.size());

    doNothing().when(cacheService)
        .deleteByPattern(FIND_ALL_ACCOUNTS_PATTERN);
    doNothing().when(cacheService)
        .deleteByPattern(FIND_ONE_ACCOUNT_BY_ID_PATTERN);
    doNothing().when(cacheService)
        .deleteByPattern(FIND_ONE_ACCOUNT_BY_USERNAME_PATTERN);
    doNothing().when(cacheService)
        .deleteByPattern(FIND_ONE_ACCOUNT_BY_EMAIL_ADDRESS_PATTERN);
    when(accountRepository.save(any(Account.class))).thenReturn(account);

    when(cacheService.get(eq(FIND_ALL_ACCOUNTS_KEY), any(TypeReference.class)))
        .thenReturn(accountList);
    when(accountRepository.findAll(PAGE_REQUEST)).thenReturn(accountPage);
    doNothing().when(cacheService)
        .set(FIND_ALL_ACCOUNTS_KEY, accountPage.getContent(), null);

    when(cacheService.get(eq(FIND_ONE_ACCOUNT_BY_USERNAME_PATTERN), any(TypeReference.class)))
        .thenReturn(account);
    when(accountRepository.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(account);
    doNothing().when(cacheService)
        .set(FIND_ONE_ACCOUNT_BY_USERNAME_PATTERN, account, null);

    when(accountRepository.findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS)).thenReturn(account);

    when(accountRepository.saveAll(anyList())).thenReturn(accountList);

    when(accountRepository.findByIdAndMarkForDeleteFalse(ACCOUNT_ID)).thenReturn(account);

    when(cacheService.get(eq(FIND_ACCOUNT_FOLLOWERS_KEY), any(TypeReference.class)))
        .thenReturn(followerList);
    when(accountRepository.findFollowers(ACCOUNT_ID, PAGE_REQUEST)).thenReturn(followerPage);
    doNothing().when(cacheService)
        .set(FIND_ACCOUNT_FOLLOWERS_KEY, followerPage.getContent(), null);

    when(accountRepository.findAllFollowers(ACCOUNT_ID)).thenReturn(followerList);

    when(cacheService.get(eq(FIND_ACCOUNT_FOLLOWING_KEY), any(TypeReference.class)))
        .thenReturn(followingList);
    when(accountRepository.findFollowing(ACCOUNT_ID, PAGE_REQUEST)).thenReturn(followingPage);
    doNothing().when(cacheService)
        .set(FIND_ACCOUNT_FOLLOWING_KEY, followingPage.getContent(), null);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(cacheService, accountRepository);
  }

  @Test
  void save() {

    Account result = accountRepositoryService.save(account);

    verify(cacheService).deleteByPattern(FIND_ALL_ACCOUNTS_PATTERN);
    verify(cacheService).deleteByPattern(FIND_ONE_ACCOUNT_BY_ID_PATTERN);
    verify(cacheService).deleteByPattern(FIND_ONE_ACCOUNT_BY_USERNAME_PATTERN);
    verify(cacheService).deleteByPattern(FIND_ONE_ACCOUNT_BY_EMAIL_ADDRESS_PATTERN);
    verify(accountRepository).save(account);

    assertNotNull(result);
    assertEquals(result.getId(), ACCOUNT_ID);
    assertEquals(result.getUsername(), USERNAME);
    assertEquals(result.getEmailAddress(), EMAIL_ADDRESS);
  }

  @Test
  void findAll() {

    Page<Account> result = accountRepositoryService.findAll(PAGE_REQUEST);

    verify(cacheService).get(eq(FIND_ALL_ACCOUNTS_KEY), any(TypeReference.class));

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void findByUsernameAndMarkForDeleteFalse() {

    Account result = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME);

    verify(cacheService).get(eq(FIND_ONE_ACCOUNT_BY_USERNAME_PATTERN), any(TypeReference.class));

    assertNotNull(result);
    assertEquals(result.getId(), ACCOUNT_ID);
    assertEquals(result.getUsername(), USERNAME);
    assertEquals(result.getEmailAddress(), EMAIL_ADDRESS);
  }

  @Test
  void findByEmailAddressAndMarkForDeleteFalse() {

    Account result = accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS);

    verify(accountRepository).findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS);

    assertNotNull(result);
    assertEquals(result.getId(), ACCOUNT_ID);
    assertEquals(result.getUsername(), USERNAME);
    assertEquals(result.getEmailAddress(), EMAIL_ADDRESS);
  }

  @Test
  void saveAll() {

    accountRepositoryService.saveAll(accountList);

    verify(cacheService).deleteByPattern(FIND_ALL_ACCOUNTS_PATTERN);
    verify(cacheService).deleteByPattern(FIND_ONE_ACCOUNT_PATTERN);
    verify(accountRepository).saveAll(accountList);
  }

  @Test
  void findByIdAndMarkForDeleteFalse() {

    Account result = accountRepositoryService.findByIdAndMarkForDeleteFalse(ACCOUNT_ID);

    verify(accountRepository).findByIdAndMarkForDeleteFalse(ACCOUNT_ID);

    assertNotNull(result);
    assertEquals(result.getId(), ACCOUNT_ID);
    assertEquals(result.getUsername(), USERNAME);
    assertEquals(result.getEmailAddress(), EMAIL_ADDRESS);
  }

  @Test
  void findFollowers() {

    Page<Account> result = accountRepositoryService.findFollowers(ACCOUNT_ID, PAGE_REQUEST);

    verify(cacheService).get(eq(FIND_ACCOUNT_FOLLOWERS_KEY), any(TypeReference.class));

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void findAllFollowers() {

    List<Account> result = accountRepositoryService.findAllFollowers(ACCOUNT_ID);

    verify(accountRepository).findAllFollowers(ACCOUNT_ID);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }

  @Test
  void findFollowing() {

    Page<Account> result = accountRepositoryService.findFollowing(ACCOUNT_ID, PAGE_REQUEST);

    verify(cacheService).get(eq(FIND_ACCOUNT_FOLLOWING_KEY), any(TypeReference.class));

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
