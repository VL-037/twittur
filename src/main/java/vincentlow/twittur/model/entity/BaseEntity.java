package vincentlow.twittur.model.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(generator = "uuid-sequence")
  @GenericGenerator(name = "uuid-sequence", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  protected String id;

  @Column(name = "created_by")
  protected String createdBy;

  @Column(name = "created_date")
  protected Date createdDate;

  @Column(name = "updated_by")
  protected String updatedBy;

  @Column(name = "updated_date")
  protected Date updatedDate;

  @Column(name = "mark_for_delete")
  protected boolean markForDelete;

  @Override
  public String toString() {

    return "BaseEntity{" +
        "id='" + id + '\'' +
        ", createdBy='" + createdBy + '\'' +
        ", createdDate=" + createdDate +
        ", updatedBy='" + updatedBy + '\'' +
        ", updatedDate=" + updatedDate +
        ", markForDelete=" + markForDelete +
        '}';
  }
}
