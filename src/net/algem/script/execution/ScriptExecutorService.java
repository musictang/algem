package net.algem.script.execution;

import net.algem.script.Script;
import net.algem.script.execution.models.ScriptArguments;
import net.algem.script.execution.models.ScriptResult;

public interface ScriptExecutorService {
    /**
     * Execute the given script by the scripting engine
     * @param script the script to execute
     * @param arguments
     * @return script execution result as a ScriptResult object
     * @throws Exception
     */
    public ScriptResult executeScript(Script script, ScriptArguments arguments) throws Exception;
}
