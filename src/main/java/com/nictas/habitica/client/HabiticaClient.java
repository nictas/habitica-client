package com.nictas.habitica.client;

import org.springframework.web.reactive.function.client.WebClient;

import com.nictas.habitica.client.domain.CreateUserTaskResponse;
import com.nictas.habitica.client.domain.Task;

public class HabiticaClient {

    private static final String PATH_TASKS_USER = "/tasks/user";
    private static final String PATH_TASKS_SCORE = "/tasks/%s/score/up";

    private final WebClient webClient;

    public HabiticaClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public CreateUserTaskResponse createUserTask(Task task) {
        return webClient.post()
                        .uri(PATH_TASKS_USER)
                        .bodyValue(task)
                        .retrieve()
                        .toEntity(CreateUserTaskResponse.class)
                        .block()
                        .getBody();
    }

    public void completeTask(String id) {
        webClient.post()
                 .uri(String.format(PATH_TASKS_SCORE, id))
                 .retrieve()
                 .toEntity(String.class)
                 .block();
    }

}
