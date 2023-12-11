package com.nictas.habitica.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.nictas.habitica.client.csv.TaskCsvParser;
import com.nictas.habitica.client.domain.Task;

public class Main {

	private static final int MAX_RETRIES = 5;

	public static void main(String[] args) throws InterruptedException {
		Client client = createClient();

		List<Task> tasks = getTasks(args);
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			createTask(client, task);
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

	private static void createTask(Client client, Task task) {
		for (int i = 0; i < MAX_RETRIES; i++) {
			try {
				System.out.printf("Creating task \"%s\"... ", task.getText());
				client.createUserTask(task);
				System.out.printf("OK!%n");
				return;
			} catch (WebClientResponseException e) {
				System.err.printf("%nCreating task \"%s\" failed with: (%d %s) %s%n", task.getText(),
						e.getRawStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
				if (e.getStatusCode().is5xxServerError()) {
					System.out.printf("Retry %d out of %d: ", i + 1, MAX_RETRIES);
					continue;
				} else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
					System.out.printf("Sleeping for 65 seconds to reset the Habitica rate limit...%n");
					sleepQuietly(65000);
					System.out.printf("Retry %d out of %d: ", i + 1, MAX_RETRIES);
					continue;
				} else {
					System.exit(1);
				}
			}
		}
	}

	private static void sleepQuietly(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			System.err.println("Sleeping failed: " + e.getMessage());
			System.exit(1);
		}
	}

}
