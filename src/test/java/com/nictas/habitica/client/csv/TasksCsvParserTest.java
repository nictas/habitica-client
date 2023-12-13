package com.nictas.habitica.client.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nictas.habitica.client.domain.Task;

class TasksCsvParserTest {

    private TasksCsvParser parser = new TasksCsvParser();

    @Test
    @DisplayName("With a valid CSV")
    void testParseCsvWithValidData1() {
        List<String> csv = new ArrayList<>();
        csv.add("`[Warcraft]` Achievements (1/4),https://www.wowhead.com/achievement=19157,t");
        csv.add("`[Warcraft]` Achievements (2/4),https://www.wowhead.com/achievement=19158,e");
        csv.add("`[Warcraft]` Achievements (3/4),https://www.wowhead.com/achievement=19159,m");
        csv.add("`[Warcraft]` Achievements (4/4),https://www.wowhead.com/achievement=19160,h");

        List<Task> tasks = parser.parseCsv(csv);

        assertTrue(tasks.size() == 4, () -> "Result should contain 4 tasks");

        Task combatant1Achievement = tasks.get(0);
        assertEquals("`[Warcraft]` Achievements (1/4)", combatant1Achievement.getText());
        assertEquals("https://www.wowhead.com/achievement=19157", combatant1Achievement.getNotes());
        assertEquals(Task.DIFFICULTY_TRIVIAL, combatant1Achievement.getPriority());

        Task combatant2Achievement = tasks.get(1);
        assertEquals("`[Warcraft]` Achievements (2/4)", combatant2Achievement.getText());
        assertEquals("https://www.wowhead.com/achievement=19158", combatant2Achievement.getNotes());
        assertEquals(Task.DIFFICULTY_EASY, combatant2Achievement.getPriority());

        Task challenger1Achievement = tasks.get(2);
        assertEquals("`[Warcraft]` Achievements (3/4)", challenger1Achievement.getText());
        assertEquals("https://www.wowhead.com/achievement=19159", challenger1Achievement.getNotes());
        assertEquals(Task.DIFFICULTY_MEDIUM, challenger1Achievement.getPriority());

        Task challenger2Achievement = tasks.get(3);
        assertEquals("`[Warcraft]` Achievements (4/4)", challenger2Achievement.getText());
        assertEquals("https://www.wowhead.com/achievement=19160", challenger2Achievement.getNotes());
        assertEquals(Task.DIFFICULTY_HARD, challenger2Achievement.getPriority());
    }

    @Test
    @DisplayName("With a valid CSV and blank lines")
    void testParseCsvWithValidData2() {
        List<String> csv = new ArrayList<>();
        csv.add("");
        csv.add("Test 1,foo,t");
        csv.add("  ");
        csv.add("Test 2,bar,e");
        csv.add("\t");
        csv.add("\n");
        csv.add("");
        csv.add("Test 3,baz,e");
        csv.add("\t\t");
        csv.add("\t  ");

        List<Task> tasks = parser.parseCsv(csv);

        assertTrue(tasks.size() == 3, () -> "Result should contain 3 tasks");
    }

    @Test
    @DisplayName("With a valid CSV and extra whitespace")
    void testParseCsvWithValidData3() {
        List<String> csv = new ArrayList<>();
        csv.add("\tTest 1,foo,t    ");
        csv.add("  Test 2,bar,e\t\t");
        csv.add("Test 3,baz,e");

        List<Task> tasks = parser.parseCsv(csv);

        assertTrue(tasks.size() == 3, () -> "Result should contain 3 tasks");

        Task test1 = tasks.get(0);
        assertEquals("Test 1", test1.getText());
        assertEquals("foo", test1.getNotes());
        assertEquals(Task.DIFFICULTY_TRIVIAL, test1.getPriority());

        Task test2 = tasks.get(1);
        assertEquals("Test 2", test2.getText());
        assertEquals("bar", test2.getNotes());
        assertEquals(Task.DIFFICULTY_EASY, test2.getPriority());

        Task test3 = tasks.get(2);
        assertEquals("Test 3", test3.getText());
        assertEquals("baz", test3.getNotes());
        assertEquals(Task.DIFFICULTY_EASY, test3.getPriority());
    }

    @Test
    @DisplayName("With CSV containing unknown difficulties")
    void testParseCsvWithInvalidData1() {
        List<String> csv = new ArrayList<>();
        csv.add("Test 1,foo,t");
        csv.add("Test 2,bar,vt");
        csv.add("Test 3,baz,vh");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> parser.parseCsv(csv));
        assertEquals("Unable to parse line 2: Unknown difficulty: vt", e.getMessage());
    }

    @Test
    @DisplayName("With CSV containing extra tokens")
    void testParseCsvWithInvalidData2() {
        List<String> csv = new ArrayList<>();
        csv.add("\"Whelp, I'm Lost\" achievement,Amirdrassil, the Dream's Hope,e");
        csv.add("\"Memories of Teldrassil\" achievement,Amirdrassil, the Dream's Hope,e");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> parser.parseCsv(csv));
        assertEquals("Unable to parse line 1: Expected 3 tokens but got 5. Expected format: " + Arrays.toString(TasksCsvParser.CSV_FORMAT),
                     e.getMessage());
    }

    @Test
    @DisplayName("With an empty CSV")
    void testParseCsvWithEmptyCsv1() {
        List<String> csv = Collections.emptyList();

        List<Task> tasks = parser.parseCsv(csv);

        assertTrue(tasks.isEmpty(), () -> "Result should be empty");
    }

    @Test
    @DisplayName("With an empty CSV with whitespace")
    void testParseCsvWithEmptyCsv2() {
        List<String> csv = new ArrayList<>();
        csv.add(null);
        csv.add("  ");
        csv.add("\t");
        csv.add("\n");

        List<Task> tasks = parser.parseCsv(csv);

        assertTrue(tasks.isEmpty(), () -> "Result should be empty");
    }

}
