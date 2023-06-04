package pl.mnekos.safemessage;

public class Validate {

    public static void notNull(Object o, String name) {
        if(o == null) throw new IllegalArgumentException(name + " cannot be null.");
    }

    public static void notEmpty(CharSequence c, String name) {
        if(c == null || c.length() == 0) throw new IllegalArgumentException(name + " cannot be empty or null.");
    }

    public static void hasSize(CharSequence c, String name, int from, int to) {
        if(c == null || c.length() < from || c.length() > to) throw new IllegalArgumentException(name + " must have specific length: <" + from + ";" + to + ">");
    }
}
