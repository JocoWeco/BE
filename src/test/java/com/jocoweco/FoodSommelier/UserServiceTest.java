package com.jocoweco.FoodSommelier;

import com.jocoweco.FoodSommelier.domain.User;
import com.jocoweco.FoodSommelier.dto.UserRequestDto;
import com.jocoweco.FoodSommelier.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        User user = new User(1L,
                "ysy",
                "jihwan",
                "1234",
                "bbq",
                "kyo");
    }

    @Test
    public void updateUser() throws Exception {
        UserRequestDto req = new UserRequestDto();
        req.setUid(1L);
        req.setUserId("ysy");
        req.setNickName("jihwan");
        req.setUser_pw("12341");
        req.setRecently_store("");
        req.setSaved_store("");

        userService.updateUser(req);
    }

}
