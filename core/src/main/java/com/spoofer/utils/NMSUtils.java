package com.spoofer.utils;

import com.spoofer.obj.IFakeEntity;
import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NMSUtils {

    static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static Field getField(Class<?> clazz, String name, boolean log) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static IFakeEntity getFakeEntityInstance(String name, boolean visible,org.bukkit.Location location) {
        String version = Bukkit.getBukkitVersion().split("-")[0];

        try {
            if(version.equals("1.20.6")){
                Class<?> clazz = Class.forName("com.spoofer.obj.FakeEntityR4");
                Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, boolean.class, org.bukkit.Location.class);

                return (IFakeEntity) constructor.newInstance(name, visible, location);
            }else if(version.equals("1.20.1")){
                Class<?> clazz = Class.forName("com.spoofer.obj.FakeEntityR3");
                Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, boolean.class, org.bukkit.Location.class);

                return (IFakeEntity) constructor.newInstance(name, visible, location);
            }else{
                Class<?> clazz = Class.forName("com.spoofer.obj.FakeEntityR1");
                Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, boolean.class, org.bukkit.Location.class);

                return (IFakeEntity) constructor.newInstance(name, visible, location);

            }

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public static MethodHandle getSetter(Class<?> clazz, String name, boolean log) {
        try {
            return LOOKUP.unreflectSetter(getField(clazz, name, log));
        } catch (Exception ignored) {
        }
        return null;
    }

}
