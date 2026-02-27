package com.assignment.aop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Persistence entity representing a User record in the primary SQL database This class acts as the
 * Source in the data synchronization It maps directly to the 'users' table.
 *
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
