package net.algem.script.execution.models;

import java.util.HashMap;
import java.util.Map;

public class ScriptUserArguments {
    private final Map<String, Object> arguments;

    public ScriptUserArguments(Map<String, Object> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
