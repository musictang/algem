package net.algem.script.execution.models;

import java.util.List;

public class ScriptResult {

    private final List<String> header;
    private final List<List<Object>> rows;

    public ScriptResult(List<String> header, List<List<Object>> rows) {
        this.header = header;
        this.rows = rows;
    }
}
