package net.algem.script.directory;

import java.io.File;
import net.algem.script.common.Script;
import net.algem.script.directory.models.ScriptDirectory;
import net.algem.script.directory.models.ScriptFile;
import net.algem.script.directory.models.ScriptImplFile;

/**
 * 
 * @author Alexandre Delattre
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0 28/09/16
 * @since 2.9.4.12
 */
public interface ScriptDirectoryService {
    /**
     * Get the available scripts as a directory tree
     * @return the tree of available scripts
     */
    ScriptDirectory getAvailableScripts();

    /**
     * Loads a script from its script file
     * @param scriptFile the script file to load
     * @return a script object
     */
    Script loadScript(ScriptImplFile scriptFile) throws Exception;
    
    ScriptFile getFile(File file);
}
