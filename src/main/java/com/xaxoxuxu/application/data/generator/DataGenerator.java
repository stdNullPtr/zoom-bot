package com.xaxoxuxu.application.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;
import com.xaxoxuxu.application.data.Role;
import com.xaxoxuxu.application.data.entity.User;
import com.xaxoxuxu.application.data.service.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringComponent
public class DataGenerator
{

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository)
    {
        return args ->
        {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L)
            {
                logger.info("Using existing database");
                return;
            }
            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");

            User admin = new User();
            admin.setName("Shubham Kumar Sinha");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://www.upwork.com/profile-portraits/c1feREPYOvZYxLmzyecEu12ok01A13xBCOo4_BWj0lpB3oxlbDMVmtxcubJj1GRNv7");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);

            logger.info("Generated demo data");
        };
    }

}