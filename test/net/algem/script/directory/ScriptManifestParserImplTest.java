package net.algem.script.directory;

import junit.framework.TestCase;
import net.algem.script.common.ArgType;
import net.algem.script.common.ScriptArgument;
import net.algem.script.directory.ScriptManifestParser.ScriptManifest;
import net.algem.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class ScriptManifestParserImplTest extends TestCase {

    private ScriptManifestParserImpl scriptManifestParser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scriptManifestParser = new ScriptManifestParserImpl();
    }

    public void testParseManifest() throws Exception {
        String data = IOUtil.readFile(new File("testData/scriptsParser/script1.json"));
        ScriptManifest scriptManifest = scriptManifestParser.parseManifest("script1", data);
        ScriptManifest expected = new ScriptManifest(
            "script1",
            asList(
                new ScriptArgument("text1", "A text input", ArgType.TEXT),
                new ScriptArgument("int1", "A int", ArgType.INT),
                new ScriptArgument("bool1", "A bool", ArgType.BOOL),
                new ScriptArgument("date1", "A date", ArgType.DATE),
                new ScriptArgument("float1", "A float", ArgType.FLOAT)
            ),
            "Simple test script"
        );
        assertEquals(expected, scriptManifest);
    }

    public void testDefault() throws Exception {
        String data = IOUtil.readFile(new File("testData/scriptsParser/empty.json"));
        ScriptManifest scriptManifest = scriptManifestParser.parseManifest("empty", data);
        ScriptManifest expected = new ScriptManifest(
                "empty",
                new ArrayList<ScriptArgument>(),
                ""
        );
        assertEquals(expected, scriptManifest);
    }

    public void testDefaultArg() throws Exception {
        String data = IOUtil.readFile(new File("testData/scriptsParser/defaultarg.json"));
        ScriptManifest scriptManifest = scriptManifestParser.parseManifest("defaultarg", data);
        ScriptManifest expected = new ScriptManifest(
                "defaultarg",
                asList(
                        new ScriptArgument("text1", "", ArgType.TEXT)
                ),
                ""
        );
        assertEquals(expected, scriptManifest);
    }

    public void testInvalid() throws IOException {
        File[] files = new File("testData/scriptsParser/invalid").listFiles();
        for (File file : files) {
            String data = IOUtil.readFile(file);

            try {
                scriptManifestParser.parseManifest("test", data);
                fail("File " + file.getName() +" should be invalid");
            } catch (Exception ignored) {
            }
        }

    }
}