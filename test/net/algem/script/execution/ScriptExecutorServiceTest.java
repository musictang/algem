package net.algem.script.execution;

import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.script.common.Script;
import net.algem.script.common.ScriptArgument;
import net.algem.script.execution.models.ScriptResult;
import net.algem.script.execution.models.ScriptUserArguments;
import net.algem.util.DataConnection;
import net.algem.util.DataConnectionTest;
import net.algem.util.IOUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ScriptExecutorServiceTest extends TestCase {
    private ScriptExecutorServiceImpl executorService;
    private DataConnectionTest.SimpleService simpleService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DataConnection dc = TestProperties.getDataConnection();
        executorService = new ScriptExecutorServiceImpl(dc);

        simpleService = new DataConnectionTest.SimpleService(dc);
        simpleService.initTables();
        simpleService.op1();
        simpleService.op2();
    }

    public void tearDown() throws Exception {
        simpleService.dropTables();
    }

    public void testHelloWorld() throws Exception {
        String code = IOUtil.readFile(new File("testData/scriptsSample/hello.js"));
        Script script = new Script("hello world", new ArrayList<ScriptArgument>(), "", code);

        ScriptResult scriptResult = executorService.executeScript(script, new ScriptUserArguments(new HashMap<String, Object>()));
        ScriptResult expected = new ScriptResult(Arrays.asList("test"),
                Arrays.asList(Arrays.<Object>asList("Hello world")));

        assertEquals(expected, scriptResult);
    }


    public void testWithArgs() throws Exception {
        String code = IOUtil.readFile(new File("testData/scriptsSample/withargs.js"));
        Script script = new Script("hello world", new ArrayList<ScriptArgument>(), "", code);

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("test", "hello");
        ScriptResult scriptResult = executorService.executeScript(script, new ScriptUserArguments(arguments));
        ScriptResult expected = new ScriptResult(Arrays.asList("test"),
                Arrays.asList(Arrays.<Object>asList("hello")));

        assertEquals(expected, scriptResult);
    }

    public void testWithDataConnection() throws Exception {
        String code = IOUtil.readFile(new File("testData/scriptsSample/withdc.js"));
        Script script = new Script("hello world", new ArrayList<ScriptArgument>(), "", code);
        ScriptResult scriptResult = executorService.executeScript(script, new ScriptUserArguments(new HashMap<String, Object>()));
        ScriptResult expected = new ScriptResult(Arrays.asList("value"),
                Arrays.asList(Arrays.<Object>asList(1), Arrays.<Object>asList(2)));

        assertEquals(expected, scriptResult);

    }
}