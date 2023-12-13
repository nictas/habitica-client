package com.nictas.habitica.client.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class CreateUserTaskResponseTest {

    @Test
    void testJsonDeserialization() throws JsonMappingException, JsonProcessingException {
        String json = "{\"success\":true,\"data\":{\"userId\":\"b0413351-405f-416f-8787-947ec1c85199\",\"alias\":\"hab-api-tasks\",\"text\":\"Update Habitica API Documentation - Tasks\",\"type\":\"todo\",\"notes\":\"Update the tasks api on GitHub\",\"tags\":[\"ed427623-9a69-4aac-9852-13deb9c190c3\"],\"value\":0,\"priority\":2,\"attribute\":\"str\",\"challenge\":{},\"group\":{\"assignedUsers\":[],\"approval\":{\"required\":false,\"approved\":false,\"requested\":false}},\"reminders\":[],\"_id\":\"829d435b-edc4-498c-a30e-e52361a0f35a\",\"createdAt\":\"2017-01-12T02:11:02.876Z\",\"updatedAt\":\"2017-01-12T02:11:02.876Z\",\"checklist\":[{\"completed\":true,\"text\":\"read wiki\",\"id\":\"91edadda-fb62-4e6e-b110-aff26f936678\"},{\"completed\":false,\"text\":\"write code\",\"id\":\"d1ddad50-ab22-49c4-8261-9996ae337b6a\"}],\"collapseChecklist\":false,\"completed\":false,\"id\":\"829d435b-edc4-498c-a30e-e52361a0f35a\"},\"notifications\":[]}";
        ObjectMapper objectMapper = new ObjectMapper();

        CreateUserTaskResponse response = objectMapper.readValue(json, CreateUserTaskResponse.class);

        assertTrue(response.getSuccess());
        assertEquals("829d435b-edc4-498c-a30e-e52361a0f35a", response.getData()
                                                                     .getId());
        assertEquals("Update Habitica API Documentation - Tasks", response.getData()
                                                                          .getText());
    }

}
