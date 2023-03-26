package vincentlow.twittur.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.DirectMessage;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, String> {

  Page<DirectMessage> findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(String senderId, String recipientId,
      Pageable pageable);
}
