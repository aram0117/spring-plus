package org.example.expert.domain.auth.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 로그인할 유저객체
        User user = new User("test2@naver.com", passwordEncoder.encode("test2222"), UserRole.USER, "심청");
        userRepository.save(user);
    }

    @Test
    @DisplayName("유저 회원가입 통합테스트")
    void signUp_test_success() throws Exception {

        String requestBody =
                """
                        {
                        "nickname" : "홍길동",
                        "email" : "test@naver.com",
                        "password" : "aaaa1111",
                        "userRole" : "USER"
                        }
                """;
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("로그인 통합테스트")
    void signin_test_success() throws Exception {

        String requestBody =
                """
                        {
                        "email" : "test2@naver.com",
                        "password" : "test2222"
                        }
                """;

        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

}