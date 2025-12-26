package org.example.expert.domain.user.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private long saveUser;
    private String token;

    @BeforeEach
    void setUp() {

        // 조회할 유저
        User user = new User("test@naver.com", passwordEncoder.encode("test1111"), UserRole.USER, "홍길동");
        userRepository.save(user);

        saveUser = user.getId();

        token = jwtUtil.createToken(user.getId(), user.getNickname(), user.getUserRole(), user.getEmail());


    }

    @Test
    @DisplayName("등록된 유저 조회 테스트")
    void user_getTest_success() throws Exception {

        mockMvc.perform(get("/users/{userId}", saveUser)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("등록된 유저 비밀번호 변경")
    void userPassword_updateTest_success() throws Exception {

        String requestBody =
                """
                       {
                         "oldPassword" : "test1111",
                         "newPassword" : "Test2222"
                       }
                """;
        mockMvc.perform(put("/users")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
}