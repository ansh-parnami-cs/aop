package com.assignment.aop.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * Persistence entity representing a User record in the primary SQL database
 * This class acts as the Source in the data synchronization
 * It maps directly to the 'users' table
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
