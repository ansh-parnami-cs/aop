package com.assignment.aop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for outgoing user creation responses. This class returns the details
 * of the successfully persisted user back to the client, including the auto-generated primary key
 * from the SQL database.
 *
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddResponse {
  long id;
  String username;
  String email;
}
