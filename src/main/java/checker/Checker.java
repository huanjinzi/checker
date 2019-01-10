package checker;

import checker.exp.BooleanCheckException;
import checker.exp.IntegerCheckException;
import checker.exp.ObjectCheckException;
import checker.exp.StringCheckException;

import java.util.Locale;

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
