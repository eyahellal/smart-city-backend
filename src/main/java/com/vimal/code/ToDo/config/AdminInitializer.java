package com.vimal.code.ToDo.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.vimal.code.ToDo.models.UserEnitiy;
import com.vimal.code.ToDo.models.Role;
import com.vimal.code.ToDo.Repositories.UserRepo;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin exists
        if (userRepo.findByRole(Role.ADMIN).isEmpty()) {
            UserEnitiy admin = new UserEnitiy();
            admin.setName("Admin");
            admin.setEmail("admin@smartcity.com"); // Change as needed
            admin.setPassword(passwordEncoder.encode("admin123")); // Change & Hash password
            admin.setRole(Role.ADMIN);

            userRepo.save(admin);
            System.out.println("✅ Admin account created successfully.");
        } else {
            System.out.println("✅ Admin account already exists.");
        }
    }
}
