package com.nictas.habitica.client.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Enclosing
@Value.Immutable
@JsonSerialize(as = ImmutableCreateUserTaskResponse.class)
@JsonDeserialize(as = ImmutableCreateUserTaskResponse.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface CreateUserTaskResponse {

    boolean getSuccess();

    Data getData();

    @Value.Immutable
    @JsonSerialize(as = ImmutableCreateUserTaskResponse.Data.class)
    @JsonDeserialize(as = ImmutableCreateUserTaskResponse.Data.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public interface Data {

        String getId();

        String getText();

    }

}
