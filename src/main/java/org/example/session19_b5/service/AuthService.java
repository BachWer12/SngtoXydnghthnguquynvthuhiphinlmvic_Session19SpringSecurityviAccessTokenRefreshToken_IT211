package org.example.session19_b5.service;

import lombok.RequiredArgsConstructor;
import org.example.session19_b5.entity.User;
import org.example.session19_b5.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;


    public User authenticate(String username, String password){
        User user = userRepository.findByUsername(username) .orElseThrow();

        if(!user.getPassword().equals(password)){

            throw new RuntimeException(

                    "Invalid credentials"

            );

        }

        return user;
    }

}
