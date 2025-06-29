package com.library.notification.repository;

import com.library.notification.model.NotificationTemplate;
import com.library.notification.repository.custom.NotificationTemplateRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>, NotificationTemplateRepositoryCustom {

    List<NotificationTemplate> findByName(String name);
}
