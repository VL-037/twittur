package vincentlow.twittur.repository.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import vincentlow.twittur.model.entity.Account;

public interface AccountRepositoryService {

  Account save(Account account);

  Page<Account> findAll(PageRequest pageRequest);

  Account findByUsernameAndMarkForDeleteFalse(String username);

  Account findByEmailAddressAndMarkForDeleteFalse(String emailAddress);

  Account findByUsernameOrEmailAddressAndMarkForDeleteFalse(String username);

  void saveAll(List<Account> accounts);

  Account findByIdAndMarkForDeleteFalse(String id);

  Page<Account> findFollowers(String accountId, PageRequest pageRequest);

  List<Account> findAllFollowers(String accountId);

  Page<Account> findFollowing(String accountId, PageRequest pageRequest);
}
