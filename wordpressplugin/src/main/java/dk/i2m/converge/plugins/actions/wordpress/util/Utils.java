/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress.util;

import java.util.regex.Pattern;

/**
 *
 * @author fred
 */
public class Utils {

    private static Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?");

    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String string) {
        return doublePattern.matcher(string).matches();
    }
}
