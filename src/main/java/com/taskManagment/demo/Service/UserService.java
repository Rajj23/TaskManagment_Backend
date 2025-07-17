package com.taskManagment.demo.Service;

import com.taskManagment.demo.DTO.Login.AuthResponse;
import com.taskManagment.demo.DTO.Login.LoginRequest;
import com.taskManagment.demo.DTO.Login.RegisterRequest;
import com.taskManagment.demo.DTO.User.UserResponse;
import com.taskManagment.demo.Entity.User;

import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    User findByUsername(String username);
    List<UserResponse> getAllUsers();
}
