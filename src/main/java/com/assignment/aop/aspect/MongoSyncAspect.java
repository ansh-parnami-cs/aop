package com.assignment.aop.aspect;

import com.assignment.aop.adapter.DataSyncAdapter;
import com.assignment.aop.model.UserDoc;
import com.assignment.aop.model.UserEntity;
import com.assignment.aop.repository.UserMongoRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Aspect class responsible for synchronizing relational database UserEntity data into MongoDB after
 * a successful save operation.
 *
 * <p>This uses Spring AOP to intercept the save() method execution of UserRepository and replicate
 * the data into MongoDB.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Aspect
@Component
public class MongoSyncAspect {
  @Autowired UserMongoRepository userMongoRepository;

  @Autowired DataSyncAdapter<UserDoc, UserEntity> dataSyncAdapter;

  /**
   * Advice that runs AFTER the successful execution of: UserRepository.save(..)
   *
   * @param userEntity returned user entity from UserService save user
   */
  @AfterReturning(
      pointcut = "execution(* com.assignment.aop.repository.UserRepository.save(..))",
      returning = "userEntity")
  public void afterUserSave(UserEntity userEntity) {
    // Use the adapter to convert, then use mongoTemplate to save

    UserDoc doc = dataSyncAdapter.sync(userEntity);
    userMongoRepository.save(doc);
    System.out.println("Syncing to MongoDB: " + doc.getUserName());
  }
}
