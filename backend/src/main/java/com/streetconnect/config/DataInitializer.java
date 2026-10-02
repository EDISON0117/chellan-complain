package com.streetconnect.config;

import com.streetconnect.entity.Category;
import com.streetconnect.entity.Role;
import com.streetconnect.entity.Street;
import com.streetconnect.entity.User;
import com.streetconnect.repository.CategoryRepository;
import com.streetconnect.repository.StreetRepository;
import com.streetconnect.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StreetRepository streetRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           StreetRepository streetRepository,
                           CategoryRepository categoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.streetRepository = streetRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure upload directory exists
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
                logger.info("Created upload directory: {}", dir.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.warn("Could not create uploads directory: {}", e.getMessage());
        }

        // Initialize Categories if empty
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = Arrays.asList(
                    new Category(null, "Street Light", "Issues related to non-functional or flickering street lamps", "bi-lightbulb"),
                    new Category(null, "Garbage & Waste", "Overflowing dustbins, uncollected waste, illegal dumping", "bi-trash"),
                    new Category(null, "Road & Pavement", "Potholes, damaged tar, missing paver blocks, unsafe walkways", "bi-signpost-split"),
                    new Category(null, "Drainage & Sewage", "Clogged storm drains, overflowing sewage, foul smell", "bi-droplet-half"),
                    new Category(null, "Water Supply", "Low pressure, broken pipeline, contaminated drinking water", "bi-water"),
                    new Category(null, "Electricity & Wiring", "Hanging live wires, transformer sparking, power pole lean", "bi-lightning-charge"),
                    new Category(null, "Public Safety", "Lack of surveillance, stray animals, unauthorized blockades", "bi-shield-exclamation"),
                    new Category(null, "Sanitation & Cleaning", "Unswept streets, stagnant puddles, mosquito breeding", "bi-recycle"),
                    new Category(null, "Traffic & Parking", "Illegal parking blocking access, missing speed breakers", "bi-cone-striped"),
                    new Category(null, "Other Local Issues", "Park maintenance, general civic concerns", "bi-three-dots")
            );
            categoryRepository.saveAll(defaultCategories);
            logger.info("Initialized default categories");
        }

        // Initialize Streets if empty
        Street defaultStreet = null;
        if (streetRepository.count() == 0) {
            Street s1 = new Street(null, "Gandhi Street", "Tambaram", "Chennai", "600045", "Tamil Nadu");
            Street s2 = new Street(null, "Bharathi Nagar Main Road", "Velachery", "Chennai", "600042", "Tamil Nadu");
            Street s3 = new Street(null, "Anna Salai 3rd Cross", "Guindy", "Chennai", "600032", "Tamil Nadu");
            streetRepository.saveAll(Arrays.asList(s1, s2, s3));
            defaultStreet = s1;
            logger.info("Initialized default streets");
        } else {
            defaultStreet = streetRepository.findAll().get(0);
        }

        // Initialize Admin if not present or sync default passwords
        String defaultResidentHash = passwordEncoder.encode("password123");
        String defaultAdminHash = passwordEncoder.encode("admin123");

        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if ("admin@streetconnect.com".equalsIgnoreCase(u.getEmail())) {
                u.setPassword(defaultAdminHash);
            } else {
                u.setPassword(defaultResidentHash);
            }
        }
        if (!allUsers.isEmpty()) {
            userRepository.saveAll(allUsers);
            logger.info("Synchronized BCrypt passwords for all {} seed users (admin123 / password123)", allUsers.size());
        }

        if (!userRepository.existsByEmail("admin@streetconnect.com")) {
            User admin = new User();
            admin.setFullName("Suresh Kumar (Admin)");
            admin.setEmail("admin@streetconnect.com");
            admin.setMobileNumber("9840112233");
            admin.setPassword(defaultAdminHash);
            admin.setRole(Role.ROLE_ADMIN);
            admin.setStreet(defaultStreet);
            admin.setIsActive(true);
            admin.setProfileImage("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80");
            userRepository.save(admin);
            logger.info("Created default administrator account: admin@streetconnect.com / admin123");
        }
    }
}
