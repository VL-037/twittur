package vincentlow.twittur.controller;

import static vincentlow.twittur.utils.ObjectMappingHelper.toDirectMessageResponse;
import static vincentlow.twittur.utils.ValidatorUtil.validatePageableRequest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;
import vincentlow.twittur.model.response.DirectMessageResponse;
import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;
import vincentlow.twittur.service.DirectMessageService;

@Slf4j
@RestController
@RequestMapping(value = ApiPath.DIRECT_MESSAGE, produces = MediaType.APPLICATION_JSON_VALUE)
public class DirectMessageController extends BaseController {

  private final int DEFAULT_PAGE_SIZE = 2; // will return DEFAULT_PAGE_SIZE*2 messages

  @Autowired
  private DirectMessageService directMessageService;

  @PostMapping("/{senderId}/{recipientId}")
  public ApiSingleResponse<DirectMessageResponse> sendDirectMessage(@PathVariable String senderId,
      @PathVariable String recipientId, @RequestBody DirectMessageRequest request) {

    try {
      DirectMessage directMessage = directMessageService.sendMessage(senderId, recipientId, request);
      DirectMessageResponse response = toDirectMessageResponse(directMessage);

      return toSuccessApiResponse(response);
    } catch (Exception e) {
      log.error("#sendDirectMessage ERROR! with senderId: {}, recipientId: {}, request: {}, and error: {}", senderId,
          recipientId, request, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @GetMapping("/{senderId}/{recipientId}")
  public ApiListResponse<DirectMessageResponse> getDirectMessages(@PathVariable String senderId,
      @PathVariable String recipientId,
      @RequestParam(defaultValue = "0") int pageNumber) {

    try {
      validatePageableRequest(pageNumber, DEFAULT_PAGE_SIZE);

      Page<DirectMessage> directMessages =
          directMessageService.getDirectMessages(senderId, recipientId, pageNumber, DEFAULT_PAGE_SIZE);
      List<DirectMessageResponse> response = directMessages.stream()
          .map(directMessage -> toDirectMessageResponse(directMessage))
          .collect(Collectors.toList());
      PageMetaData pageMetaData = getPageMetaData(directMessages, pageNumber, DEFAULT_PAGE_SIZE);

      return toSuccessApiResponse(response, pageMetaData);
    } catch (Exception e) {
      log.error("#getDirectMessages ERROR! with senderId: {}, recipientId: {}, pageNumber: {}, and error: {}", senderId,
          recipientId, pageNumber, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
