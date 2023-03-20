package vincentlow.twittur.repository.service;

import vincentlow.twittur.model.entity.AccountRelationship;

public interface AccountRelationshipRepositoryService {

  void save(AccountRelationship accountRelationship);

  AccountRelationship findByFollowerIdAndFollowedId(String followerId, String followedId);

  void deleteById(String id);
}
