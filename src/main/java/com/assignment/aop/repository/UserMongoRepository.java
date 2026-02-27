package com.assignment.aop.repository;

import com.assignment.aop.model.UserDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB Repository representation of a User.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Repository
public interface UserMongoRepository extends MongoRepository<UserDoc, String> {}
