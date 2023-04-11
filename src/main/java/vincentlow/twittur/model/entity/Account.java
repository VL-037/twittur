package vincentlow.twittur.model.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import vincentlow.twittur.model.constant.Role;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class Account extends BaseEntity implements UserDetails {

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

  @JsonIgnore
  @Column(name = "phone_number")
  private String phoneNumber;

  @JsonIgnore
  @Column(name = "password")
  private String password;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @JsonIgnore
  @OneToMany(mappedBy = "account") // 1 account have many tokens
  private List<Token> tokens;

  @JsonIgnore
  @OneToMany(mappedBy = "creator")
  private List<Tweet> tweets;

  @JsonIgnore
  @OneToMany(mappedBy = "followed")
  private List<AccountRelationship> followers;

  @JsonIgnore
  @OneToMany(mappedBy = "follower")
  private List<AccountRelationship> following;

  @JsonIgnore
  @OneToMany(mappedBy = "sender")
  private List<DirectMessage> sentMessages;

  @JsonIgnore
  @OneToMany(mappedBy = "recipient")
  private List<DirectMessage> receivedMessages;

  @JsonIgnore
  @OneToMany(mappedBy = "recipient")
  private List<Notification> notifications;

  @Column(name = "tweets_count")
  private int tweetsCount;

  @Column(name = "followers_count")
  private int followersCount;

  @Column(name = "following_count")
  private int followingCount;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public boolean isAccountNonExpired() {

    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {

    return true;
  }

  @Override
  public boolean isEnabled() {

    return true;
  }
}
