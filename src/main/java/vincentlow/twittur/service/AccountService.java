package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;

public interface AccountService {

  Account createAccount(CreateAccountRequest request);

  Page<Account> findAccounts(int pageNumber, int pageSize);

  Account findAccountByUsername(String username);

  Account updateAccountByUsername(String username, UpdateAccountRequest request);

  void initDummyAccounts();
}
