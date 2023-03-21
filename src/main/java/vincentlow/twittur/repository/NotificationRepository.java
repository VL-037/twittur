package vincentlow.twittur.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vincentlow.twittur.model.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
}
