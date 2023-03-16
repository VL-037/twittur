package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.CreateAccountRequest;

public interface AccountService {

  Page<Account> findAccounts(int pageNumber, int pageSize);

  Account findAccountByUsername(String username);

  Account createAccount(CreateAccountRequest account);

  void initDummyAccounts();
}
