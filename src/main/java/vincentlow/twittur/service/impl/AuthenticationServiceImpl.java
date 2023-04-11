package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.constant.Role;
import vincentlow.twittur.model.constant.TokenType;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Token;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.LoginRequest;
import vincentlow.twittur.model.response.AuthenticationResponse;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.ForbiddenException;
import vincentlow.twittur.repository.TokenRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.AuthenticationService;
import vincentlow.twittur.service.JWTService;
import vincentlow.twittur.utils.StringUtil;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JWTService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Override
  public AuthenticationResponse register(CreateAccountRequest request) {

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

    account.setPassword(passwordEncoder.encode(request.getPassword()));
    account.setRole(Role.USER);
    account.setTokens(Collections.EMPTY_LIST);
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

    accountRepositoryService.save(account);

    String jwtToken = jwtService.generateToken(account);
    saveAccountToken(account, jwtToken, now);

    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .build();
  }

  @Override
  public AuthenticationResponse login(LoginRequest request) {

    try {
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      Account account =
          accountRepositoryService.findByUsernameOrEmailAddressAndMarkForDeleteFalse(request.getUsername());
      String jwtToken = jwtService.generateToken(account);

      revokeAllAccountToken(account); // Don't want multiple valid tokens when login
      saveAccountToken(account, jwtToken, LocalDateTime.now());

      return AuthenticationResponse.builder()
          .accessToken(jwtToken)
          .build();
    } catch (AuthenticationException e) {
      log.error("#login ERROR! with request: {}, and error: {}", request, e.getMessage(), e);
      throw new ForbiddenException(e.getMessage());
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

  private int getCurrentAge(LocalDate dateOfBirth) {

    LocalDate currentDate = LocalDate.now();
    return Period.between(dateOfBirth, currentDate)
        .getYears();
  }

  private boolean validateBioLength(String bio) {

    if (Objects.isNull(bio) || bio.isBlank()) {
      return true;
    }
    return bio.length() < 100;
  }

  private boolean isPhoneNumberValid(String phoneNumber) {

    String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(phoneNumber)
        .matches();
  }

  private void saveAccountToken(Account account, String jwtToken, LocalDateTime now) {

    Token token = new Token();
    token.setAccount(account);
    token.setToken(jwtToken);
    token.setType(TokenType.BEARER);
    token.setCreatedBy("system");
    token.setCreatedDate(now);
    token.setUpdatedBy("system");
    token.setUpdatedDate(now);

    tokenRepository.save(token);
  }

  private void revokeAllAccountToken(Account account) {

    List<Token> validAccountTokens = tokenRepository.findAllValidTokensByAccountId(account.getId());
    if (!validAccountTokens.isEmpty()) {
      validAccountTokens.forEach(token -> {
        token.setExpired(true);
        token.setRevoked(true);
      });
      tokenRepository.saveAll(validAccountTokens);
    }
  }
}
