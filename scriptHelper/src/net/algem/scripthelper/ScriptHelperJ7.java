package net.algem.scripthelper;

import sun.org.mozilla.javascript.internal.NativeObject;

import java.util.Map;

class ScriptHelperJ7 implements ScriptHelper {
    @Override
    public Object convertArgumentsToJs(Map<String, Object> args) {
        NativeObject obj = new NativeObject();
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            obj.put(entry.getKey(), obj, entry.getValue());
        }
        return obj;
    }
}
