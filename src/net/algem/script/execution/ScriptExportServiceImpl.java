package net.algem.script.execution;

import net.algem.script.execution.models.ScriptResult;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

public class ScriptExportServiceImpl implements ScriptExportService {
    @Override
    public void exportScriptResult(ScriptResult scriptResult, File outFile) throws Exception {
        try (CsvListWriter csvListWriter = new CsvListWriter(new OutputStreamWriter(new FileOutputStream(outFile), "iso-8859-1"), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {
            String[] header = new String[scriptResult.getHeader().size()];
            header = scriptResult.getHeader().toArray(header);
            csvListWriter.writeHeader(header);
            for (List<Object> row : scriptResult.getRows()) {
                csvListWriter.write(row);
            }
        }
    }
}
