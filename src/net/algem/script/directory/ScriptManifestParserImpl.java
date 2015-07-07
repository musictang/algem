package net.algem.script.directory;

import net.algem.script.common.ArgType;
import net.algem.script.common.ScriptArgument;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptManifestParserImpl implements ScriptManifestParser {
    @SuppressWarnings("unchecked")
    @Override
    public ScriptManifest parseManifest(String name, String manifestData) throws Exception {
        Map<String, Object> root = (Map<String, Object>) JSONValue.parse(manifestData);
        String description = (String) root.get("description");
        if (description == null) {
            description = "";
        }
        List<Object> argsList = (List<Object>) root.get("args");
        if (argsList == null) {
            argsList = new ArrayList<>();
        }
        List<ScriptArgument> args = new ArrayList<>();
        for (Object argJson : argsList) {
            args.add(parseArgument((Map<String, Object>) argJson));
        }
        return new ScriptManifest(name, args, description);
    }

    private String getOrDefault(Map<String, Object> map, String key, String defaultValue) {
        String val = (String) map.get(key);
        return val != null ? val : defaultValue;
    }

    private ScriptArgument parseArgument(Map<String, Object> argJson) throws Exception {
        String name = (String) argJson.get("name");
        if (name == null) {
            throw new Exception("Missing required name for argument");
        }
        String label = getOrDefault(argJson, "label", "");
        String typeString = getOrDefault(argJson, "type", "text");
        return new ScriptArgument(name, label, parseArgType(typeString));
    }

    private ArgType parseArgType(String typeString) throws Exception {
        switch (typeString) {
            case "text":
                return ArgType.TEXT;
            case "int":
                return ArgType.INT;
            case "float":
                return ArgType.FLOAT;
            case "date":
                return ArgType.DATE;
            case "bool":
                return ArgType.BOOL;
            default:
                throw new Exception("Invalid argument type " + typeString);
        }
    }

}
