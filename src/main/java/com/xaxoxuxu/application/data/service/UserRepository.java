package com.xaxoxuxu.application.data.service;

import com.xaxoxuxu.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>
{

    User findByUsername(String username);
}