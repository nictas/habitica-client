package com.nictas.habitica.client;

import org.springframework.web.reactive.function.client.WebClient;

import com.nictas.habitica.client.domain.Task;

public class Client {

  private static final String PATH_TASKS_USER = "/tasks/user";

  private final WebClient webClient;

  public Client(WebClient webClient) {
    this.webClient = webClient;
  }

  public void createUserTask(Task task) {
    webClient.post().uri(PATH_TASKS_USER).bodyValue(task).retrieve().toBodilessEntity().block();
  }

}
