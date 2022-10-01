package com.nictas.habitica.client.csv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nictas.habitica.client.domain.ImmutableTask;
import com.nictas.habitica.client.domain.Task;

public class TaskCsvParser {

  private static final String[] CSV_FORMAT = {"Name", "Description", "Difficulty(t, e, m, h)"};

  public List<Task> parseCsv(List<String> csv) {
    List<Task> tasks = new ArrayList<>();
    for (int i = 0; i < csv.size(); i++) {
      tasks.add(parseLine(i, csv.get(i)));
    }
    return tasks;
  }

  private Task parseLine(int i, String line) {
    String[] lineTokens = line.split(",");
    try {
      return toTask(lineTokens);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(String.format("Unable to parse line %d: %s", i, e.getMessage()), e);
    }
  }

  private Task toTask(String[] lineTokens) {
    if (lineTokens.length != CSV_FORMAT.length) {
      throw new IllegalArgumentException(String.format("Expected %d tokens but got %d. Expected format: %s",
          CSV_FORMAT.length, lineTokens.length, Arrays.toString(CSV_FORMAT)));
    }
    return ImmutableTask.builder() //
        .text(lineTokens[0]) //
        .type(getDefaultType()) //
        .date(getDefaultDate()) //
        .notes(lineTokens[1]) //
        .priority(parseDifficulty(lineTokens[2])) //
        .build();
  }

  private String getDefaultType() {
    return Task.TYPE_TODO;
  }

  private String getDefaultDate() {
    return DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.now());
  }

  private Double parseDifficulty(String difficulty) {
    switch (difficulty) {
      case "t":
        return Task.DIFFICULTY_TRIVIAL;
      case "e":
        return Task.DIFFICULTY_EASY;
      case "m":
        return Task.DIFFICULTY_MEDIUM;
      case "h":
        return Task.DIFFICULTY_HARD;
      default:
        throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
    }
  }

}
