package com.taskManagment.demo.Service.Implemention;

import com.taskManagment.demo.DTO.Login.AuthResponse;
import com.taskManagment.demo.DTO.Login.LoginRequest;
import com.taskManagment.demo.DTO.Login.RegisterRequest;
import com.taskManagment.demo.DTO.User.UserResponse;
import com.taskManagment.demo.Entity.Role;
import com.taskManagment.demo.Entity.User;
import com.taskManagment.demo.Repo.UserRepo;
import com.taskManagment.demo.Security.JwtService;
import com.taskManagment.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request){
        if(userRepo.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepo.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @Override
    public User findByUsername(String username){
        return userRepo.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(user->new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name()
                ))
                .collect(Collectors.toList());
    }
}
