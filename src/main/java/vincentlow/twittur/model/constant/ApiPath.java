package vincentlow.twittur.model.constant;

public interface ApiPath {

  String BASE_PATH = "/api/v1";

  String ACCOUNT = BASE_PATH + "/accounts";

  String TWEET = ACCOUNT + "/@{username}/tweets";

  String DIRECT_MESSAGE = BASE_PATH + "/direct-messages";

  String NOTIFICATION = BASE_PATH + "/notifications";

  String EMAIL = BASE_PATH + "/emails";

  String SCHEDULER = BASE_PATH + "/schedulers";
}
