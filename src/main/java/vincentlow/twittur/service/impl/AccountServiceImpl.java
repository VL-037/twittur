package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;
import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
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

import io.micrometer.common.util.StringUtils;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.UpdateAccountRequest;
import vincentlow.twittur.model.response.exception.BadRequestException;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;
import vincentlow.twittur.repository.AccountRepository;
import vincentlow.twittur.service.AccountService;
import vincentlow.twittur.utils.StringUtil;

@Service
public class AccountServiceImpl implements AccountService {

  private final String DUMMY_REQUESTS_PATH = "dummy_requests/accounts.json";

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public Account createAccount(CreateAccountRequest request) {

    StringUtil.trimStrings(request);
    validateRequest(request);

    Account existingAccount = accountRepository.findByUsername(request.getUsername());
    if (Objects.nonNull(existingAccount)) {
      throw new ConflictException(ExceptionMessage.USERNAME_IS_TAKEN);
    }

    existingAccount = accountRepository.findByEmailAddress(request.getEmailAddress());
    if (Objects.nonNull(existingAccount)) {
      throw new ConflictException(ExceptionMessage.EMAIL_IS_ASSOCIATED_WITH_AN_ACCOUNT);
    }

    String salt = BCrypt.gensalt();
    String hashedPassword = BCrypt.hashpw(request.getPassword(), salt);

    Account account = convertToAccount(request);
    account.setSalt(salt);
    account.setPassword(hashedPassword);
    account.setTweets(Collections.EMPTY_LIST);

    Date now = new Date();
    account.setCreatedBy("system");
    account.setCreatedDate(now);
    account.setUpdatedBy("system");
    account.setUpdatedDate(now);

    return accountRepository.save(account);
  }

  @Override
  public Page<Account> findAccounts(int pageNumber, int pageSize) {

    return accountRepository.findAll(PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Account findAccountByUsername(String username) {

    Account account = accountRepository.findByUsername(username);;
    return validateAccount(account);
  }

  @Override
  public Account updateAccountByUsername(String username, UpdateAccountRequest request) {

    Account account = accountRepository.findByUsername(username);
    validateAccount(account);

    StringUtil.trimStrings(request);
    validateRequest(request);

    if (!request.getUsername()
        .equals(account.getUsername())) {
      Account existingAccount = accountRepository.findByUsername(account.getUsername());
      if (Objects.nonNull(existingAccount)) {
        throw new ConflictException(ExceptionMessage.USERNAME_IS_TAKEN);
      }
    }

    if (!request.getEmailAddress()
        .equals(account.getEmailAddress())) {
      Account existingAccount = accountRepository.findByEmailAddress(account.getEmailAddress());
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
    account.setUpdatedDate(new Date());

    return accountRepository.save(account);
  }

  @Override
  public void initDummyAccounts() {

    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource accountJson = new ClassPathResource(DUMMY_REQUESTS_PATH);

    try {
      List<CreateAccountRequest> requests =
          mapper.readValue(accountJson.getInputStream(), new TypeReference<>() {});
      List<Account> accounts = requests.stream()
          .map(request -> convertToAccount(request))
          .collect(Collectors.toList());
      accountRepository.saveAll(accounts);
    } catch (IOException e) {
      throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
    }
  }

  private Account convertToAccount(CreateAccountRequest request) {

    Account account = new Account();
    account.setFirstName(request.getFirstName());
    account.setLastName(request.getLastName());
    account.setDateOfBirth(request.getDateOfBirth());
    account.setUsername(request.getUsername());
    account.setAccountName(request.getAccountName());
    account.setBio(request.getBio());
    account.setEmailAddress(request.getEmailAddress());
    account.setPhoneNumber(request.getPhoneNumber());
    account.setPassword(request.getPassword());
    return account;
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
    // validateArgument(getCurrentAge(request.getDateOfBirth()) >= 13, ErrorCode.AGE_MUST_BE_AT_LEAST_13.getMessage());
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

  // private int getCurrentAge(Date dateOfBirth) {
  //
  // LocalDate currentDate = LocalDate.now();
  // LocalDate birthDate = dateOfBirth.toLocalDate();
  // return Period.between(birthDate, currentDate)
  // .getYears();
  // }

  private boolean validateBioLength(String bio) {

    if (bio.isBlank()) {
      return true;
    }
    return bio.length() < 100;
  }
}
