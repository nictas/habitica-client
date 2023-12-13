package com.nictas.habitica.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.nictas.habitica.client.csv.TasksCsvParser;
import com.nictas.habitica.client.domain.CreateUserTaskResponse;
import com.nictas.habitica.client.domain.Task;

public class Main {

    private static final int MAX_RETRIES = 5;

    public static void main(String[] args) throws InterruptedException {
        HabiticaClient client = createClient();

        List<Task> tasks = getTasks(args);
        List<CreateUserTaskResponse> createdTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            createdTasks.add(createTask(client, task));
        }

        for (int i = createdTasks.size() - 1; i >= 0; i--) {
            completeTask(client, createdTasks.get(i));
        }
    }

    private static List<Task> getTasks(String[] args) {
        List<String> tasksCsv = getTasksCsv(args);
        return new TasksCsvParser().parseCsv(tasksCsv);
    }

    private static List<String> getTasksCsv(String[] args) {
        Path tasksFilePath = getTasksFilePath(args);
        System.out.println("Tasks file path: " + tasksFilePath);
        return getLines(tasksFilePath);
    }

    private static HabiticaClient createClient() {
        Configuration configuration = new Configuration();
        String username = configuration.getUsername();
        System.out.println("Using username: " + username);
        String key = configuration.getKey();
        System.out.println("Using key: " + key);
        return new HabiticaClientFactory().createClient(username, key);
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

    private static CreateUserTaskResponse createTask(HabiticaClient client, Task task) {
        for (int i = 0; i < MAX_RETRIES + 1; i++) {
            try {
                if (i == 0) {
                    System.out.printf("Creating task \"%s\"... ", task.getText());
                } else {
                    System.out.printf("Creating task \"%s\" (retry %d of %d)... ", task.getText(), i, MAX_RETRIES);
                }
                CreateUserTaskResponse createdTask = client.createUserTask(task);
                System.out.printf("OK!%n");
                return createdTask;
            } catch (WebClientResponseException e) {
                System.err.printf("%nCreating task \"%s\" failed with: (%d %s) %s%n", task.getText(), e.getRawStatusCode(),
                                  e.getStatusText(), e.getResponseBodyAsString());
                if (e.getStatusCode()
                     .is5xxServerError()) {
                    continue;
                } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    System.out.printf("%nSleeping for 65 seconds to reset the Habitica rate limit...%n");
                    sleepQuietly(65000);
                    continue;
                } else {
                    System.exit(1);
                }
            }
        }
        System.err.printf("All %d retries failed!", MAX_RETRIES);
        System.exit(1);
        throw new IllegalStateException();
    }

    private static void sleepQuietly(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.err.println("Sleeping failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private static CreateUserTaskResponse completeTask(HabiticaClient client, CreateUserTaskResponse createdTask) {
        CreateUserTaskResponse.Data data = createdTask.getData();
        for (int i = 0; i < MAX_RETRIES + 1; i++) {
            try {
                if (i == 0) {
                    System.out.printf("Completing task \"%s\"... ", data.getText());
                } else {
                    System.out.printf("Completing task \"%s\" (retry %d of %d)... ", data.getText(), i, MAX_RETRIES);
                }
                client.completeTask(data.getId());
                System.out.printf("OK!%n");
                return createdTask;
            } catch (WebClientResponseException e) {
                System.err.printf("%nCompleting task \"%s\" failed with: (%d %s) %s%n", data.getText(), e.getRawStatusCode(),
                                  e.getStatusText(), e.getResponseBodyAsString());
                if (e.getStatusCode()
                     .is5xxServerError()) {
                    continue;
                } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    System.out.printf("%nSleeping for 65 seconds to reset the Habitica rate limit...%n");
                    sleepQuietly(65000);
                    continue;
                } else {
                    System.exit(1);
                }
            }
        }
        System.err.printf("All %d retries failed!", MAX_RETRIES);
        System.exit(1);
        throw new IllegalStateException();
    }

}
