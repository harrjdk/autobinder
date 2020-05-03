package dev.hornetshell.autobinder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * AutoMapper
 * <p>
 * Bind values from one class to your target class
 */
public class AutoMapper {

    private static ConcurrentHashMap<String, Function> converterCache = new ConcurrentHashMap<>();
    private static Vector<AutoMapper> autoMapperVector = new Vector<>();

    private static Pattern reduceGetterPattern = Pattern.compile("^(get|is)([A-Z].*)$");
    private static Pattern reduceSetterPattern = Pattern.compile("^(set)([A-Z].*)$");

    public static AutoMapper getAutoMapper() {
        return new AutoMapper(true, true);
    }

    public static AutoMapper getAutoMapper(boolean ignoreUnknown, boolean acceptNull) {
        final AutoMapper autoMapper = new AutoMapper(ignoreUnknown, acceptNull);
        autoMapperVector.add(autoMapper);
        return autoMapper;
    }

    public static void registerGlobalConverter(final String converterName, Function function) {
        converterCache.put(converterName, function);
        autoMapperVector.stream().forEach(autoMapper -> autoMapper.registerConverter(converterName, function));

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

        char[] c = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }

    private static List<String> getMatchingProperties(Method method) {
        final Conversion conversionAnnotation = method.getAnnotation(Conversion.class);
        final Converter converterAnnotation = method.getAnnotation(Converter.class);
        if (conversionAnnotation != null) {
            return Arrays.stream(conversionAnnotation.value())
                    .map(converter -> converter.matchingProperty())
                    .filter(s -> !s.trim().isEmpty())
                    .collect(Collectors.toList());
        } else if (converterAnnotation != null) {
            if (!converterAnnotation.matchingProperty().trim().isEmpty()) {
                return Arrays.asList(converterAnnotation.matchingProperty());
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    private boolean ignoreUnknown;

    private boolean acceptNull;

    private ConcurrentHashMap<String, Function> localConverterCache;

    AutoMapper(boolean ignoreUnknown, boolean acceptNull) {
        this.ignoreUnknown = ignoreUnknown;
        this.acceptNull = acceptNull;
        localConverterCache = new ConcurrentHashMap<>();
        localConverterCache.putAll(converterCache);
    }

    public void registerConverter(final String converterName, Function function) {
        localConverterCache.put(converterName, function);
    }

    /**
     * Convert an auto-value object into your desired type
     *
     * @param object the input object
     * @param <T>    the type you'd like to use
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
                final String incomingProperty = decapitalizeGetter(targetMethod);
                boolean foundMatch = false;
                for (Method goalMethod : goalMethods) {
                    final List<String> matchingProperties = getMatchingProperties(goalMethod);
                    if (incomingProperty != null && incomingProperty.equals(decapitalizeSetter(goalMethod))
                            || matchingProperties.contains(incomingProperty)) {
                        final Object targetInvocationValue = targetMethod.invoke(object);
                        if (!acceptNull && targetInvocationValue == null) {
                            throw new NullPointerException(
                                    String.format("Null value received from %s", targetMethod.getName())
                            );
                        }
                        final Conversion conversionAnnotation = goalMethod.getAnnotation(Conversion.class);
                        final Converter converterAnnotation = goalMethod.getAnnotation(Converter.class);
                        if (conversionAnnotation != null) {
                            List<Function> converterFunctions = Arrays.stream(conversionAnnotation.value())
                                    .map(converter -> localConverterCache.get(converter.value()))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                            if (converterFunctions.size() > 1) {
                                throw new IllegalStateException("Multiple converters some how registered to the same namespace!");
                            } else if (converterFunctions.size() == 1) {
                                goalMethod.invoke(newInstance, converterFunctions.get(0).apply(targetInvocationValue));
                            } else {
                                goalMethod.invoke(newInstance, targetInvocationValue);
                            }
                        } else if (converterAnnotation != null) {
                            final Function converterFunction = localConverterCache.get(converterAnnotation.value());
                            if (converterFunction != null) {
                                goalMethod.invoke(newInstance, converterFunction.apply(targetInvocationValue));
                            } else {
                                throw new IllegalStateException(String.format("No converter registered for namespace %s", converterAnnotation.value()));
                            }
                        } else {
                            goalMethod.invoke(newInstance, targetInvocationValue);
                        }
                        foundMatch = true;
                        break;
                    }
                }
                if (!ignoreUnknown && !foundMatch) {
                    throw new IllegalArgumentException(
                            String.format("Could not find matching method to %s", targetMethod.getName())
                    );
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
