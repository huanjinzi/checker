/*
 *  Copyright (C) 2019 Yuan Huan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hz.checker;

import com.hz.checker.exp.BooleanCheckException;
import com.hz.checker.exp.IntegerCheckException;
import com.hz.checker.exp.ObjectCheckException;
import com.hz.checker.exp.StringCheckException;

import java.util.Locale;
/**
 * Usage:
 *
 * <code>
 *     // without message.
 *     Checker.check(int);
 *     Checker.check(boolean);
 *     Checker.check(Object);
 *
 *     // with message.
 *     Checker.check(int,String);
 *     Checker.check(boolean,String);
 *     Checker.check(Object,String);
 *
 * </code>
 *
 * Checker is default in debug mode,it means <code>Checker.dcheck()</>
 * will work. */
public class Checker {

    enum ExceptionType {
        Boolean,
        Integer,
        String,
        Object
    }

    /**
     * indicate check only in debug version.
     */
    private static boolean DEBUG = true;

    /**
     * @param condition if condition is false,will interrupt application.
     */
    public static void check(boolean condition) {
        check(condition, null);
    }

    /**
     * @param condition if condition is false,will interrupt application.
     * @param message   when application interrupted,show message.
     */
    public static void check(boolean condition, String message) {
        if (message == null) message = "";
        message = "[boolean=false]" + message;
        check(condition, message, ExceptionType.Boolean);
    }

    /**
     * @param condition if condition is false,will interrupt application.
     * @param message   when application interrupted,show message.
     * @param type      check type. see ExceptionType
     */
    private static void check(boolean condition, String message, ExceptionType type) {
        if (!condition) throwException(message, type);
    }

    /**
     * @param number if condition is <= 0,will interrupt application.
     */
    public static void check(int number) {
        check(number, null);
    }

    /**
     * @param number  if condition is <= 0,will interrupt application.
     * @param message when application interrupted,show message.
     */
    public static void check(int number, String message) {
        if (message == null) {
            message = String.format(Locale.ENGLISH,"[number=%d]", number);
        } else {
            message = String.format(Locale.ENGLISH,"[number=%d] %s",number,message);
        }
        check(number > 0, message, ExceptionType.Integer);
    }

    /**
     * @param obj if obj is null,will interrupt application.
     */
    public static void check(Object obj) {
        check(obj, null);
    }

    /**
     * @param obj     if obj is null,will interrupt application.
     * @param message when application interrupted,show message.
     */
    public static void check(Object obj, String message) {

        if (message == null) message = "";
        String objMsg = "[obj=null]" + message;
        check(obj != null, message, ExceptionType.Object);

        /*check string length equal 0.*/
        if (obj instanceof String) {
            String string = (String) obj;
            String strMsg = "[string=\"\"]" + message;
            check(!string.isEmpty(), strMsg, ExceptionType.String);
        }
    }

    /**
     * in debug mode,has same behavior with check(boolean).
     *
     * @param condition if condition is false,will interrupt application.
     */
    public static void dcheck(boolean condition) {
        dcheck(condition, null);
    }

    /**
     * in debug mode,has same behavior with check(boolean,String).
     *
     * @param condition if condition is false,will interrupt application.
     * @param message   when application interrupted,show message.
     */
    public static void dcheck(boolean condition, String message) {
        if (!DEBUG) return;

        check(condition, message);
    }

    /**
     * in debug mode,has same behavior with check(Object).
     *
     * @param obj if obj is null,will interrupt application.
     */
    public static void dcheck(Object obj) {
        dcheck(obj, null);
    }

    /**
     * in debug mode,has same behavior with check(Object,String).
     *
     * @param obj     if obj is null,will interrupt application.
     * @param message when application interrupted,show message.
     */
    public static void dcheck(Object obj, String message) {
        if (!DEBUG) return;

        check(obj, message);
    }


    /**
     * in debug mode,has same behavior with check(Object).
     *
     * @param number if condition is <= 0,will interrupt application.
     */
    public static void dcheck(int number) {
        dcheck(number, null);
    }

    /**
     * in debug mode,has same behavior with check(Object,String).
     *
     * @param number  if condition is <= 0,will interrupt application.
     * @param message when application interrupted,show message.
     */
    public static void dcheck(int number, String message) {
        if (!DEBUG) return;

        check(number, message);
    }

    /**
     * @param message RuntimeException's message.
     */
    private static void throwException(String message, ExceptionType type) {
        String checkPoint = checkPoint();
        if (message == null) message = "";

        switch (type) {
            case Object:
                throw new ObjectCheckException(checkPoint + message);
            case String:
                throw new StringCheckException(checkPoint + message);
            case Boolean:
                throw new BooleanCheckException(checkPoint + message);
            case Integer:
                throw new IntegerCheckException(checkPoint + message);
        }
    }

    /**
     * 0
     * ___|||||___
     * 1
     * <p>
     * when 0 arrived,trigger becomes true.
     * when 1 arrived,foreach is break.
     *
     * @return get Checker.check() call position.
     */
    private static String checkPoint() {
        StringBuilder checkPoint = new StringBuilder();

        Thread current = Thread.currentThread();
        StackTraceElement[] elements = current.getStackTrace();

        boolean trigger = false;
        StackTraceElement checkPointElement = null;
        for (StackTraceElement element : elements) {
            if (trigger) {
                if (!element.getClassName().equals(Checker.class.getName())) {
                    checkPointElement = element;
                    break;
                }
            }
            if (element.getClassName().equals(Checker.class.getName()))
                trigger = true;
        }

        // checkPointElement will never be null.
        checkPoint.append('(');
        checkPoint.append(checkPointElement.getFileName());
        checkPoint.append(':');
        checkPoint.append(checkPointElement.getLineNumber());
        checkPoint.append(')');

        return checkPoint.toString();
    }

    /**
     * indicate current version is debug.
     */
    public static void debug() {
        DEBUG = true;
    }

    /**
     * indicate current version is release.
     */
    public static void release() {
        DEBUG = false;
    }
}
