package com.cse.common;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Common {
    public static int ERROR = -1;
    private static int sleepTime = 1000;
    public static void sleep(){
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {

        }
    }
}
