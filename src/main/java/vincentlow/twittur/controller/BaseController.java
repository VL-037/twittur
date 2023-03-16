package vincentlow.twittur.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import vincentlow.twittur.model.response.api.ApiListResponse;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;

public class BaseController {

  protected ApiResponse successResponse = new ApiResponse(HttpStatus.OK, null);

  protected <T> ApiListResponse toSuccessApiResponse(List<T> content, PageMetaData pageMetaData) {

    return new ApiListResponse(HttpStatus.OK, null, content, pageMetaData);
  }

  protected <T> ApiSingleResponse toSuccessApiResponse(T data) {

    return new ApiSingleResponse(HttpStatus.OK, null, data);
  }

  protected ApiResponse toErrorApiResponse(HttpStatus httpStatus, String error) {

    return new ApiResponse(httpStatus, error);
  }

  protected PageMetaData getPageMetaData(Page page, int pageNumber, int pageSize) {

    return PageMetaData.builder()
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalRecords(page.getTotalElements())
        .build();
  }

}
