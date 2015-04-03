package net.algem.script.execution;

import net.algem.script.execution.models.ScriptResult;

import java.io.File;

public interface ScriptExportService {
    void exportScriptResult(ScriptResult scriptResult, File outFile) throws Exception;
}
