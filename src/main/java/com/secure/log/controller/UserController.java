package com.secure.log.controller;

import com.secure.log.dto.UserDto;
import com.secure.log.exception.ResourceAlreadyExistsException;
import com.secure.log.exception.ResourceNotFoundException;
import com.secure.log.model.User;
import com.secure.log.response.ApiResponse;
import com.secure.log.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    @GetMapping("/hi")
    public String hi(){
        return "Hi";
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/get")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers().stream()
                .map(userService::convertUserToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("get/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("Success", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            UserDto userDto = userService.convertUserToDto(newUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("User created successfully!", userDto));
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<ApiResponse> updateUser(@RequestParam Long userId,
                                                  @Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(userId, user);
            UserDto userDto = userService.convertUserToDto(updatedUser);
            return ResponseEntity.ok(new ApiResponse("User updated successfully!", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/update-role")
    public ResponseEntity<ApiResponse> updateUserRole(@RequestParam Long userId,
                                                      @RequestParam String roleName) {
        try {
            User updatedUser = userService.updateUserRole(userId, roleName);
            UserDto userDto = userService.convertUserToDto(updatedUser);
            return ResponseEntity.ok(new ApiResponse("User role updated successfully!", userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse("User deleted successfully!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
