package com.assignment.aop.repository;

import com.assignment.aop.model.UserDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends MongoRepository<UserDoc, String> {

}