package net.algem.script.directory;

import net.algem.script.common.ScriptArgument;

import java.util.List;

public interface ScriptManifestParser {
    public static class ScriptManifest {
        private final String name;
        private final List<ScriptArgument> arguments;
        private final String description;

        public ScriptManifest(String name, List<ScriptArgument> arguments, String description) {
            this.name = name;
            this.arguments = arguments;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public List<ScriptArgument> getArguments() {
            return arguments;
        }

        public String getDescription() {
            return description;
        }
    }

    public ScriptManifest parseManifest(String manifestData);
}
