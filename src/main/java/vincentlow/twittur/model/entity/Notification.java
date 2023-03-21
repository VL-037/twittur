package vincentlow.twittur.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import vincentlow.twittur.model.constant.NotificationType;

@Entity
@Table(name = "notification")
@Data
public class Notification extends BaseEntity {

  @Column(name = "sender_id")
  private String senderId; // if "SYSTEM" than it's a system-generated notification, else account id??

  @ManyToOne
  @JoinColumn(name = "recipient_id", referencedColumnName = "id")
  private Account recipient;

  @Column(name = "title")
  private String title;

  @Column(name = "message")
  private String message;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "redirect_url")
  private String redirectUrl;

  @Column(name = "type")
  private NotificationType type;

  @Column(name = "has_read")
  private boolean hasRead;
}
