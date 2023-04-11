package vincentlow.twittur.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import vincentlow.twittur.model.constant.TokenType;

@Entity
@Table(name = "token")
@Data
public class Token extends BaseEntity {

  private static final long serialVersionUID = 3637737372229598739L;

  @Column(name = "token")
  private String token;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TokenType type;

  @Column(name = "is_expired")
  private boolean expired;

  @Column(name = "is_revoked")
  private boolean revoked; // revoke when restart app

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;
}
