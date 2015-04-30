package net.algem.scripthelper;

public class ScriptHelperFactory {
    public static ScriptHelper getScriptHelper() {
        try {
            return new ScriptHelperJ7();
        } catch (NoClassDefFoundError e) {
            return new ScriptHelperJ8();
        }
    }
}
