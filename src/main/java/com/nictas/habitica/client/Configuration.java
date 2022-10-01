package com.nictas.habitica.client;

public class Configuration {

  private static final String ENV_USERNAME = "HABITICA_USERNAME";
  private static final String ENV_KEY = "HABITICA_KEY";

  public static String getUsername() {
    return getEnv(ENV_USERNAME);
  }

  public static String getKey() {
    return getEnv(ENV_KEY);
  }

  private static String getEnv(String name) {
    String value = System.getenv(name);
    if (value == null || value.isEmpty()) {
      throw new IllegalStateException(String.format("Environment variable %s is not set", name));
    }
    return value;
  }

}
