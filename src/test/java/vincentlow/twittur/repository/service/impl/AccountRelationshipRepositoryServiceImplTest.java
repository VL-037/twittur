package vincentlow.twittur.repository.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import vincentlow.twittur.model.constant.CacheKey;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.repository.AccountRelationshipRepository;
import vincentlow.twittur.service.CacheService;

public class AccountRelationshipRepositoryServiceImplTest {

  private final String ACCOUNT_RELATIONSHIP_ID = "ACCOUNT_RELATIONSHIP_ID";

  private final String FOLLOWER_ID = "FOLLOWER_ID";

  private final String FOLLOWED_ID = "FOLLOWED_ID";

  @InjectMocks
  private AccountRelationshipRepositoryServiceImpl accountRelationshipRepositoryService;

  @Mock
  private CacheService cacheService;

  @Mock
  private AccountRelationshipRepository accountRelationshipRepository;

  private AccountRelationship accountRelationship;

  private Account follower;

  private Account followed;

  @BeforeEach
  void setUp() {

    openMocks(this);

    follower = new Account();
    followed = new Account();

    accountRelationship = new AccountRelationship();
    accountRelationship.setId(ACCOUNT_RELATIONSHIP_ID);
    accountRelationship.setFollower(follower);
    accountRelationship.setFollowed(followed);

    doNothing().when(cacheService)
        .deleteByPattern(CacheKey.FIND_ACCOUNT_FOLLOWERS_PATTERN);
    doNothing().when(cacheService)
        .deleteByPattern(CacheKey.FIND_ACCOUNT_FOLLOWING_PATTERN);
    when(accountRelationshipRepository.save(any(AccountRelationship.class))).thenReturn(accountRelationship);

    when(accountRelationshipRepository.findByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID))
        .thenReturn(accountRelationship);

    doNothing().when(accountRelationshipRepository)
        .deleteById(ACCOUNT_RELATIONSHIP_ID);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(cacheService, accountRelationshipRepository);
  }

  @Test
  void save() {

    accountRelationshipRepositoryService.save(accountRelationship);

    verify(cacheService).deleteByPattern(CacheKey.FIND_ACCOUNT_FOLLOWERS_PATTERN);
    verify(cacheService).deleteByPattern(CacheKey.FIND_ACCOUNT_FOLLOWING_PATTERN);
    verify(accountRelationshipRepository).save(any(AccountRelationship.class));
  }

  @Test
  void findByFollowerIdAndFollowedId_follower() {

    AccountRelationship result =
        accountRelationshipRepositoryService.findByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID);

    verify(accountRelationshipRepository).findByFollowerIdAndFollowedId(FOLLOWER_ID, FOLLOWED_ID);

    assertNotNull(result);
    assertEquals(follower, result.getFollower());
    assertEquals(followed, result.getFollowed());
  }

  @Test
  void deleteById() {

    accountRelationshipRepositoryService.deleteById(ACCOUNT_RELATIONSHIP_ID);

    verify(accountRelationshipRepository).deleteById(ACCOUNT_RELATIONSHIP_ID);
  }
}
