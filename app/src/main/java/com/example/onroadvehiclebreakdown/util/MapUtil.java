package com.example.onroadvehiclebreakdown.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapUtil {

    public static String mapToString(Map<String,String> viewMap)
    {
        String data="";

        Set<String> keySet=viewMap.keySet();

        for(String s : keySet)
        {
            data=data+s+":"+viewMap.get(s)+"\n";
        }

        return data;
    }

    public static Map<String,String> stringToMap(String viewMap)
    {
        Map<String,String> dataMap=new HashMap<String,String>();

        String[] tokens=viewMap.split("\n");

        for(String s : tokens)
        {
            String[] subTokens=s.split(":");

            dataMap.put(subTokens[0],subTokens[1]);
        }

        return dataMap;
    }
}
