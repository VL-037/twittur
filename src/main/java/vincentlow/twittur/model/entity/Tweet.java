package vincentlow.twittur.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tweet")
@Data
public class Tweet extends BaseEntity {

  @Column(name = "message")
  private String message;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", referencedColumnName = "id")
  private Account creator;

  @PrePersist
  public void prePersist() {

    super.prePersist();
  }
}
