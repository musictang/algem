package net.algem.script.execution;

import net.algem.script.common.Script;
import net.algem.script.execution.models.ScriptUserArguments;
import net.algem.script.execution.models.ScriptResult;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.List;

public class ScriptExecutorServiceImpl implements ScriptExecutorService {
    @Override
    public ScriptResult executeScript(Script script, ScriptUserArguments arguments) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        Bindings bindings = engine.createBindings();
        OutInterface out = new OutInterface();
        bindings.put("out", out);
        bindings.put("args", arguments.getArguments());

        engine.eval(script.getCode(), bindings);
        return out.getResult();
    }


    public static class OutInterface {
        private List<String> _header;
        private List<List<Object>> rows;

        public OutInterface() {
            rows = new ArrayList<>();
        }

        public void header(List<String> header) {
            _header = header;
        }

        public void line(List<Object> line) {
            rows.add(line);
        }

        ScriptResult getResult() {
            return new ScriptResult(_header, rows);
        }
    }
}
