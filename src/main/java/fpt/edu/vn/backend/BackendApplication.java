package fpt.edu.vn.backend;

import fpt.edu.vn.backend.repository.UserRepository;
import fpt.edu.vn.backend.service.impl.AuthenticationService;
import fpt.edu.vn.backend.auth.RegisterRequest;
import fpt.edu.vn.backend.service.impl.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

import static fpt.edu.vn.backend.entity.Role.ADMIN;
import static fpt.edu.vn.backend.entity.Role.MANAGER;

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
					.email("admin@mail.com")
					.pass("password")
					.knowAs("Admin")
					.gender("Male")
					.dateOfBirth(LocalDate.of(2003, 1, 30))
					.role(ADMIN)
					.build();

			if (!userRepository.existsByEmail(admin.getEmail())) {
				System.out.println("Admin token: " + service.register(admin).getJwt());
			} else {
				System.out.println("Admin already exists");
			}

			var manager = RegisterRequest.builder()
					.firstName("Manager")
					.lastName("huythu2")
					.email("manager@mail.com")
					.pass("password")
					.knowAs("manager")
					.gender("Male")
					.dateOfBirth(LocalDate.of(2002, 12, 30))
					.role(MANAGER)
					.build();

			if (!userRepository.existsByEmail(manager.getEmail())) {
				System.out.println("Manager token: " + service.register(manager).getJwt());
			} else {
				System.out.println("Manager already exists");
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
