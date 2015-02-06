package net.algem.script.execution;

import net.algem.script.common.Script;
import net.algem.script.execution.models.ScriptUserArguments;
import net.algem.script.execution.models.ScriptResult;
import net.algem.util.DataConnection;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScriptExecutorServiceImpl implements ScriptExecutorService {
    private final DataConnection dataConnection;

    public ScriptExecutorServiceImpl(DataConnection dataConnection) {
        this.dataConnection = dataConnection;
    }

    @Override
    public ScriptResult executeScript(final Script script, final ScriptUserArguments arguments) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine engine = manager.getEngineByName("js");

        return dataConnection.withTransaction(new DataConnection.SQLRunnable<ScriptResult>() {
            @Override
            public ScriptResult run(DataConnection conn) throws Exception {
                Bindings bindings = engine.createBindings();
                OutInterface out = new OutInterface();
                bindings.put("out", out);
                bindings.put("args", arguments.getArguments());
                bindings.put("dc", conn);

                engine.eval(script.getCode(), bindings);
                return out.getResult();
            }
        });
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

        public void resultSet(ResultSet rs) throws Exception {
            int n = rs.getMetaData().getColumnCount();
            _header = new ArrayList<>();
            for (int i=0; i<n; i++) {
                _header.add(rs.getMetaData().getColumnName(i + 1));
            }
            rows = new ArrayList<>();
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    row.add(rs.getObject(i + 1));
                }
                rows.add(row);
            }
        }

        @Override
        public String toString() {
            return "OutInterface{" +
                    "_header=" + _header +
                    ", rows=" + rows +
                    '}';
        }
    }
}
