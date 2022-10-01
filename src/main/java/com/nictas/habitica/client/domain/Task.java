package com.nictas.habitica.client.domain;

import org.immutables.value.Value;

@Value.Immutable
public interface Task {

  String TYPE_HABIT = "habit";
  String TYPE_DAILY = "daily";
  String TYPE_TODO = "todo";
  String TYPE_REWARD = "reward";

  double DIFFICULTY_TRIVIAL = 0.1;
  double DIFFICULTY_EASY = 1;
  double DIFFICULTY_MEDIUM = 1.5;
  double DIFFICULTY_HARD = 2;

  /**
   * The text to be displayed for the task.
   */
  String getText();

  /**
   * Task type, options are: "habit", "daily", "todo", "reward".
   */
  String getType();

  /**
   * Optional: Due date to be shown in task list. Only valid for type "todo".
   */
  String getDate();

  /**
   * Optional: Array of UUIDs of tags.
   */
  String[] getTags();

  /**
   * Optional: Extra notes.
   */
  String getNotes();

  /**
   * Optional: Difficulty, options are 0.1, 1, 1.5, 2; equivalent of Trivial, Easy, Medium, Hard.
   */
  Double getPriority();

}
