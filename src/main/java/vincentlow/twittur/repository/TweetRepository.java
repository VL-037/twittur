package vincentlow.twittur.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.Tweet;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, String> {

  Page<Tweet> findAllByCreatorId(String creator, Pageable pageable);
}
