package net.algem.script.execution.models;

import java.util.List;

public class ScriptResult {

    private final List<String> header;
    private final List<List<Object>> rows;

    public ScriptResult(List<String> header, List<List<Object>> rows) {
        this.header = header;
        this.rows = rows;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "ScriptResult{" +
                "header=" + header +
                ", rows=" + rows +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptResult that = (ScriptResult) o;

        if (!header.equals(that.header)) return false;
        if (!rows.equals(that.rows)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = header.hashCode();
        result = 31 * result + rows.hashCode();
        return result;
    }
}
