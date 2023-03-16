package vincentlow.twittur.model.response;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse implements Serializable {

  private static final long serialVersionUID = -2829404744875698664L;

  private String id;

  private Date createdDate;

  private String createdBy;

  private Date updatedDate;

  private String updatedBy;
}
