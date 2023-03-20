package vincentlow.twittur.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

  Page<Account> findAll(Pageable pageable);

  Account findByIdAndMarkForDeleteFalse(String id);

  Account findByUsernameAndMarkForDeleteFalse(String username);

  Account findByEmailAddressAndMarkForDeleteFalse(String emailAddress);
}
