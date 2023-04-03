package vincentlow.twittur.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.service.EmailService;
import vincentlow.twittur.service.SchedulerService;

@Slf4j
@Service
public class SchedulerServiceImpl implements SchedulerService {

  @Autowired
  private EmailService emailService;

  @Override
  public Pair<Integer, Integer> resendFailedEmails() {

    return emailService.resendFailedEmails();
  }
}
