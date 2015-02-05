package net.algem.script.directory;

import junit.framework.TestCase;
import net.algem.script.directory.models.ScriptDirectory;

import java.io.File;

public class ScriptDirectoryServiceImplTest extends TestCase {

    private ScriptDirectoryServiceImpl service;

    public void setUp() throws Exception {
        super.setUp();
        service = new ScriptDirectoryServiceImpl(new File("testData/scripts"));
    }

    public void testGetAvailableScripts() throws Exception {
        ScriptDirectory availableScripts = service.getAvailableScripts();
        System.out.println(availableScripts);
    }

    public void testLoadScript() throws Exception {

    }
}