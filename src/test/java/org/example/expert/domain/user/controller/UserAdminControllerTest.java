package org.example.expert.domain.user.controller;

import org.example.expert.config.JwtUtil;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken;
    private long userId;

    private String adminToken;
    private long adminId;

    @BeforeEach
    void setUp() {

        // 일반 유저
        User user = new User("test@naver.com", "test1111", UserRole.USER, "홍길동");
        userRepository.save(user);

        userId = user.getId();

        userToken = jwtUtil.createToken(user.getId(), user.getNickname(), user.getUserRole(), user.getEmail());

        // 관리자
        User admin = new User("test2@naver.com", "test2222", UserRole.ADMIN, "심청");
        userRepository.save(admin);

        adminId = admin.getId();

        adminToken = jwtUtil.createToken(admin.getId(), admin.getNickname(), admin.getUserRole(), admin.getEmail());
    }

    @Test
    @DisplayName("일반 유저의 접근방지")
    void user_userRoleTest_forbidden() throws Exception {

        String requestBody =
                """
                            {
                               "role" : "ADMIN"
                            }
                """;

        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 유저의 권한 변경")
    void admin_userRoleTest_success() throws Exception {

        String requestBody =
                """
                      {
                         "role" : "USER"
                      }
                """;

        mockMvc.perform(patch("/admin/users/{userId}", adminId)
                .header(HttpHeaders.AUTHORIZATION, adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }
}