package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.UpdateAccountEmailRequest;
import vincentlow.twittur.model.request.UpdateAccountPasswordRequest;
import vincentlow.twittur.model.request.UpdateAccountPhoneNumberRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;

public interface AccountService {

  Page<Account> findAccounts(int pageNumber, int pageSize);

  Account findAccountByUsername(String username);

  void updateAccountByUsername(String username, UpdateAccountRequest request);

  void updateAccountEmailAddressByUsername(String username, UpdateAccountEmailRequest request);

  void updateAccountPhoneNumberByUsername(String username, UpdateAccountPhoneNumberRequest request);

  void updateAccountPasswordByUsername(String username, UpdateAccountPasswordRequest request);

  void initDummyAccounts();

  void follow(AccountRelationshipRequest request);

  void unfollow(AccountRelationshipRequest request);

  Page<Account> getAccountFollowers(String username, int pageNumber, int pageSize);

  Page<Account> getAccountFollowing(String username, int pageNumber, int pageSize);
}
