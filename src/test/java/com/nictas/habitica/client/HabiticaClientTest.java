package com.nictas.habitica.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpHeaders;

import com.nictas.habitica.client.domain.CreateUserTaskResponse;
import com.nictas.habitica.client.domain.ImmutableTask;
import com.nictas.habitica.client.domain.Task;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class HabiticaClientTest {

    private static final String USERNAME = "79f8b89d-824f-4ab1-9944-3db2e7e17d36";
    private static final String KEY = "96cc2dc4-979a-4f37-a7b8-87ac45bed034";
    private static final String CREATE_USER_TASK_RESPONSE = "{\"success\":true,\"data\":{\"userId\":\"79f8b89d-824f-4ab1-9944-3db2e7e17d36\",\"alias\":\"hab-api-tasks\",\"text\":\"Test the Habitica client\",\"type\":\"todo\",\"notes\":\"Use the OkHttp MockWebServer instead of mocking the Spring WebClient\",\"tags\":[\"7a93d6ab-181d-4c27-92e6-fe5545d1d53c\", \"c77421a9-6bae-4ffe-80ea-fc89ba5db834\"],\"value\":0,\"priority\":1.5,\"attribute\":\"str\",\"challenge\":{},\"group\":{\"assignedUsers\":[],\"approval\":{\"required\":false,\"approved\":false,\"requested\":false}},\"reminders\":[],\"_id\":\"829d435b-edc4-498c-a30e-e52361a0f35a\",\"createdAt\":\"2017-01-12T02:11:02.876Z\",\"updatedAt\":\"2017-01-12T02:11:02.876Z\",\"checklist\":[{\"completed\":true,\"text\":\"read wiki\",\"id\":\"91edadda-fb62-4e6e-b110-aff26f936678\"},{\"completed\":false,\"text\":\"write code\",\"id\":\"d1ddad50-ab22-49c4-8261-9996ae337b6a\"}],\"collapseChecklist\":false,\"completed\":false,\"id\":\"829d435b-edc4-498c-a30e-e52361a0f35a\"},\"notifications\":[]}";
    private static final String COMPLETE_TASK_RESPONSE = "{\"success\":true,\"data\":{\"delta\":0.9746999906450404,\"_tmp\":{},\"hp\":49.06645205596985,\"mp\":37.2008917491047,\"exp\":101.93810026267543,\"gp\":77.09694176716997,\"lvl\":19,\"class\":\"rogue\",\"points\":0,\"str\":5,\"con\":3,\"int\":3,\"per\":8,\"buffs\":{\"str\":9,\"int\":9,\"per\":9,\"con\":9,\"stealth\":0,\"streaks\":false,\"snowball\":false,\"spookySparkles\":false,\"shinySeed\":false,\"seafoam\":false},\"training\":{\"int\":0,\"per\":0,\"str\":0,\"con\":0}},\"notifications\":[]}";

    private final BasicJsonTester jsonTester = new BasicJsonTester(getClass());
    private MockWebServer mockWebServer;
    private HabiticaClientFactory habiticaClientFactory;
    private HabiticaClient habiticaClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String mockWebServerUrl = mockWebServer.url("/")
                                               .url()
                                               .toString();

        habiticaClientFactory = new HabiticaClientFactory();
        habiticaClient = habiticaClientFactory.createClient(mockWebServerUrl, USERNAME, KEY);
    }

    @Test
    void testCreateUserTask() throws InterruptedException {
        Task task = ImmutableTask.builder()
                                 .text("Test the Habitica client")
                                 .notes("Use the OkHttp MockWebServer instead of mocking the Spring WebClient")
                                 .type(Task.TYPE_TODO)
                                 .priority(Task.DIFFICULTY_MEDIUM)
                                 .date(DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.now()))
                                 .tags("7a93d6ab-181d-4c27-92e6-fe5545d1d53c", "c77421a9-6bae-4ffe-80ea-fc89ba5db834")
                                 .build();

        MockResponse mockResponse = new MockResponse().setResponseCode(201)
                                                      .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                                      .setBody(CREATE_USER_TASK_RESPONSE);
        mockWebServer.enqueue(mockResponse);

        CreateUserTaskResponse response = habiticaClient.createUserTask(task);
        verifyCreateUserTaskResponse(task, response);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        verifyCreateUserTaskRequest(recordedRequest);
        verifyCreateUserTaskRequestBody(task, recordedRequest);
    }

    private void verifyCreateUserTaskResponse(Task task, CreateUserTaskResponse response) {
        assertTrue(response.getSuccess());
        assertEquals("829d435b-edc4-498c-a30e-e52361a0f35a", response.getData()
                                                                     .getId());
        assertEquals(task.getText(), response.getData()
                                             .getText());
    }

    private void verifyCreateUserTaskRequest(RecordedRequest recordedRequest) {
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/tasks/user", recordedRequest.getPath());
        verifyClientHeaders(recordedRequest);
    }

    private void verifyClientHeaders(RecordedRequest recordedRequest) {
        assertEquals(USERNAME, recordedRequest.getHeader(HabiticaClientFactory.HEADER_X_API_USER));
        assertEquals(KEY, recordedRequest.getHeader(HabiticaClientFactory.HEADER_X_API_KEY));
        assertThat(recordedRequest.getHeader(HabiticaClientFactory.HEADER_X_API_CLIENT)).contains(USERNAME);
    }

    private void verifyCreateUserTaskRequestBody(Task task, RecordedRequest recordedRequest) {
        JsonContent<Object> requestBodyTester = jsonTester.from(recordedRequest.getBody()
                                                                               .readUtf8());

        assertThat(requestBodyTester).extractingJsonPathStringValue("$.text")
                                     .isEqualTo(task.getText());
        assertThat(requestBodyTester).extractingJsonPathStringValue("$.notes")
                                     .isEqualTo(task.getNotes());
        assertThat(requestBodyTester).extractingJsonPathStringValue("$.type")
                                     .isEqualTo(task.getType());
        assertThat(requestBodyTester).extractingJsonPathNumberValue("$.priority")
                                     .isEqualTo(task.getPriority());
        assertThat(requestBodyTester).extractingJsonPathStringValue("$.date")
                                     .isEqualTo(task.getDate());
        assertThat(requestBodyTester).extractingJsonPathArrayValue("$.tags")
                                     .containsAll(List.of(task.getTags()));
    }

    @Test
    void testCompleteTask() throws InterruptedException {
        MockResponse mockResponse = new MockResponse().setResponseCode(200)
                                                      .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                                      .setBody(COMPLETE_TASK_RESPONSE);
        mockWebServer.enqueue(mockResponse);

        String taskId = UUID.randomUUID()
                            .toString();
        habiticaClient.completeTask(taskId);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        verifyCompleteTaskRequest(taskId, recordedRequest);
    }

    private void verifyCompleteTaskRequest(String taskId, RecordedRequest recordedRequest) {
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(String.format("/tasks/%s/score/up", taskId), recordedRequest.getPath());
        verifyClientHeaders(recordedRequest);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
