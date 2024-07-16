package ru.kostrikov.gym_booking.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class ParametersProcessor {
    @SneakyThrows
    public static <T, B, R> Optional<R> process(HttpServletRequest req, Class<T> dtoClass, Class<B> builderClass, Function<T, R> func) {
        return process(req, dtoClass, builderClass, Collections.emptyMap(), func);
    }

    @SneakyThrows
    public static <T, B, R, S> Optional<R> process(HttpServletRequest req, Class<T> dtoClass, Class<B> builderClass, Map<String, S> specialFieldsBuilder, Function<T, R> func) {
        Enumeration<String> parameterNames = req.getParameterNames();
        Field[] fields = dtoClass.getDeclaredFields();
        B builder = null;

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();

            for (Field field : fields) {
                if (paramName.equals(field.getName())) {
                    if (builder == null) {
                        builder = createBuilder(dtoClass);
                    }

                    Method declaredMethod = findSetter(builderClass, field);
                    if (declaredMethod != null) {
//                        if (specialFieldBuilder.get(field.getName()) == null) {
                        declaredMethod.invoke(builder, convertParameter(req.getParameter(paramName), field.getType()));
//                        } else {
//                            declaredMethod.invoke(builder, specialFieldBuilder.get(field.getName()));
//                        }
                    }
                }
            }
        }

        if (builder != null) {

            for (var specialFieldKey : specialFieldsBuilder.keySet()) {
                try {
                    Field declaredField = dtoClass.getDeclaredField(specialFieldKey);
                    Method setterMethod = findSetter(builderClass, declaredField);
                    if (setterMethod != null) {
                        setterMethod.invoke(builder, specialFieldsBuilder.get(declaredField.getName()));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }


        T dto = buildDto(builder, dtoClass);
        if (dto != null) {
            return Optional.ofNullable(func.apply(dto));
        }
        return Optional.empty();
    }

    // Method to create an instance of the builder class
    private static <T, B> B createBuilder(Class<T> builderClass) throws Exception {
        Method method = builderClass.getDeclaredMethod("builder");

        return (B) method.invoke(null);
    }

    // Method to find the setter method for a field in the builder class
    private static <B> Method findSetter(Class<B> builderClass, Field field) throws NoSuchMethodException {
//        String setterName = "set" + capitalize(field.getName());
        try {
//            return builderClass.getDeclaredMethod(setterName, field.getType());
            return builderClass.getDeclaredMethod(field.getName(), field.getType());
        } catch (NoSuchMethodException e) {
            // Handle if setter method is not found
            return null;
        }
    }

    // Method to build the DTO object from the builder
    private static <T, B> T buildDto(B builder, Class<T> dtoClass) throws Exception {
        Method buildMethod = builder.getClass().getDeclaredMethod("build");
        return (T) buildMethod.invoke(builder);
    }

    // Utility method to capitalize the first letter of a string
//    private static String capitalize(String s) {
//        if (s == null || s.isEmpty()) {
//            return s;
//        }
//        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
//    }

    // Utility method to convert String parameter to target type
    private static Object convertParameter(String parameter, Class<?> targetType) {
        // Add conversion logic as needed (e.g., parsing, type conversion)
        if (targetType == String.class) {
            return parameter;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(parameter);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(parameter);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(parameter) || "on".equals(parameter) || parameter.isEmpty();
        }
        // Handle other types as necessary
        return null;
    }

}
