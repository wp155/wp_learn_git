package com.wp.jvm;

import ch.qos.logback.core.encoder.EchoEncoder;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Title: WpClassLoad
 * @author: wangpeng
 * @Description:
 * @date: 2021/11/7 21:49
 * @version: V1.0
 */
public class WpClassLoad extends ClassLoader {
    public static void main(String[] args) {
        // 创建类加载器
        ClassLoader classLoader = new WpClassLoad();
        Class<?> clazz = null;
        try {
            // 加载相应的类
            clazz = classLoader.loadClass("Hello");
            // 创建对象
            Object instance = clazz.getDeclaredConstructor().newInstance();
            System.out.println(clazz.getDeclaredMethods()[0].getName());
            // 调用实例方法
            Method method = clazz.getMethod(clazz.getDeclaredMethods()[0].getName());
            method.invoke(instance);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }


    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 如果支持包名, 则需要进行路径转换
        String resourcePath = name.replace(".", "/");
        // 文件后缀
        final String suffix = ".xlass";
        // 获取输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath + suffix);
        try {
            // 读取数据
            int length = inputStream.available();
            byte[] byteArray = new byte[length];
            inputStream.read(byteArray);
            // 转换
            byte[] classBytes = decode(byteArray);
            // 通知底层定义这个类
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            close(inputStream);
        }
    }


    // 解码
    private static byte[] decode(byte[] byteArray) {
        byte[] targetArray = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            targetArray[i] = (byte) (255 - byteArray[i]);
        }
        return targetArray;
    }

    // 关闭
    private static void close(Closeable res) {
        if (null != res) {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
