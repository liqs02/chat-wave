package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(User user){
        if(userRepository.findByLoginName(user.getLoginName()).isPresent())
            throw new ResponseStatusException(CONFLICT, "User with given loginName already exists");


        var encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);

        userRepository.save(user);
        log.info("new user has been created: " + user.getId() + ". " +  user.getLoginName());

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public User authenticateUser(User user) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getLoginName(),
                            user.getPassword()
                    )
            );
        } catch(Exception e) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid password");
        }

        log.info("User has been authenticated: " + user.getId() + ". " +  user.getLoginName());
        return userRepository.findByLoginName(user.getLoginName()).get();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserPassword(Integer userId, String newPassword) {
        var foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with given id does not exist"));

        var encoded = passwordEncoder.encode(newPassword);
        foundUser.setPassword(encoded);

        userRepository.save(foundUser);
    }
}
