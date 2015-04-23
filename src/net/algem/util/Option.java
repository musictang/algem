package net.algem.util;

import net.algem.planning.fact.services.ReplanifyCommand;

import java.util.Iterator;

/**
 * Lightweight Option type, that behaves like a zero or one element list
 */
public class Option<T> implements Iterable<T> {
    @Override
    public Iterator<T> iterator() {
        if (data != null) {
            return new Iterator<T>() {
                boolean hasNext = true;
                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public T next() {
                    if (hasNext) {
                        hasNext = false;
                        return data;
                    }
                    return null;
                }

                @Override
                public void remove() {

                }
            };
        } else {
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public T next() {
                    return null;
                }

                @Override
                public void remove() {

                }
            };
        }
    }

    public boolean isPresent() {
        return data != null;
    }

    public T get() {
        return data;
    }

    private T data;

    private Option(T data) {
        this.data = data;
    }

    public static <T> Option<T> of(T data) {
        return new Option<>(data);
    }
    public static <T> Option<T> none() {
        return new Option<>(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option<?> option = (Option<?>) o;

        return !(data != null ? !data.equals(option.data) : option.data != null);

    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @Override
    public String toString() {
        if (data != null) {
            return "Some(" + data + ")";
        } else {
            return "None";
        }

    }
}
