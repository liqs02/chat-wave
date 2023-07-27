package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@DisplayName("UserRepository")
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    @DisplayName("save() and findById()")
    public void findUserById() {
        var user = new User();
        user.setId(1);
        user.setPassword("pass");
        repository.save(user);

        var exists = repository.findById(user.getId());
        if(exists.isEmpty()) fail();

        var foundUser = exists.get();
        assertEquals(1, foundUser.getId());
        assertEquals("pass", foundUser.getPassword());
    }

}
