package com.assignment.aop.controller;

import com.assignment.aop.model.UserAddRequest;
import com.assignment.aop.model.UserAddResponse;
import com.assignment.aop.service.UserAddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller responsible for handling user-related operations.

 * This controller exposes endpoints for creating new users.
 * It delegates business logic to {@link UserAddService}.

 * Base URL: /user
 *
  * @author Ansh Parnami
 * @since 2026-02-26
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserAddService userAddService;


    /**
     * Creates a new user.
     * This endpoint accepts a {@link UserAddRequest} object in the request body
     * and returns a {@link UserAddResponse} containing the result of the operation.
     * @param userAddRequest the request object containing user details to be created
     * @return UserAddResponse containing the created user information or status details
     */
    @PostMapping
    public UserAddResponse addUser(@RequestBody UserAddRequest userAddRequest){
        return userAddService.addUser(userAddRequest);
    }

}
