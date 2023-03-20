package vincentlow.twittur.repository.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vincentlow.twittur.model.constant.CacheKey;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.repository.AccountRelationshipRepository;
import vincentlow.twittur.repository.service.AccountRelationshipRepositoryService;
import vincentlow.twittur.service.CacheService;

@Service
public class AccountRelationshipRepositoryServiceImpl implements AccountRelationshipRepositoryService {

  @Autowired
  private CacheService cacheService;

  @Autowired
  private AccountRelationshipRepository accountRelationshipRepository;

  @Override
  public void save(AccountRelationship accountRelationship) {

    cacheService.deleteByPattern(CacheKey.FIND_ACCOUNT_FOLLOWERS_PATTERN);
    cacheService.deleteByPattern(CacheKey.FIND_ACCOUNT_FOLLOWING_PATTERN);
    accountRelationshipRepository.save(accountRelationship);
  }

  @Override
  public AccountRelationship findByFollowerIdAndFollowedId(String followerId, String followedId) {

    return accountRelationshipRepository.findByFollowerIdAndFollowedId(followerId, followedId);
  }

  @Override
  public void deleteById(String id) {

    accountRelationshipRepository.deleteById(id);
  }
}
