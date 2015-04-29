package net.algem.script.common;

public class ScriptArgument {
    private final String name;
    private final String label;
    private final ArgType type;

    public ScriptArgument(String name, String label, ArgType type) {
        this.name = name;
        this.label = label;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public ArgType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptArgument that = (ScriptArgument) o;

        if (!label.equals(that.label)) return false;
        if (!name.equals(that.name)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ScriptArgument{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", type=" + type +
                '}';
    }
}
