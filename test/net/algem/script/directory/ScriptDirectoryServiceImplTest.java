package net.algem.script.directory;

import junit.framework.TestCase;
import net.algem.script.common.ArgType;
import net.algem.script.common.Script;
import net.algem.script.common.ScriptArgument;
import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptFile;
import net.algem.script.directory.models.ScriptImplFile;
import net.algem.util.IOUtil;
import org.mockito.Mockito;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ScriptDirectoryServiceImplTest extends TestCase {

    private ScriptDirectoryServiceImpl service;
    private IOUtil.FileReaderHelper fileReaderHelper;
    private ScriptManifestParser scriptManifestParser;

    public void setUp() throws Exception {
        super.setUp();
        fileReaderHelper = mock(IOUtil.FileReaderHelper.class);
        scriptManifestParser = mock(ScriptManifestParser.class);
        service = new ScriptDirectoryServiceImpl(new File("testData/scripts"), fileReaderHelper, scriptManifestParser);
    }

    private List<ScriptFile> list(ScriptFile ...items) {
        return Arrays.asList(items);
    }

    public void testGetAvailableScripts() throws Exception {
        ScriptDirectory availableScripts = service.getAvailableScripts();

        ScriptDirectory expected = new ScriptDirectory(new File("testData/scripts"), list(
                new ScriptDirectory(new File("testData/scripts/folder1"), list(
                        new ScriptImplFile(new File("testData/scripts/folder1/script1.json"), new File("testData/scripts/folder1/script1.js"))
                )),
                new ScriptDirectory(new File("testData/scripts/folder2"), list())
        ));
        assertEquals(expected, availableScripts);
    }

    public void testLoadScript() throws Exception {
        File manifestFile = new File("script.json");
        File codeFile = new File("script.js");

        String codeData = "codeData";
        String manifestData = "{...}";

        when(fileReaderHelper.readFile(codeFile)).thenReturn(codeData);
        when(fileReaderHelper.readFile(manifestFile)).thenReturn(manifestData);

        String scriptName = "script";
        List<ScriptArgument> arguments = Arrays.asList(new ScriptArgument("test", "Test", ArgType.TEXT));
        String scriptDescription = "description";

        when(scriptManifestParser.parseManifest(manifestData)).thenReturn(new ScriptManifestParser.ScriptManifest(
                scriptName, arguments, scriptDescription
        ));

        Script expected = new Script(scriptName, arguments, scriptDescription, codeData);
        Script script = service.loadScript(new ScriptImplFile(manifestFile, codeFile));

        assertEquals(expected, script);
    }
}