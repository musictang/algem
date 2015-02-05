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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ScriptManifest that = (ScriptManifest) o;

            if (!arguments.equals(that.arguments)) return false;
            if (!description.equals(that.description)) return false;
            if (!name.equals(that.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + arguments.hashCode();
            result = 31 * result + description.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "ScriptManifest{" +
                    "name='" + name + '\'' +
                    ", arguments=" + arguments +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    /**
     * Parse manifest data into a ScriptManifest object
     *
     * @param manifestData
     * @return
     * @throws Exception
     */
    public ScriptManifest parseManifest(String name, String manifestData) throws Exception;
}
