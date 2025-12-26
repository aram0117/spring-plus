package org.example.expert.domain.comment.controller;

import org.example.expert.config.JwtUtil;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private long savetodo;
    private String token;

    @BeforeEach
    void setUp() {

        // 유저
        User user = new User("test@naver.com", "test1111", UserRole.USER, "홍길동");
        userRepository.save(user);

        // 유저가 만든 할일
        Todo todo = new Todo("text title", "test contents", "test weather", user);
        todoRepository.save(todo);

        savetodo = todo.getId();


        token = jwtUtil.createToken(user.getId(),user.getNickname(),user.getUserRole(),user.getEmail());
    }

    @Test
    @DisplayName("등록된 할일의 댓글 등록")
    void todoComment_saveTest_success() throws Exception {

        String requestBody =
                """
                    {
                       "contents" : "댓글 테스트"
                    }
                """;

        mockMvc.perform(post("/todos/{todoId}/comments", savetodo)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("등록된 할일의 댓글 조회")
    void todoComment_getTest_success() throws Exception {

        mockMvc.perform(get("/todos/{todoId}/comments", savetodo)
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }
}