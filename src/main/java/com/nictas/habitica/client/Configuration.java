package com.nictas.habitica.client;

import java.util.function.Function;

public class Configuration {

    static final String ENV_USERNAME = "HABITICA_USERNAME";
    static final String ENV_KEY = "HABITICA_KEY";

    private Function<String, String> getEnvFunction;

    public Configuration() {
        this(System::getenv);
    }

    Configuration(Function<String, String> getEnvFunction) {
        this.getEnvFunction = getEnvFunction;
    }

    public String getUsername() {
        return getEnv(ENV_USERNAME);
    }

    public String getKey() {
        return getEnv(ENV_KEY);
    }

    private String getEnv(String name) {
        String value = getEnvFunction.apply(name);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(String.format("Environment variable %s is not set", name));
        }
        return value;
    }

}
