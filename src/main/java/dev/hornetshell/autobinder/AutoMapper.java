package dev.hornetshell.autobinder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

/**
 * AutoMapper
 *
 * Bind values from one class to your target class
 */
public class AutoMapper {

    private static Pattern reduceGetterPattern = Pattern.compile("^(get|is)([A-Z]{1}.*)$");
    private static Pattern reduceSetterPattern = Pattern.compile("^(set)([A-Z]{1}.*)$");

    public static AutoMapper getAutoMapper() {
        return new AutoMapper(true, true);
    }

    public static AutoMapper getAutoMapper(boolean ignoreUnknown, boolean acceptNull) {
        return new AutoMapper(ignoreUnknown, acceptNull);
    }

    private static String decapitalizeGetter(Method method) {
        return decapitalize(reduceGetterPattern.matcher(method.getName()).replaceAll("$2"));
    }

    private static String decapitalizeSetter(Method method) {
        return decapitalize(reduceSetterPattern.matcher(method.getName()).replaceAll("$2"));
    }

    private static String decapitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }

    private boolean ignoreUnknown;

    private boolean acceptNull;

    AutoMapper(boolean ignoreUnknown, boolean acceptNull) {
        this.ignoreUnknown = ignoreUnknown; // TODO: implement this
        this.acceptNull = acceptNull; // TODO: implement this
    }

    /**
     * Convert an auto-value object into your desired type
     * @param object the input object
     * @param <T> the type you'd like to use
     * @return an object of your goal type
     */
    public <T> T cast(Object object, AutoType<T> type) {
        if (object == null) {
            throw new IllegalArgumentException("The input object cannot be null!", new NullPointerException());
        }
        List<Method> goalMethods = type.getSetterMethods();
        List<Method> targetMethods = AutoType.getMethods(object.getClass(), AutoType.MethodType.GETTER);

        final T newInstance = type.getInstance();

        try {
            for (Method targetMethod : targetMethods) {
                for (Method goalMethod : goalMethods) {
                    if (decapitalizeGetter(targetMethod).equals(decapitalizeSetter(goalMethod))) {
                        goalMethod.invoke(newInstance, targetMethod.invoke(object));
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    String.format("An error occurred while mapping Object %s to type %s", object.toString(), type.getGoalClassName()),
                    e
            );
        }
        return newInstance;
    }

}
