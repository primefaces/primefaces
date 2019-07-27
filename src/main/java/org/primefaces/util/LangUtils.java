/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LangUtils {

    private LangUtils() {
    }

    public static boolean isValueEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isValueBlank(String str) {
        if (str == null) {
            return true;
        }

        int strLen = str.length();
        if (strLen == 0) {
            return true;
        }

        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(Object[] array, Object object) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(object)) {
                return true;
            }
        }

        return false;
    }

    public static String[] concat(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        int length = length1 + length2;
        String[] dest = new String[length];
        System.arraycopy(array1, 0, dest, 0, length1);
        System.arraycopy(array2, 0, dest, length1, length2);
        return dest;
    }

    public static String[] concat(String[]... arrays) {
        int destSize = 0;
        for (int i = 0; i < arrays.length; i++) {
            destSize += arrays[i].length;
        }

        String[] dest = new String[destSize];

        int lastIndex = 0;
        for (int i = 0; i < arrays.length; i++) {
            String[] array = arrays[i];
            System.arraycopy(array, 0, dest, lastIndex, array.length);
            lastIndex += array.length;
        }

        return dest;
    }

    public static boolean containsIgnoreCase(String[] array, String searchedText) {
        if (array == null || array.length == 0) {
            return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(searchedText)) {
                return true;
            }
        }

        return false;
    }

    public static final <T> List<T> unmodifiableList(T... args) {
        return Collections.unmodifiableList(Arrays.asList(args));
    }

    public static Class tryToLoadClassForName(String name) {
        try {
            return loadClassForName(name);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class loadClassForName(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name, false, LangUtils.class.getClassLoader());
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(name, false, getContextClassLoader());
            }
            catch (ClassNotFoundException e2) {
                throw e2;
            }
        }
    }

    public static ClassLoader getContextClassLoader() {
        // TODO we probably need to check the SecurityManager and do this via AccessController
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader getCurrentClassLoader(Class clazz) {
        ClassLoader cl = getContextClassLoader();

        if (cl == null && clazz != null) {
            cl = clazz.getClassLoader();
        }

        return cl;
    }

    /**
     * NOTE: copied from DeltaSpike
     *
     * @param currentClass current class
     *
     * @return class of the real implementation
     */
    public static Class getUnproxiedClass(Class currentClass) {
        Class unproxiedClass = currentClass;

        while (isProxiedClass(unproxiedClass)) {
            unproxiedClass = unproxiedClass.getSuperclass();
        }

        return unproxiedClass;
    }

    /**
     * NOTE: copied from DeltaSpike
     *
     * Analyses if the given class is a generated proxy class
     *
     * @param currentClass current class
     *
     * @return true if the given class is a known proxy class, false otherwise
     */
    public static boolean isProxiedClass(Class currentClass) {
        if (currentClass == null || currentClass.getSuperclass() == null) {
            return false;
        }

        return currentClass.getName().startsWith(currentClass.getSuperclass().getName())
                && currentClass.getName().contains("$$");
    }
}
