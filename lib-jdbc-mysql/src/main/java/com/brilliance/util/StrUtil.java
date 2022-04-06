package com.brilliance.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Xiaobo Liu
 */
public class StrUtil {
    public static boolean empty(String param) {
        return (param == null || param.trim().length() < 1) ? true : false;
    }

    public static String nvl(String param) {
        return param == null ? "" : param.trim();
    }

    public static int parseInt(String param, int d) {
        int i = d;
        try {
            i = Integer.parseInt(param);
        } catch (Exception e) {
            //
        }
        return i;
    }

    public static int parseInt(String param) {
        return parseInt(param, 0);
    }

    public static long parseLong(String param) {
        long l = 0;
        try {
            l = Long.parseLong(param);
        } catch (Exception e) {
            //
        }
        return l;
    }

    public static boolean parseBoolean(String param) {
        if (empty(param))
            return false;
        switch (param.charAt(0)) {
            case '1':
            case 'y':
            case 'Y':
            case 't':
            case 'T':
                return true;
        }
        return false;
    }

    /* public static String escapeSQL(String input) {
         if (input == null || input.length() == 0)
             return input;
         StringBuffer buf = new StringBuffer();
         char ch = ' ';
         for (int i = 0; i < input.length(); i++) {
             ch = input.charAt(i);
             if (ch == '\\')
                 buf.append("\\\\");
             else if (ch == '\'')
                 buf.append("\'\'");
             else
                 buf.append(ch);
         }
         return buf.toString();
     }*/

    public static String replace(String mainString, String oldString, String newString) {
        if (mainString == null)
            return null;
        int i = mainString.lastIndexOf(oldString);
        if (i < 0)
            return mainString;
        StringBuffer mainSb = new StringBuffer(mainString);
        while (i >= 0) {
            mainSb.replace(i, i + oldString.length(), newString);
            i = mainString.lastIndexOf(oldString, i - 1);
        }
        return mainSb.toString();
    }


    public static final String[] split(String str, String delims) {
        StringTokenizer st = new StringTokenizer(str, delims);
        ArrayList list = new ArrayList();
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static void main(String[] args) {
        System.out.println(split("a,2", ","));
    }
//    public static String[] split(String source, String delim) {
//      String[] wordLists;
//      if (source == null) {
//        wordLists = new String[1];
//        wordLists[0] = source;
//        return wordLists;
//      }
//      StringTokenizer st = new StringTokenizer(source, delim);
//      int total = st.countTokens();
//      wordLists = new String[total];
//      for (int i = 0; i < total; i++) {
//        wordLists[i] = st.nextToken();
//      }
//      return wordLists;
//    }


}
