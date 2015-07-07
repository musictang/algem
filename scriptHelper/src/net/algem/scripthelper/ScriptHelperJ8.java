package net.algem.scripthelper;

import java.util.Map;

class ScriptHelperJ8 implements ScriptHelper {
    @Override
    public Object convertArgumentsToJs(Map<String, Object> args) {
        return args;
    }
}
