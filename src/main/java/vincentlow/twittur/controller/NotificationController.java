package vincentlow.twittur.controller;

import static vincentlow.twittur.utils.ObjectMappingHelper.toResponse;
import static vincentlow.twittur.utils.ValidatorUtil.validatePageableRequest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.model.request.GetNotificationRequest;
import vincentlow.twittur.model.response.NotificationResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;
import vincentlow.twittur.service.NotificationService;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController extends BaseController {

  private final int DEFAULT_PAGE_SIZE = 10;

  @Autowired
  private NotificationService notificationService;

  @GetMapping
  public ApiListResponse<NotificationResponse> getNotifications(
      @RequestBody GetNotificationRequest request,
      @RequestParam(defaultValue = "0") int pageNumber) {

    try {
      validatePageableRequest(pageNumber, DEFAULT_PAGE_SIZE);

      Page<Notification> notifications =
          notificationService.getNotifications(request.getRecipientId(), pageNumber, DEFAULT_PAGE_SIZE);
      List<NotificationResponse> response = notifications.stream()
          .map(notification -> toResponse(notification, NotificationResponse.class))
          .collect(Collectors.toList());
      PageMetaData pageMetaData = getPageMetaData(notifications, pageNumber, DEFAULT_PAGE_SIZE);

      return toSuccessApiResponse(response, pageMetaData);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
