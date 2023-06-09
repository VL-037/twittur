package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;
import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.constant.Role;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountEmailRequest;
import vincentlow.twittur.model.request.UpdateAccountPasswordRequest;
import vincentlow.twittur.model.request.UpdateAccountPhoneNumberRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;
import vincentlow.twittur.repository.service.AccountRelationshipRepositoryService;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.AccountService;
import vincentlow.twittur.utils.StringUtil;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

  private final String DUMMY_REQUESTS_PATH = "dummy_requests/accounts.json";

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Autowired
  private AccountRelationshipRepositoryService accountRelationshipRepositoryService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Page<Account> findAccounts(int pageNumber, int pageSize) {

    return accountRepositoryService.findAll(PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Account findAccountByUsername(String username) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);;
    return validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);
  }

  @Override
  public void updateAccountByUsername(String username, UpdateAccountRequest request) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    StringUtil.trimStrings(request);

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getUsername()), ErrorCode.USERNAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getUsername()
        .length() >= 5, ErrorCode.USERNAME_MINIMAL_LENGTH_IS_5.getMessage());
    validateArgument(request.getUsername()
        .length() <= 15, ErrorCode.USERNAME_MAXIMAL_LENGTH_IS_15.getMessage());
    validateArgument(validateBioLength(request.getBio()), ErrorCode.BIO_MAXIMAL_LENGTH_IS_100.getMessage());

    if (!request.getUsername()
        .equals(account.getUsername())) {
      Account existingAccount = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(request.getUsername());
      if (Objects.nonNull(existingAccount)) {
        throw new ConflictException(ExceptionMessage.USERNAME_IS_TAKEN);
      }
    }

    BeanUtils.copyProperties(request, account);
    account.setUpdatedBy(account.getId());
    account.setUpdatedDate(LocalDateTime.now());

    accountRepositoryService.save(account);
  }

  @Override
  public void updateAccountEmailAddressByUsername(String username, UpdateAccountEmailRequest request) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    StringUtil.trimStrings(request);

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getEmailAddress()),
        ErrorCode.EMAIL_ADDRESS_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getEmailAddress()
        .length() <= 62, ErrorCode.EMAIL_ADDRESS_MAXIMAL_LENGTH_IS_62.getMessage());

    if (!request.getEmailAddress()
        .equals(account.getEmailAddress())) {
      Account existingAccount =
          accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(request.getEmailAddress());
      if (Objects.nonNull(existingAccount)) {
        throw new ConflictException(ExceptionMessage.EMAIL_IS_ASSOCIATED_WITH_AN_ACCOUNT);
      }

      account.setEmailAddress(request.getEmailAddress());
      account.setUpdatedBy(account.getId());
      account.setUpdatedDate(LocalDateTime.now());

      accountRepositoryService.save(account);
    }
  }

  @Override
  public void updateAccountPhoneNumberByUsername(String username, UpdateAccountPhoneNumberRequest request) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    StringUtil.trimStrings(request);

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    if (StringUtils.isNotBlank(request.getPhoneNumber())) {
      validateArgument(isPhoneNumberValid(request.getPhoneNumber()), ErrorCode.PHONE_NUMBER_IS_NOT_VALID.getMessage());
    } else {
      request.setPhoneNumber(null);
    }

    account.setPhoneNumber(request.getPhoneNumber());
    account.setUpdatedBy(account.getId());
    account.setUpdatedDate(LocalDateTime.now());

    accountRepositoryService.save(account);
  }

  @Override
  public void updateAccountPasswordByUsername(String username, UpdateAccountPasswordRequest request) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    StringUtil.trimStrings(request);

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateState(StringUtils.isNotBlank(request.getOldPassword()),
        ErrorCode.OLD_PASSWORD_MUST_NOT_BE_BLANK.getMessage());
    validateState(StringUtils.isNotBlank(request.getNewPassword()),
        ErrorCode.NEW_PASSWORD_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getNewPassword()
        .length() >= 10, ErrorCode.PASSWORD_MINIMAL_LENGTH_IS_10.getMessage());
    validateState(StringUtils.isNotBlank(request.getConfirmNewPassword()),
        ErrorCode.CONFIRM_PASSWORD_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getNewPassword()
        .equals(request.getConfirmNewPassword()), ErrorCode.CONFIRM_PASSWORD_IS_DIFFERENT_WITH_PASSWORD.getMessage());
    validateArgument(passwordEncoder.matches(request.getOldPassword(), account.getPassword()),
        ErrorCode.OLD_PASSWORD_IS_WRONG.getMessage());

    account.setPassword(passwordEncoder.encode(request.getNewPassword()));
    account.setUpdatedBy(account.getId());
    account.setUpdatedDate(LocalDateTime.now());

    accountRepositoryService.save(account);
  }

  @Override
  public void initDummyAccounts() {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule()); // map birthDate to LocalDate
    ClassPathResource accountJson = new ClassPathResource(DUMMY_REQUESTS_PATH);

    try {
      List<CreateAccountRequest> requests = mapper.readValue(accountJson.getInputStream(), new TypeReference<>() {});
      List<Account> accounts = requests.stream()
          .map(this::convertToAccount)
          .collect(Collectors.toList());
      accountRepositoryService.saveAll(accounts);
    } catch (IOException e) {
      log.error("#initDummyAccounts ERROR! with accountJson: {}, and error: {}", accountJson, e.getMessage(), e);
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  @Override
  public void follow(AccountRelationshipRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFollowedId()),
        ErrorCode.FOLLOWED_ID_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFollowerId()),
        ErrorCode.FOLLOWER_ID_MUST_NOT_BE_BLANK.getMessage());

    Account followerAccount = accountRepositoryService.findByIdAndMarkForDeleteFalse(request.getFollowerId());
    validateAccount(followerAccount, ExceptionMessage.FOLLOWER_ACCOUNT_NOT_FOUND);

    Account followedAccount = accountRepositoryService.findByIdAndMarkForDeleteFalse(request.getFollowedId());
    validateAccount(followedAccount, ExceptionMessage.FOLLOWED_ACCOUNT_NOT_FOUND);

    AccountRelationship relationship = new AccountRelationship();
    relationship.setFollower(followerAccount);
    relationship.setFollowed(followedAccount);

    LocalDateTime now = LocalDateTime.now();
    relationship.setCreatedBy(followerAccount.getId());
    relationship.setCreatedDate(now);
    relationship.setUpdatedBy(followerAccount.getId());
    relationship.setUpdatedDate(now);

    followerAccount.setFollowingCount(followerAccount.getFollowingCount() + 1);
    followedAccount.setFollowersCount(followedAccount.getFollowersCount() + 1);

    accountRelationshipRepositoryService.save(relationship);
    accountRepositoryService.saveAll(List.of(followerAccount, followedAccount));
  }

  @Override
  public void unfollow(AccountRelationshipRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFollowedId()),
        ErrorCode.FOLLOWED_ID_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFollowerId()),
        ErrorCode.FOLLOWER_ID_MUST_NOT_BE_BLANK.getMessage());

    Account followerAccount = accountRepositoryService.findByIdAndMarkForDeleteFalse(request.getFollowerId());
    validateAccount(followerAccount, ExceptionMessage.FOLLOWER_ACCOUNT_NOT_FOUND);

    Account followedAccount = accountRepositoryService.findByIdAndMarkForDeleteFalse(request.getFollowedId());
    validateAccount(followedAccount, ExceptionMessage.FOLLOWED_ACCOUNT_NOT_FOUND);

    AccountRelationship relationship =
        accountRelationshipRepositoryService.findByFollowerIdAndFollowedId(request.getFollowerId(),
            request.getFollowedId());

    if (Objects.nonNull(relationship)) {
      accountRelationshipRepositoryService.deleteById(relationship.getId());

      followerAccount.setFollowingCount(followerAccount.getFollowingCount() - 1);
      followedAccount.setFollowersCount(followedAccount.getFollowersCount() - 1);

      accountRepositoryService.saveAll(List.of(followerAccount, followedAccount));
    }
  }

  @Override
  public Page<Account> getAccountFollowers(String username, int pageNumber, int pageSize) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    return accountRepositoryService.findFollowers(account.getId(), PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Page<Account> getAccountFollowing(String username, int pageNumber, int pageSize) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    return accountRepositoryService.findFollowing(account.getId(), PageRequest.of(pageNumber, pageSize));
  }

  private boolean isPhoneNumberValid(String phoneNumber) {

    String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(phoneNumber)
        .matches();
  }

  private boolean validateBioLength(String bio) {

    if (Objects.isNull(bio) || bio.isBlank()) {
      return true;
    }
    return bio.length() < 100;
  }

  private Account convertToAccount(CreateAccountRequest request) {

    Account existingAccount = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(request.getUsername());
    if (Objects.nonNull(existingAccount)) {
      throw new ConflictException(ExceptionMessage.USERNAME_IS_TAKEN);
    }

    existingAccount = accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(request.getEmailAddress());
    if (Objects.nonNull(existingAccount)) {
      throw new ConflictException(ExceptionMessage.EMAIL_IS_ASSOCIATED_WITH_AN_ACCOUNT);
    }

    Account account = new Account();
    BeanUtils.copyProperties(request, account);

    account.setPassword(passwordEncoder.encode(request.getPassword()));
    account.setRole(Role.USER);
    account.setTweets(Collections.EMPTY_LIST);
    account.setFollowers(Collections.EMPTY_LIST);
    account.setFollowing(Collections.EMPTY_LIST);
    account.setSentMessages(Collections.EMPTY_LIST);
    account.setReceivedMessages(Collections.EMPTY_LIST);
    account.setNotifications(Collections.EMPTY_LIST);

    LocalDateTime now = LocalDateTime.now();
    account.setCreatedBy("system");
    account.setCreatedDate(now);
    account.setUpdatedBy("system");
    account.setUpdatedDate(now);

    return account;
  }
}
