package com.nictas.habitica.client;

import org.springframework.web.reactive.function.client.WebClient;

import com.nictas.habitica.client.domain.Task;

public class Client {

  static final String HEADER_X_API_USER = "x-api-user";
  static final String HEADER_X_API_KEY = "x-api-key";
  static final String HEADER_X_API_CLIENT = "x-api-client";
  static final String HEADER_X_API_CLIENT_SUFFIX = "-Testing";

  private static final String PATH_TASKS_USER = "/tasks/user";

  private final WebClient webClient;

  public Client(WebClient webClient) {
    this.webClient = webClient;
  }

  public void createUserTask(Task task) {
    webClient.post().uri(PATH_TASKS_USER).bodyValue(task).retrieve().toBodilessEntity().block();
  }

}
