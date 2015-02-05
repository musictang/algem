package net.algem.script.execution.models;

import java.util.Map;

public class ScriptArguments {
    private final Map<String, Object> arguments;

    public ScriptArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}
