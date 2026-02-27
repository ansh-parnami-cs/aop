package com.assignment.aop.model;



import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * MongoDB Document representation of a User.
 * This class serves as the Target model for the AOP-driven synchronization.
 * It defines how relational SQL user data is structured when archived in NoSQL.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users_archive")
public class UserDoc {

    @Id
    private String mongoId;

    @Field("user_name")
    private String userName;

    private String email;

    /** Timestamp recording exactly when this document was synced via AOP. */
    private LocalDateTime syncTimestamp;
}