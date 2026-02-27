package com.assignment.aop.adapter;

import com.assignment.aop.model.UserDoc;
import com.assignment.aop.model.UserEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * This class adapts the user entity to user doc specifically.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Component
public class UserToMongoAdapter implements DataSyncAdapter<UserDoc, UserEntity> {

  public UserDoc sync(UserEntity s) {
    UserDoc doc =
        UserDoc.builder()
            .mongoId(s.getId().toString())
            .userName(s.getUsername())
            .email(s.getEmail())
            .syncTimestamp(LocalDateTime.now())
            .build();

    return doc;
  }
}
