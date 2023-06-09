package vincentlow.twittur.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "account_relationship")
@Data
public class AccountRelationship extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "follower_id", referencedColumnName = "id")
  private Account follower;

  @ManyToOne
  @JoinColumn(name = "followed_id", referencedColumnName = "id")
  private Account followed;

  @JsonIgnore
  public Account getFollower() {

    return follower;
  }

  @JsonIgnore
  public Account getFollowed() {

    return followed;
  }
}
