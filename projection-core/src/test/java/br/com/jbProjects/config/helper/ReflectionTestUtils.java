package br.com.jbProjects.config.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public class ReflectionTestUtils {

    private ReflectionTestUtils(){}

    public static Object getField(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        return getField(field, target);
    }

    public static Object getField(Field field, Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot read field %s in %s".formatted(field.getName(), target.getClass()), e);

        }
    }

    public static void setField(Object target, String name, Object value) {
        try {
            Field field = findField(target.getClass(), name);
            setField(field, target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invokeMethod(Object target, String name, Object... args) {
        Method method = findMethodAssignable(target.getClass(), name, args);
        if(method == null){
            throw new RuntimeException("Method %s not found in %s".formatted(name, target.getClass()));
        }
        return invokeMethod(method, target, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object target, Object... args) {
        method.setAccessible(true);
        try {
            return (T) method.invoke(target, args);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Cannot invoke method %s in %s".formatted(method.getName(), target.getClass()), e);
        }
    }

    public static Method findMethodAssignable(Class<?> type, String name, Object... args) {
        outer:
        for (Method method : type.getDeclaredMethods()) {
            if (!method.getName().equals(name)) continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != args.length) continue;

            for (int i = 0; i < params.length; i++) {
                if (!params[i].isAssignableFrom(args[i].getClass())) {
                    continue outer;
                }
            }
            return method;
        }
        return null;
    }

    public static Field findField(Class<?> type, String name) {
        while (type != null) {
            try {
                return type.getDeclaredField(name);
            } catch (NoSuchFieldException ignore) {}
            type = type.getSuperclass();
        }
        throw new RuntimeException("Field '%s' not found".formatted(name));
    }
}
