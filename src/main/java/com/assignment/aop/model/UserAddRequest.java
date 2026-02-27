package com.assignment.aop.model;


import lombok.Data;

/**
 * Data Transfer Object (DTO) for handling incoming user creation requests.
 * This class captures the JSON payload from the API client before it is
 * transformed into a persistent database entity.
 * @author Ansh Parnami
 * @since 2026-02-26
 */
@Data
public class UserAddRequest {
    String username;
    String email;
}
