package com.assignment.aop.service;

import com.assignment.aop.model.UserEntity;
import com.assignment.aop.model.UserAddRequest;
import com.assignment.aop.model.UserAddResponse;
import com.assignment.aop.repository.UserRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service class responsible for managing user creation logic.
 * This class handles the transformation of incoming request DTOs into
 * persistent JPA entities for SQL Server storage.
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Service
public class UserAddService {
    @Autowired
    UserRepository userRepository;

    /**
     * Processes the addition of a new user to the primary SQL database.
     * This method's call to userRepository.save() is intercepted by
     * the MongoSyncAspect to perform secondary synchronization to MongoDB.
     *
     * @param userAddRequest DTO containing user details.
     * @return userAddResponse containing the generated ID and persisted user details.
     */
    public UserAddResponse addUser(UserAddRequest userAddRequest) {
        // map the request DTO to a persistent Entity
        UserEntity u = UserEntity.builder()
                .username(userAddRequest.getUsername())
                .email(userAddRequest.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Persist to SQL Server (MongoSyncAspect triggers after this)
        UserEntity savedUser = userRepository.save(u);

        UserAddResponse userAddResponse = new UserAddResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());


        return userAddResponse;
    }

}
