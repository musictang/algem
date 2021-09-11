/*
 * @(#)ScriptExecutorServiceImpl.java 2.11.1 07/10/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.script.execution;

import net.algem.scripthelper.ScriptHelperFactory;
import net.algem.script.common.Script;
import net.algem.script.execution.models.ScriptResult;
import net.algem.script.execution.models.ScriptUserArguments;
import net.algem.util.DataConnection;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;

/**
 * 
 * @author alexd6631
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * 
 * @version 2.11.1 07/10/16
 */
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
                bindings.put("args", ScriptHelperFactory.getScriptHelper().convertArgumentsToJs(arguments.getArguments()));
                //bindings.put("dc", conn);
                bindings.put("utils", new Utils());
                engine.eval(script.getCode(), bindings);

                //2.17.3g eric le 17/05/2020
                if (arguments.getArguments().containsKey("update")) {
                    out.updateSet(dataConnection.executeUpdate(out.getQuery()));
                    return out.getResult();
                }
                out.resultSet(dataConnection.executeQuery(out.getQuery()));
                return out.getResult();
            }
        });
    }


    public static class OutInterface
  {

    private List<String> _header;
    private List<List<Object>> rows;
    /** Default query string. */
    private String query = "SELECT '" + MessageUtil.getMessage("scripts.error.execution.warning") + "' AS \"" + BundleUtil.getLabel("Warning.label") + "\"";

    public OutInterface() {
      rows = new ArrayList<>();
    }

    public void header(List<String> header) {
      _header = header;
    }

    public void line(List<Object> line) {
      rows.add(line);
    }

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }

    ScriptResult getResult() {
      return new ScriptResult(_header, rows);
    }

    public void resultSet(ResultSet rs) throws Exception {
      int n = rs.getMetaData().getColumnCount();
      _header = new ArrayList<>();
      for (int i = 0; i < n; i++) {
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

      //2.17.3g eric le 17/05/2020
      public void updateSet(int nb) throws Exception {
      _header = new ArrayList<>();
      _header.add("Nombre de mise à jour");
      rows = new ArrayList<>();
      List<Object> row = new ArrayList<>();
      row.add(Integer.valueOf(nb));
      rows.add(row);
    }

    @Override
    public String toString() {
      return "OutInterface{"
              + "_header=" + _header
              + ", rows=" + rows
              + '}';
    }
  }

  public static class Utils
  {

    public String sqlDate(Date date) {
      return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public Date getStartOfYear() {
      return new DateFr(ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey())).getDate();
    }

    public Date getEndOfYear() {
      return new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey())).getDate();
    }

    public void print(Object o) {
      System.out.println(o);
    }

  }
}
