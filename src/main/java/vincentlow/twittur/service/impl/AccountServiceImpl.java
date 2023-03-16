package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.util.StringUtils;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.repository.TweetRepository;
import vincentlow.twittur.service.AccountService;
import vincentlow.twittur.utils.StringUtil;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public Page<Account> findAccounts(int pageNumber, int pageSize) {

    return accountRepository.findAll(PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Account findAccountByUsername(String username) {

    return accountRepository.findByUsername(username);
  }

  @Override
  public Account createAccount(CreateAccountRequest request) {

    Account account = convertToAccount(request);

    validateState(Objects.nonNull(account), ErrorCode.ACCOUNT_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(account.getFirstName()),
        ErrorCode.FIRST_NAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(account.getFirstName()
        .length() < 50, ErrorCode.FIRST_NAME_MAXIMAL_LENGTH_IS_50.getMessage());
    validateArgument(StringUtils.isNotBlank(account.getLastName()), ErrorCode.LAST_NAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(account.getLastName()
        .length() < 50, ErrorCode.LAST_NAME_MAXIMAL_LENGTH_IS_50.getMessage());
    validateState(Objects.nonNull(account.getDateOfBirth()), ErrorCode.DATE_OF_BIRTH_MUST_NOT_BE_NULL.getMessage());
    validateArgument(getCurrentAge(account.getDateOfBirth()) >= 13, ErrorCode.AGE_MUST_BE_AT_LEAST_13.getMessage());
    validateArgument(StringUtils.isNotBlank(account.getUsername()), ErrorCode.USERNAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(account.getUsername()
        .length() > 5, ErrorCode.USERNAME_MINIMAL_LENGTH_IS_5.getMessage());
    validateArgument(account.getUsername()
        .length() < 15, ErrorCode.USERNAME_MAXIMAL_LENGTH_IS_15.getMessage());
    validateArgument(validateBioLength(account.getBio()), ErrorCode.BIO_MAXIMAL_LENGTH_IS_100.getMessage());
    validateArgument(StringUtils.isNotBlank(account.getEmailAddress()),
        ErrorCode.EMAIL_ADDRESS_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(account.getEmailAddress()
        .length() < 62, ErrorCode.EMAIL_ADDRESS_MAXIMAL_LENGTH_IS_62.getMessage());

    StringUtil.trimStrings(account);
    account.setTweets(Collections.EMPTY_LIST);

    return accountRepository.save(account);
  }

  @Override
  public void initDummyAccounts() {

    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource accountJson = new ClassPathResource("dummy_requests/accounts.json");

    try {
      List<CreateAccountRequest> requests = mapper.readValue(accountJson.getInputStream(), new TypeReference<>() {});
      List<Account> accounts = requests.stream()
          .map(request -> convertToAccount(request))
          .collect(Collectors.toList());
      accountRepository.saveAll(accounts);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Account convertToAccount(CreateAccountRequest request) {

    Account account = new Account();
    account.prePersist();
    account.setFirstName(request.getFirstName());
    account.setLastName(request.getLastName());
    account.setDateOfBirth(request.getDateOfBirth());
    account.setUsername(request.getUsername());
    account.setAccountName(request.getAccountName());
    account.setBio(request.getBio());
    account.setEmailAddress(request.getEmailAddress());
    return account;
  }

  private int getCurrentAge(Date dateOfBirth) {

    LocalDate currentDate = LocalDate.now();
    LocalDate birthDate = dateOfBirth.toLocalDate();
    return Period.between(birthDate, currentDate)
        .getYears();
  }

  private boolean validateBioLength(String bio) {

    if (bio.isBlank()) {
      return true;
    }
    return bio.length() < 100;
  }
}
