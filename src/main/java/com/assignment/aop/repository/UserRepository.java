package com.assignment.aop.repository;

import com.assignment.aop.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository representing a User record in the primary SQL database.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {}
