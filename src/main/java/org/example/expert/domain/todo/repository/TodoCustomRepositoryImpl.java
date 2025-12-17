package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodoResponse> searchTodoByMultiCondition(TodoSearchRequest request) {

        BooleanBuilder builder = new BooleanBuilder();

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        if (request.getWeather() != null && !request.getWeather().isBlank()) {
            builder.and(todo.weather.eq(request.getWeather()));
        }
        if (request.getStartAt() != null && request.getEndAt() != null && request.getStartAt().isBefore(request.getEndAt())) {
            builder.and(todo.modifiedAt.between(request.getStartAt(), request.getEndAt()));
        }
        List<Todo> results = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(builder)
                .orderBy(todo.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<TodoResponse> todoDto = results.stream()
                .map(t -> new TodoResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getContents(),
                        t.getWeather(),
                        new UserResponse(t.getUser().getId(), t.getUser().getEmail()),
                        t.getCreatedAt(),
                        t.getModifiedAt()
                ))
                .toList();


        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(todoDto, pageable, total != null ? total : 0L);
    }
}
