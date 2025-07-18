package com.example.java_mail_example;

import com.example.java_mail_example.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class JavaMailExampleApplication {

	private static final Logger logger = LoggerFactory.getLogger(JavaMailExampleApplication.class); // Correct logger for this class

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(JavaMailExampleApplication.class, args);

		MailService mailService = context.getBean(MailService.class);
		mailService.sendEmail(
				"mail@example.com", // Replace with a valid recipient for testing
				"Test Subject",
				"Hello, this is a test email from Spring Boot context!"
		);
		logger.info("Email sending process initiated. Check logs for details.");
	}

}
