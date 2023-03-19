package vincentlow.twittur.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.AccountRelationship;

@Repository
public interface AccountRelationshipRepository extends JpaRepository<AccountRelationship, String> {

  AccountRelationship findByFollowerIdAndFollowedId(String followerId, String followedId);
}
