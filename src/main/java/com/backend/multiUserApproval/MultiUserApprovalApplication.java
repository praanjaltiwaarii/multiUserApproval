package com.backend.multiUserApproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories(basePackages = "com.backend.multiUserApproval.repository")
@EntityScan(basePackages = "com.backend.multiUserApproval.model.db")
public class MultiUserApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiUserApprovalApplication.class, args);
	}

	@Bean(name = "emailExecutor")
	public ThreadPoolTaskExecutor emailExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("EmailThread-");
		executor.initialize();
		return executor;
	}

}
