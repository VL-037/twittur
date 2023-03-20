package vincentlow.twittur.repository.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import vincentlow.twittur.model.constant.CacheKey;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.CacheService;

@Service
public class AccountRepositoryServiceImpl implements AccountRepositoryService {

  @Autowired
  private CacheService cacheService;

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public Account save(Account account) {

    cacheService.deleteByPattern(CacheKey.FIND_ALL_ACCOUNTS_PATTERN);
    cacheService.deleteByPattern(String.format(CacheKey.FIND_ONE_ACCOUNT, account.getId()));
    cacheService.deleteByPattern(String.format(CacheKey.FIND_ONE_ACCOUNT, account.getUsername()));
    cacheService.deleteByPattern(String.format(CacheKey.FIND_ONE_ACCOUNT, account.getEmailAddress()));
    return accountRepository.save(account);
  }

  @Override
  public Page<Account> findAll(PageRequest pageRequest) {

    String key = String.format(CacheKey.FIND_ALL_ACCOUNTS, pageRequest.getPageNumber(), pageRequest.getPageSize());
    List<Account> accounts = cacheService.get(key, new TypeReference<>() {});
    Page<Account> accountsPage;
    if (Objects.isNull(accounts)) {
      accountsPage = accountRepository.findAll(pageRequest);
      cacheService.set(key, accountsPage.getContent(), null);
    } else {
      accountsPage = new PageImpl<>(accounts, pageRequest, accounts.size());
    }
    return accountsPage;
  }

  @Override
  public Account findByUsernameAndMarkForDeleteFalse(String username) {

    String key = String.format(CacheKey.FIND_ONE_ACCOUNT, username);
    Account account = cacheService.get(key, new TypeReference<>() {});

    if (Objects.isNull(account)) {
      account = accountRepository.findByUsernameAndMarkForDeleteFalse(username);
      cacheService.set(key, account, null);
    }
    return account;
  }

  @Override // not using cache since only used on update Account, which delete the key
  public Account findByEmailAddressAndMarkForDeleteFalse(String emailAddress) {

    return accountRepository.findByEmailAddressAndMarkForDeleteFalse(emailAddress);
  }

  @Override
  public void saveAll(List<Account> accounts) {

    cacheService.deleteByPattern(CacheKey.FIND_ALL_ACCOUNTS_PATTERN);
    cacheService.deleteByPattern(CacheKey.FIND_ONE_ACCOUNT_PATTERN);
    accountRepository.saveAll(accounts);
  }

  @Override // not using cache since only used on follow & unfollow Account, which delete the key
  public Account findByIdAndMarkForDeleteFalse(String id) {

    return accountRepository.findByIdAndMarkForDeleteFalse(id);
  }

  @Override
  public Page<Account> findFollowers(String accountId, PageRequest pageRequest) {

    return accountRepository.findFollowers(accountId, pageRequest);
  }

  @Override
  public Page<Account> findFollowing(String accountId, PageRequest pageRequest) {

    return accountRepository.findFollowing(accountId, pageRequest);
  }
}
