/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.zoidberg.calculon.notation;

import java.util.Vector;

/**
 *
 * @author barrys
 */
public class StringUtils {

    public static String[] split(String s) {
        return split(s, ' ');
    }

    public static String[] split(String s, char sep) {
        Vector rv = new Vector();
        int idx;
        while((idx = s.indexOf(sep)) != -1) {
            rv.addElement(s.substring(0, idx));
            s = s.length() > idx+1 ? s.substring(idx+1) : "";
        }
        if(s.length() > 0) {
            rv.addElement(s);
        }
        String[] rs = new String[rv.size()];
        for(int i = 0; i < rv.size(); i++) {
            rs[i] = (String) rv.elementAt(i);
        }
        return rs;
    }
}
