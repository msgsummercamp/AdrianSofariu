package com.example.userapi.controller;

import com.example.userapi.dto.*;
import com.example.userapi.exception.ClashingUserException;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get users in a paginated way", description = "Retrieves a page of users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of users returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "204", description = "No users found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<UserResponseDTO>> getUsers(Pageable pageable) {
        Page<User> userPage = userService.getUsers(pageable);
        Page<UserResponseDTO> userResponseDTOPage = userPage.map(UserResponseMapper::toUserResponseDTO);
        if(userResponseDTOPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userResponseDTOPage.getContent());
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
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO userRequest) throws ClashingUserException  {
        User createdUser = userService.addUser(userRequest);
        UserResponseDTO createdUserResponse = UserResponseMapper.toUserResponseDTO(createdUser);
        return ResponseEntity.status(201).body(createdUserResponse);
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
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO userRequest) throws ClashingUserException, UserNotFoundException {
        User updatedUser = userService.updateUser(userRequest, id);
        UserResponseDTO updatedUserResponse = UserResponseMapper.toUserResponseDTO(updatedUser);
        return ResponseEntity.ok(updatedUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
            @PathVariable Long id) throws UserNotFoundException {
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
    public ResponseEntity<UserResponseDTO> patchUser(
            @Parameter(description = "ID of the user to patch", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PatchUserDTO patchUserDTO) throws ClashingUserException, UserNotFoundException {

        //UserPatchValidator.validatePatch(patchUserDTO);

        User patchedUser = userService.patchUser(id, patchUserDTO);
        UserResponseDTO patchedUserResponse = UserResponseMapper.toUserResponseDTO(patchedUser);
        return ResponseEntity.ok(patchedUserResponse);
    }
}