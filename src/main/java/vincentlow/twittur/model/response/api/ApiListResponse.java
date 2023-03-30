package vincentlow.twittur.model.response.api;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vincentlow.twittur.model.response.BaseResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;

@Data
@NoArgsConstructor // default const for JACKSON
@AllArgsConstructor
public class ApiListResponse<T extends BaseResponse> extends ApiResponse {

  private static final long serialVersionUID = -7883416372482519260L;

  private List<T> content;

  private PageMetaData pageMetaData;

  public ApiListResponse(HttpStatus httpStatus, String error, List<T> content, PageMetaData pageMetaData) {

    super(httpStatus, error);
    this.content = content;
    this.pageMetaData = pageMetaData;
  }

  @Override
  public String toString() {

    return "ListResponse{" +
        "content=" + content +
        ", pageMetaData=" + pageMetaData +
        '}';
  }
}
