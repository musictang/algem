package net.algem.util;

import java.util.List;

public class StringUtils {
    public static <T> String join(List<T> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : list) {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item);
        }
        return sb.toString();
    }
}
