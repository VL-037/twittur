package vincentlow.twittur.model.constant;

public interface ExceptionMessage {

  String PAGE_NUMBER_MUST_BE_AT_LEAST_0 = "page number must be at least 0";

  String PAGE_SIZE_MUST_BE_BETWEEN_1_AND_100 = "page size must be between 1 and 100";

  String AUTHENTICATION_FAILED = "authentication failed";

  String SERVICE_TEMPORARILY_UNAVAILABLE = "service temporarily unavailable";

  String ACCOUNT_NOT_FOUND = "account not found";

  String FOLLOWED_ACCOUNT_NOT_FOUND = "followed account not found";

  String FOLLOWER_ACCOUNT_NOT_FOUND = "follower account not found";

  String SENDER_ACCOUNT_NOT_FOUND = "sender account not found";

  String RECIPIENT_ACCOUNT_NOT_FOUND = "recipient account not found";

  String USERNAME_IS_TAKEN = "username is taken";

  String EMAIL_IS_ASSOCIATED_WITH_AN_ACCOUNT = "email is associated with an account, please login or reset password";

  String TWEET_NOT_FOUND = "tweet not found";
}
