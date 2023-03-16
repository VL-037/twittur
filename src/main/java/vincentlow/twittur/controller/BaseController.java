package vincentlow.twittur.controller;

import java.util.List;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;

public class BaseController {

  protected ApiResponse successResponse = new ApiResponse();

  protected <T> ApiListResponse toApiResponse(List<T> content, PageMetaData pageMetaData) {

    return new ApiListResponse(content, pageMetaData);
  }

  protected <T> ApiSingleResponse toApiResponse(T data) {

    return new ApiSingleResponse(data);
  }

  protected PageMetaData getPageMetaData(Page page, int pageNumber, int pageSize) {

    return PageMetaData.builder()
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalRecords(page.getTotalElements())
        .build();
  }

}
