package com.nictas.habitica.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    Path tasksFilePath = getTasksFilePath(args);
    System.out.println("Tasks file path: " + tasksFilePath);
    getLines(tasksFilePath);
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
