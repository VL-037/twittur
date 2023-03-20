package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;
import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.micrometer.common.util.StringUtils;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.AccountRelationship;
import vincentlow.twittur.model.request.AccountRelationshipRequest;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;
import vincentlow.twittur.repository.AccountRelationshipRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.AccountService;
import vincentlow.twittur.utils.StringUtil;

@Service
public class AccountServiceImpl implements AccountService {

  private final String DUMMY_REQUESTS_PATH = "dummy_requests/accounts.json";

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Autowired
  private AccountRelationshipRepository accountRelationshipRepository;

  @Override
  public Account createAccount(CreateAccountRequest request) {

    StringUtil.trimStrings(request);
    validateRequest(request);

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

    String salt = BCrypt.gensalt();
    String hashedPassword = BCrypt.hashpw(request.getPassword(), salt);

    account.setSalt(salt);
    account.setPassword(hashedPassword);
    account.setTweets(Collections.EMPTY_LIST);
    account.setFollowers(Collections.EMPTY_LIST);
    account.setFollowing(Collections.EMPTY_LIST);

    LocalDateTime now = LocalDateTime.now();
    account.setCreatedBy("system");
    account.setCreatedDate(now);
    account.setUpdatedBy("system");
    account.setUpdatedDate(now);

    return accountRepositoryService.save(account);
  }

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
  public Account updateAccountByUsername(String username, UpdateAccountRequest request) {

    Account account = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(username);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    StringUtil.trimStrings(request);
    validateRequest(request);

    if (!request.getUsername()
        .equals(account.getUsername())) {
      Account existingAccount = accountRepositoryService.findByUsernameAndMarkForDeleteFalse(account.getUsername());
      if (Objects.nonNull(existingAccount)) {
        throw new ConflictException(ExceptionMessage.USERNAME_IS_TAKEN);
      }
    }

    if (!request.getEmailAddress()
        .equals(account.getEmailAddress())) {
      Account existingAccount =
          accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(account.getEmailAddress());
      if (Objects.nonNull(existingAccount)) {
        throw new ConflictException(ExceptionMessage.EMAIL_IS_ASSOCIATED_WITH_AN_ACCOUNT);
      }
    }

    String oldSalt = account.getSalt();
    String oldHashedPassword = account.getPassword();
    String hashedPassword = BCrypt.hashpw(request.getOldPassword(), oldSalt);

    validateArgument(hashedPassword.equals(oldHashedPassword), ErrorCode.OLD_PASSWORD_IS_WRONG.getMessage());

    String newSalt = BCrypt.gensalt();
    String newHashedPassword = BCrypt.hashpw(request.getNewPassword(), newSalt);

    BeanUtils.copyProperties(request, account);
    account.setSalt(newSalt);
    account.setPassword(newHashedPassword);
    account.setUpdatedBy(account.getId());
    account.setUpdatedDate(LocalDateTime.now());

    return accountRepositoryService.save(account);
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

    accountRelationshipRepository.save(relationship);
    accountRepositoryService.saveAll(List.of(followerAccount, followedAccount));
  }

  @Override
  public void unfollow(AccountRelationshipRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFollowedId()),
        ErrorCode.FOLLOWED_ID_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFollowerId()),
        ErrorCode.FOLLOWER_ID_MUST_NOT_BE_BLANK.getMessage());

    AccountRelationship relationship =
        accountRelationshipRepository.findByFollowerIdAndFollowedId(request.getFollowerId(), request.getFollowedId());

    if (Objects.nonNull(relationship)) {
      accountRelationshipRepository.deleteById(relationship.getId());

      Account followerAccount = accountRepositoryService.findByIdAndMarkForDeleteFalse(request.getFollowerId());
      validateAccount(followerAccount, ExceptionMessage.FOLLOWER_ACCOUNT_NOT_FOUND);

      Account followedAccount = accountRepositoryService.findByIdAndMarkForDeleteFalse(request.getFollowedId());
      validateAccount(followedAccount, ExceptionMessage.FOLLOWED_ACCOUNT_NOT_FOUND);

      followerAccount.setFollowingCount(followerAccount.getFollowingCount() - 1);
      followedAccount.setFollowersCount(followedAccount.getFollowersCount() - 1);

      accountRepositoryService.saveAll(List.of(followerAccount, followedAccount));
    }
  }

  private void validateRequest(CreateAccountRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getFirstName()),
        ErrorCode.FIRST_NAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getFirstName()
        .length() <= 50, ErrorCode.FIRST_NAME_MAXIMAL_LENGTH_IS_50.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getLastName()), ErrorCode.LAST_NAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getLastName()
        .length() <= 50, ErrorCode.LAST_NAME_MAXIMAL_LENGTH_IS_50.getMessage());
    validateState(Objects.nonNull(request.getDateOfBirth()), ErrorCode.DATE_OF_BIRTH_MUST_NOT_BE_NULL.getMessage());
    validateArgument(getCurrentAge(request.getDateOfBirth()) >= 13, ErrorCode.AGE_MUST_BE_AT_LEAST_13.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getUsername()), ErrorCode.USERNAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getUsername()
        .length() >= 5, ErrorCode.USERNAME_MINIMAL_LENGTH_IS_5.getMessage());
    validateArgument(request.getUsername()
        .length() <= 15, ErrorCode.USERNAME_MAXIMAL_LENGTH_IS_15.getMessage());
    validateArgument(validateBioLength(request.getBio()), ErrorCode.BIO_MAXIMAL_LENGTH_IS_100.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getEmailAddress()),
        ErrorCode.EMAIL_ADDRESS_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getEmailAddress()
        .length() <= 62, ErrorCode.EMAIL_ADDRESS_MAXIMAL_LENGTH_IS_62.getMessage());

    if (StringUtils.isNotBlank(request.getPhoneNumber())) {
      validateArgument(isPhoneNumberValid(request.getPhoneNumber()), ErrorCode.PHONE_NUMBER_IS_NOT_VALID.getMessage());
    } else {
      request.setPhoneNumber(null);
    }

    validateState(StringUtils.isNotBlank(request.getPassword()), ErrorCode.PASSWORD_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getPassword()
        .length() >= 10, ErrorCode.PASSWORD_MINIMAL_LENGTH_IS_10.getMessage());
    validateState(StringUtils.isNotBlank(request.getConfirmPassword()),
        ErrorCode.CONFIRM_PASSWORD_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getPassword()
        .equals(request.getConfirmPassword()), ErrorCode.CONFIRM_PASSWORD_IS_DIFFERENT_WITH_PASSWORD.getMessage());
  }

  private void validateRequest(UpdateAccountRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getUsername()), ErrorCode.USERNAME_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getUsername()
        .length() >= 5, ErrorCode.USERNAME_MINIMAL_LENGTH_IS_5.getMessage());
    validateArgument(request.getUsername()
        .length() <= 15, ErrorCode.USERNAME_MAXIMAL_LENGTH_IS_15.getMessage());
    validateArgument(validateBioLength(request.getBio()), ErrorCode.BIO_MAXIMAL_LENGTH_IS_100.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getEmailAddress()),
        ErrorCode.EMAIL_ADDRESS_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(request.getEmailAddress()
        .length() <= 62, ErrorCode.EMAIL_ADDRESS_MAXIMAL_LENGTH_IS_62.getMessage());

    if (StringUtils.isNotBlank(request.getPhoneNumber())) {
      validateArgument(isPhoneNumberValid(request.getPhoneNumber()), ErrorCode.PHONE_NUMBER_IS_NOT_VALID.getMessage());
    } else {
      request.setPhoneNumber(null);
    }

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
  }

  private boolean isPhoneNumberValid(String phoneNumber) {

    String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(phoneNumber)
        .matches();
  }

  private int getCurrentAge(LocalDate dateOfBirth) {

    LocalDate currentDate = LocalDate.now();
    return Period.between(dateOfBirth, currentDate)
        .getYears();
  }

  private boolean validateBioLength(String bio) {

    if (bio.isBlank()) {
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

    String salt = BCrypt.gensalt();
    String hashedPassword = BCrypt.hashpw(request.getPassword(), salt);

    account.setSalt(salt);
    account.setPassword(hashedPassword);
    account.setTweets(Collections.EMPTY_LIST);
    account.setFollowers(Collections.EMPTY_LIST);
    account.setFollowing(Collections.EMPTY_LIST);

    LocalDateTime now = LocalDateTime.now();
    account.setCreatedBy("system");
    account.setCreatedDate(now);
    account.setUpdatedBy("system");
    account.setUpdatedDate(now);

    return account;
  }
}
