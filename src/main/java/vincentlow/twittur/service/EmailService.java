package vincentlow.twittur.service;

import org.springframework.data.util.Pair;

import vincentlow.twittur.model.entity.Email;
import vincentlow.twittur.model.request.EmailRequest;

public interface EmailService {

  Email sendEmail(EmailRequest request);

  Pair<Integer, Integer> resendFailedEmails();
}
