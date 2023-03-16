package vincentlow.twittur.model.response.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import vincentlow.twittur.model.response.BaseResponse;
import vincentlow.twittur.model.wrapper.PageMetaData;

@Data
@AllArgsConstructor
public class ApiListResponse<T extends BaseResponse> extends ApiResponse {

  private static final long serialVersionUID = -7883416372482519260L;

  private List<T> content;

  private PageMetaData pageMetaData;

  @Override
  public String toString() {

    return "ListResponse{" +
        "content=" + content +
        ", pageMetaData=" + pageMetaData +
        '}';
  }
}
