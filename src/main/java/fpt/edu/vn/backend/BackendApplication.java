package fpt.edu.vn.backend;

import fpt.edu.vn.backend.repository.UserRepository;
import fpt.edu.vn.backend.service.impl.AuthenticationService;
import fpt.edu.vn.backend.dto.RegisterRequest;
import fpt.edu.vn.backend.service.impl.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

import static fpt.edu.vn.backend.entity.Role.*;

@SpringBootApplication
public class BackendApplication {
	@Autowired
	private EmailSenderService emailSenderService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service,
			UserRepository userRepository
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.firstName("Administrator")
					.lastName("Huy")
					.email("admin@gmail.com")
					.pass("password")
					.knowAs("Admin")
					.gender("Male")
					.dateOfBirth(LocalDate.of(2003, 1, 30))
					.role(ADMIN)
					.build();

			if (!userRepository.existsByEmail(admin.getEmail())) {
				System.out.println(service.register(admin).getJwt());
			}

			var manager = RegisterRequest.builder()
					.firstName("Manager")
					.lastName("huythu2")
					.email("manager@gmail.com")
					.pass("password")
					.knowAs("manager")
					.gender("Male")
					.dateOfBirth(LocalDate.of(2002, 12, 30))
					.role(MANAGER)
					.build();

			if (!userRepository.existsByEmail(manager.getEmail())) {
				System.out.println(service.register(manager).getJwt());
			}

			var user1 = RegisterRequest.builder()
					.firstName("user1")
					.lastName("user1")
					.email("user1@gmail.com")
					.pass("password")
					.knowAs("user1")
					.gender("female")
					.dateOfBirth(LocalDate.of(2002, 12, 30))
					.role(USER)
					.build();

			if (!userRepository.existsByEmail(user1.getEmail())) {
				System.out.println(service.register(user1).getJwt());
			}

			var user2 = RegisterRequest.builder()
					.firstName("user2")
					.lastName("user2")
					.email("user2@gmail.com")
					.pass("password")
					.knowAs("user2")
					.gender("female")
					.dateOfBirth(LocalDate.of(2002, 12, 30))
					.role(USER)
					.build();

			if (!userRepository.existsByEmail(user2.getEmail())) {
				System.out.println(service.register(user2).getJwt());
			}
		};
	}

//	@EventListener(ApplicationReadyEvent.class)
//	public void sendEmail() {
//		emailSenderService.sendEmail(
//				"huylmth2107022@fpt.edu.vn",
//				"This is Subject",
//				"This is Body"
//		);
//	}
}
