package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import java.util.Optional;


public interface TodoCustomRepository {

    Page<TodoResponse> searchTodoByMultiCondition(TodoSearchRequest request);

    Optional<Todo> findByIdWithUser(long todoId);

}
