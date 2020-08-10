package io.github.nightlyside.enstaunofficialguide.misc;

import java.util.HashSet;
import java.util.Iterator;

public class Utils {

    static public String hashsetToString(HashSet<Integer> list) {
        StringBuilder strbul  = new StringBuilder();
        Iterator<Integer> iter = list.iterator();
        while(iter.hasNext())
        {
            strbul.append(iter.next());
            if(iter.hasNext()){
                strbul.append(",");
            }
        }
        return strbul.toString();
    }
}
