package dev.hornetshell.autobinder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AutoType
 *
 * A type wrapper for capturing your goal-type
 */
public class AutoType<T> {

    enum MethodType {
        GETTER,
        SETTER
    }

    private static Pattern getterPattern = Pattern.compile("^(get|is)[A-Z]{1}.*$");
    private static Pattern setterPattern = Pattern.compile("^set[A-Z]{1}.*$");

    private static ConcurrentHashMap<String, AutoType> typeCache = new ConcurrentHashMap<>();

    public static <T> AutoType<T> of(Class<T> clazz) {
        if (clazz != null) {
            final String className = String.format("%s.%s", clazz.getPackage().getName(), clazz.getCanonicalName());
            return typeCache.computeIfAbsent(className, s -> new AutoType<T>(clazz));
        } else {
            throw new IllegalArgumentException("Cannot create AutoType of null!", new NullPointerException());
        }
    }

    static List<Method> getMethods(Class<?> tClass, MethodType methodType) {
        Pattern selectedPattern = null;
        switch (methodType) {
            case GETTER: selectedPattern = getterPattern; break;
            case SETTER: selectedPattern = setterPattern; break;
            default: break;
        }
        Stream<Method> streamConcat = Arrays.stream(tClass.getMethods());
        Class<?> parent = tClass.getSuperclass();
        while(parent != null) {
            streamConcat = Stream.concat(streamConcat, Arrays.stream(parent.getMethods()));
            parent = parent.getSuperclass();
        }
        final Pattern finalSelectedPattern = selectedPattern;
        return streamConcat.filter(method -> {
            int modifiers = method.getModifiers();
            return Modifier.isPublic(modifiers);
        }).filter(method -> finalSelectedPattern.matcher(method.getName()).matches())
                .collect(Collectors.toList());
    }

    private Class<T> tClass;
    AutoType(Class<T> clazz) {
        tClass = clazz;
    }

    public List<Method> getGetterMethods() {
        return getMethods(tClass, MethodType.GETTER);
    }

    public List<Method> getSetterMethods() {
        return getMethods(tClass, MethodType.SETTER);
    }

    public T getInstance() {
        try {
            return tClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    String.format("Objects of type %s must have a zero argument constructor!", tClass.getCanonicalName()), e
            );
        }
    }

    String getGoalClassName() {
        return tClass.getCanonicalName();
    }

}
