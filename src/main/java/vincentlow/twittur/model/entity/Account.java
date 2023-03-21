package vincentlow.twittur.model.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class Account extends BaseEntity {

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Column(name = "username")
  private String username;

  @Column(name = "account_name")
  private String accountName;

  @Column(name = "bio")
  private String bio;

  @Column(name = "email_address")
  private String emailAddress;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "salt")
  private String salt;

  @Column(name = "password")
  private String password;

  @OneToMany(mappedBy = "creator")
  private List<Tweet> tweets;

  @OneToMany(mappedBy = "followed")
  private List<AccountRelationship> followers;

  @OneToMany(mappedBy = "follower")
  private List<AccountRelationship> following;

  @OneToMany(mappedBy = "sender")
  private List<DirectMessage> sentMessages;

  @OneToMany(mappedBy = "recipient")
  private List<DirectMessage> receivedMessages;

  @OneToMany(mappedBy = "recipient")
  private List<Notification> notifications;

  @Column(name = "tweets_count")
  private int tweetsCount;

  @Column(name = "followers_count")
  private int followersCount;

  @Column(name = "following_count")
  private int followingCount;

  @JsonIgnore
  public List<Tweet> getTweets() {

    return tweets;
  }

  @JsonIgnore
  public List<AccountRelationship> getFollowers() {

    return followers;
  }

  @JsonIgnore
  public List<AccountRelationship> getFollowing() {

    return following;
  }

  @JsonIgnore
  public List<DirectMessage> getSentMessages() {

    return sentMessages;
  }

  @JsonIgnore
  public List<DirectMessage> getReceivedMessages() {

    return receivedMessages;
  }
}
