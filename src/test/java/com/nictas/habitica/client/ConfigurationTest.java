package com.nictas.habitica.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigurationTest {

    @Mock
    private Function<String, String> getEnvFunction;
    @InjectMocks
    private Configuration configuration;

    @Test
    void testGetUsername() {
        String username = UUID.randomUUID()
                              .toString();

        when(getEnvFunction.apply(Configuration.ENV_USERNAME)).thenReturn(username);

        assertEquals(username, configuration.getUsername());
    }

    @Test
    void testGetUsernameWithNull() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> configuration.getUsername());
        assertEquals(String.format("Environment variable %s is not set", Configuration.ENV_USERNAME), e.getMessage());
    }

    @Test
    void testGetUsernameWithEmptyString() {
        when(getEnvFunction.apply(Configuration.ENV_USERNAME)).thenReturn("");

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> configuration.getUsername());
        assertEquals(String.format("Environment variable %s is not set", Configuration.ENV_USERNAME), e.getMessage());
    }

    @Test
    void testGetKey() {
        String username = UUID.randomUUID()
                              .toString();

        when(getEnvFunction.apply(Configuration.ENV_KEY)).thenReturn(username);

        assertEquals(username, configuration.getKey());
    }

    @Test
    void testGetKeyWithNull() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> configuration.getKey());
        assertEquals(String.format("Environment variable %s is not set", Configuration.ENV_KEY), e.getMessage());
    }

    @Test
    void testGetKeyWithEmptyString() {
        when(getEnvFunction.apply(Configuration.ENV_KEY)).thenReturn("");

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> configuration.getKey());
        assertEquals(String.format("Environment variable %s is not set", Configuration.ENV_KEY), e.getMessage());
    }

}
