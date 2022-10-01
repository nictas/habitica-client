package com.nictas.habitica.client;

import org.springframework.web.reactive.function.client.WebClient;

public class ClientFactory {

  private static final String HEADER_X_API_USER = "x-api-user";
  private static final String HEADER_X_API_KEY = "x-api-key";
  private static final String HEADER_X_API_CLIENT = "x-api-client";
  private static final String HEADER_X_API_CLIENT_SUFFIX = "-Testing";

  private static final String BASE_URL = "https://habitica.com/api/v3";

  public Client createClient(String username, String key) {
    WebClient webClient = WebClient.builder() //
        .baseUrl(BASE_URL) //
        .defaultHeader(HEADER_X_API_CLIENT, username + HEADER_X_API_CLIENT_SUFFIX) //
        .defaultHeader(HEADER_X_API_USER, username) //
        .defaultHeader(HEADER_X_API_KEY, key)//
        .build();
    return new Client(webClient);
  }

}
