package vincentlow.twittur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, String> {

  List<Email> findAllBySentFalseAndMarkForDeleteFalse();
}
