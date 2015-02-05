package net.algem.script.common;

import java.util.List;

public class Script {
    private final String name;
    private final List<ScriptArgument> argument;
    private final String description;
    private final String code;

    public Script(String name, List<ScriptArgument> argument, String description, String code) {
        this.name = name;
        this.argument = argument;
        this.description = description;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public List<ScriptArgument> getArgument() {
        return argument;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Script script = (Script) o;

        if (!argument.equals(script.argument)) return false;
        if (!code.equals(script.code)) return false;
        if (!description.equals(script.description)) return false;
        if (!name.equals(script.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + argument.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Script{" +
                "name='" + name + '\'' +
                ", argument=" + argument +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
