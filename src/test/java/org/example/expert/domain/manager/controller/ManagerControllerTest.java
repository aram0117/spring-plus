package org.example.expert.domain.manager.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ManagerControllerTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ManagerRepository managerRepository;

    private Long saveManagerUser;
    private long saveTodoId;
    private long saveManager;
    private String token;


    @BeforeEach
    void setUp() {
        // 담당자를 등록할 유저
        User user = new User("test@naver.com", "test1111", UserRole.USER, "홍길동");
        userRepository.save(user);

        // 담당자로 등록될 유저
        User managerUser = new User("test2@naver.com", "test2222", UserRole.USER,"심청");
        userRepository.save(managerUser);
        saveManagerUser = managerUser.getId();

        // 할일 등록
        Todo todo = new Todo("제목테스트", "내용테스트", "날씨테스트", user);
        todoRepository.save(todo);
        saveTodoId = todo.getId();

        // 등록된 담당자
        Manager manager = new Manager(managerUser, todo);
        managerRepository.save(manager);

        saveManager = manager.getId();

        // 유저 토큰 발급
        token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole(), user.getNickname());
    }

    @Test
    @DisplayName("등록된 할일의 담당자 등록")
    void todoManager_saveTest_success() throws Exception {

        String requestBody = String.format(
                """
                     {
                        "managerUserId" : %d
                     }
                """, saveManagerUser
        );
        mockMvc.perform(post("/todos/{todoId}/managers", saveTodoId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("등록된 할일의 담당자 조회")
    void todoManager_getTest_success() throws Exception {

        mockMvc.perform(get("/todos/{todoId}/managers", saveTodoId)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("등록된 할일의 담당자 삭제")
    void todoManager_deleteTest_success() throws Exception {

        mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", saveTodoId, saveManager)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }
}