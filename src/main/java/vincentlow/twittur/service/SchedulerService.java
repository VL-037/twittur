package vincentlow.twittur.service;

import org.springframework.data.util.Pair;

public interface SchedulerService {

  Pair<Integer, Integer> resendFailedEmails();
}
