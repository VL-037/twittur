package vincentlow.twittur.service;

import vincentlow.twittur.model.entity.Email;
import vincentlow.twittur.model.request.EmailRequest;

public interface EmailService {

  Email sendEmail(EmailRequest request);
}
