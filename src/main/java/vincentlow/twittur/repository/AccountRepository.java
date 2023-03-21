package vincentlow.twittur.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

  Page<Account> findAll(Pageable pageable);

  Account findByIdAndMarkForDeleteFalse(String id);

  Account findByUsernameAndMarkForDeleteFalse(String username);

  Account findByEmailAddressAndMarkForDeleteFalse(String emailAddress);

  @Query("SELECT a FROM Account a JOIN AccountRelationship ar ON a.id = ar.follower.id WHERE ar.followed.id = :accountId")
  Page<Account> findFollowers(String accountId, Pageable pageable);

  @Query("SELECT a FROM Account a JOIN AccountRelationship ar ON a.id = ar.follower.id WHERE ar.followed.id = :accountId")
  List<Account> findAllFollowers(String accountId);

  @Query("SELECT a FROM Account a JOIN AccountRelationship ar ON a.id = ar.followed.id WHERE ar.follower.id = :accountId")
  Page<Account> findFollowing(String accountId, Pageable pageable);
}
