package net.algem.script.execution.models;

import java.util.Map;

public class ScriptUserArguments {
    private final Map<String, Object> arguments;

    public ScriptUserArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}
