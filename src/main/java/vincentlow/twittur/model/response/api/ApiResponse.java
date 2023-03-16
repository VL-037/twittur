package vincentlow.twittur.model.response.api;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse implements Serializable {

  private static final long serialVersionUID = -8810877986413094433L;

  private String error;
}
