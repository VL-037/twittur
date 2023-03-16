package vincentlow.twittur.model.entity;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "account")
@Data
@JsonIgnoreProperties(value = {"tweets"})
public class Account extends BaseEntity {

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "date_of_birth")
  private Date dateOfBirth;

  @Column(name = "username")
  private String username;

  @Column(name = "account_name")
  private String accountName;

  @Column(name = "bio")
  private String bio;

  @Column(name = "email_address")
  private String emailAddress;

  @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
  private List<Tweet> tweets;

  private int tweetsCount;

  @PrePersist
  public void prePersist() {

    super.prePersist();
  }
}
