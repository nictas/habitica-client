package com.nictas.habitica.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.nictas.habitica.client.csv.TaskCsvParser;
import com.nictas.habitica.client.domain.Task;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    Client client = createClient();

    List<Task> tasks = getTasks(args);
    for (int i = 0; i < tasks.size(); i++) {
      if (i != 0 && i % 20 == 0) {
        System.out.printf("Sleeping for 60s to avoid exceeding the Habitica rate limit...");
        Thread.sleep(60000);
      }
      Task task = tasks.get(i);
      System.out.printf("Creating user task \"%s\"... ", task.getText());
      client.createUserTask(task);
      System.out.printf("OK!\n");
    }
  }

  private static List<Task> getTasks(String[] args) {
    List<String> tasksCsv = getTasksCsv(args);
    return new TaskCsvParser().parseCsv(tasksCsv);
  }

  private static List<String> getTasksCsv(String[] args) {
    Path tasksFilePath = getTasksFilePath(args);
    System.out.println("Tasks file path: " + tasksFilePath);
    return getLines(tasksFilePath);
  }

  private static Client createClient() {
    String username = Configuration.getUsername();
    System.out.println("Using username: " + username);
    String key = Configuration.getKey();
    System.out.println("Using key: " + key);
    return new ClientFactory().createClient(username, key);
  }

  private static List<String> getLines(Path tasksFilePath) {
    try {
      return Files.readAllLines(tasksFilePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read task file: " + e.getMessage(), e);
    }
  }

  private static Path getTasksFilePath(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("Pass a valid task file path as the first argument to the application");
    }
    String tasksFilePath = args[0];
    if (tasksFilePath == null || tasksFilePath.isEmpty()) {
      throw new IllegalArgumentException("Pass a valid task file path as the first argument to the application");
    }
    try {
      return Paths.get(tasksFilePath);
    } catch (InvalidPathException e) {
      throw new IllegalArgumentException("Task file path is invalid: " + e.getMessage(), e);
    }
  }

}
