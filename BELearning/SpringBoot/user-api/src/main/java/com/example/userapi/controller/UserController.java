package com.example.userapi.controller;

import com.example.userapi.dto.PatchUserDTO;
import com.example.userapi.dto.UserDTO;
import com.example.userapi.model.User;
import com.example.userapi.service.IUserService;
import com.example.userapi.validators.EmailValidator;
import com.example.userapi.validators.UserPatchValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "User API", description = "Operations related to user management")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get users in a paginated way", description = "Retrieves a page of users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Page<User>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Adds a new user to the system. Username and email must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "422", description = "Missing required fields",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userRequest) throws Exception {
        User newUser = new User(userRequest.getUsername(), userRequest.getEmail(), userRequest.getPassword(),
                             userRequest.getFirstname(), userRequest.getLastname());
        User createdUser = userService.addUser(newUser);
        return ResponseEntity.status(201).body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user", description = "Updates the details of an existing user by ID. Username and email must remain unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "422", description = "Missing required fields",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userRequest) throws Exception {
        User userToUpdate = new User(id, userRequest.getUsername(), userRequest.getEmail(), userRequest.getPassword(),
                userRequest.getFirstname(), userRequest.getLastname());
        User updatedUser = userService.updateUser(userToUpdate);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id) throws Exception {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a user", description = "Updates specified fields of a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Username or email already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "422", description = "Missing required fields",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<User> patchUser(
            @Parameter(description = "ID of the user to patch", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PatchUserDTO patchUserDTO) throws Exception {

        UserPatchValidator.validatePatch(patchUserDTO);

        User patchedUser = userService.patchUser(id, patchUserDTO);
        return ResponseEntity.ok(patchedUser);
    }
}