package vincentlow.twittur.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import vincentlow.twittur.model.constant.DirectMessageStatus;

@Entity
@Table(name = "direct_message")
@Data
public class DirectMessage extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "sender_id", referencedColumnName = "id")
  private Account sender;

  @ManyToOne
  @JoinColumn(name = "recipient_id", referencedColumnName = "id")
  private Account recipient;

  @Column(name = "message")
  private String message;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private DirectMessageStatus status;

  @JsonIgnore
  public Account getSender() {

    return sender;
  }

  @JsonIgnore
  public Account getRecipient() {

    return recipient;
  }
}
