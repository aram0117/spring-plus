package org.example.expert.domain.todo.repository;


import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.springframework.data.domain.Page;

public interface TodoCustomRepository {

    Page<TodoResponse> searchTodoByMultiCondition(TodoSearchRequest request);

}
