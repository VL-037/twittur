package vincentlow.twittur.model.constant;

public enum ErrorCode {

  // ACCOUNT
  ACCOUNT_MUST_NOT_BE_NULL("account must not be null"),
  FIRST_NAME_MUST_NOT_BE_BLANK("first name must not be blank"),
  FIRST_NAME_MAXIMAL_LENGTH_IS_50("first name maximal length is 50"),
  LAST_NAME_MUST_NOT_BE_BLANK("last name must not be blank"),
  LAST_NAME_MAXIMAL_LENGTH_IS_50("last name maximal length is 50"),
  DATE_OF_BIRTH_MUST_NOT_BE_NULL("date of birth must not be null"),
  AGE_MUST_BE_AT_LEAST_13("age must be at least 13"),
  USERNAME_MUST_NOT_BE_BLANK("username must not be null"),
  USERNAME_MINIMAL_LENGTH_IS_5("username minimal length is 5"),
  USERNAME_MAXIMAL_LENGTH_IS_15("username maximal length is 15"),
  BIO_MAXIMAL_LENGTH_IS_100("bio maximal length is 100"),
  EMAIL_ADDRESS_MUST_NOT_BE_BLANK("email address must not be blank"),
  EMAIL_ADDRESS_MAXIMAL_LENGTH_IS_62("email address maximal length is 62"),

  // TWEET
  TWEET_MUST_NOT_BE_NULL("tweet must not be null"),
  MESSAGE_MUST_NOT_BE_BLANK("message must not be blank"),
  MESSAGE_MAXIMAL_LENGTH_IS_250("message maximal length is 250");

  private String message;

  ErrorCode(String message) {

    this.message = message;
  }

  public String getMessage() {

    return message;
  }
}
