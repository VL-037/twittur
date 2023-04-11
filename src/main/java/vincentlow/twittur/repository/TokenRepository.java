package vincentlow.twittur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

  @Query("SELECT t FROM Token t JOIN Account a ON t.account.id = a.id WHERE a.id = :accountId AND (t.expired = false OR t.revoked = false)")
  List<Token> findAllValidTokensByAccountId(String accountId);

  Token findByToken(String token);
}
