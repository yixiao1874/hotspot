package com.gtja.util;

import us.codecraft.webmagic.Page;

import java.util.HashMap;
import java.util.List;

public class StockUtil {
    public static void addToMap(List<String> list, HashMap<String,Integer> hashMap){
        if (list!=null){
            for (String s : list){
                addToMap(s,hashMap);
            }
        }
    }
    public static void addToMap(String s, HashMap<String,Integer> hashMap){
        if (hashMap.get(s)==null){
            hashMap.put(s,1);
        }else{
            hashMap.put(s,hashMap.get(s)+1);
        }
    }
}
