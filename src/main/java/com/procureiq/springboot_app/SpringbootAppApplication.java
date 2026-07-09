package com.procureiq.springboot_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.Instant;
import com.procureiq.springboot_app.features.notifications.entity.NotificationType;
import com.procureiq.springboot_app.features.notifications.repository.NotificationTypeRepository;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class SpringbootAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAppApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner seedDatabase(NotificationTypeRepository typeRepository) {
		return args -> {
			try {
				if (typeRepository.findByCode("system_alert").isEmpty()) {
					NotificationType type = new NotificationType();
					type.setCode("system_alert");
					type.setCategory("transactional");
					type.setDefaultPriority((short) 3);
					type.setDefaultChannels(new String[]{"push", "email"});
					type.setFanOutMode("write");
					type.setCreatedAt(Instant.now());
					type.setUpdatedAt(Instant.now());
					typeRepository.save(type);
					System.out.println("[SEED] Successfully seeded 'system_alert' notification type!");
				}
			} catch (Exception e) {
				System.out.println("[SEED] Seeding skipped or already present: " + e.getMessage());
			}
		};
	}
}
