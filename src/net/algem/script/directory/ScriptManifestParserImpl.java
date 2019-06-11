/*
 * @(#)ScriptManifestParserImpl.java 2.10.0 23/05/16
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.script.directory;

import net.algem.script.common.ArgType;
import net.algem.script.common.ScriptArgument;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Alexandre Delattre
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.9.4.12
 */
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
            case "tlist":
                return ArgType.TEACHER_LIST;
            default:
                throw new Exception("Invalid argument type " + typeString);
        }
    }

}
