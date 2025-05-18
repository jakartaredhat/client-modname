package test.ejb;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ServiceLocator {

    private ServiceLocator() {
    }

    public static Object lookup(String name, Context... context)
            throws NamingException {
        return lookup(name, (Class) null, context);
    }

    public static Object lookupNoTry(String name, Context... context) {
        Object obj = null;
        try {
            obj = lookup(name, (Class) null, context);
        } catch (NamingException e) {
            throw new IllegalArgumentException(e);
        }
        return obj;
    }

    public static Object lookupByShortName(String shortName)
            throws NamingException {
        return lookup("java:comp/env/" + shortName, (Class) null);
    }

    public static Object lookupByShortNameNoTry(String shortName) {
        Object obj = null;
        try {
            obj = lookup("java:comp/env/" + shortName, (Class) null);
        } catch (NamingException e) {
            throw new IllegalArgumentException(e);
        }
        return obj;
    }

    public static Object lookup(Class type) throws NamingException {
        return lookup((String) null, type);
    }

    /**
     * Looks up a resource by its name or fully qualified type name. If name is
     * not null, then use it to look up and type is ignored. If name is null, then
     * try to use the fully qualified class name of type.
     *
     */
    public static Object lookup(String name, Class type, Context... context)
            throws NamingException {
        String nameToUse = null;
        if (name == null) {
            nameToUse = type.getName();
        } else {
            nameToUse = name;
        }
        Context c = null;
        if (context.length != 0 && context[0] != null) {
            c = context[0];
        } else {
            c = new InitialContext();
        }
        return c.lookup(nameToUse);
    }

    public static void lookupShouldFail(String name, StringBuilder reason) {
        reason.append("\t").append(lookupShouldFail(name));
    }

    public static String lookupShouldFail(String name) {
        String result = null;
        try {
            Context ic = new InitialContext();
            Object obj = ic.lookup(name);
            throw new RuntimeException("Expecting NamingException, but got " + obj
                    + ", when looking up " + name);
        } catch (NamingException e) {
            result = "Got expected " + e + ", when looking up " + name;
        }
        return result;
    }
}
