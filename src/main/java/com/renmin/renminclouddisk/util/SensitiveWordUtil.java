package com.renmin.renminclouddisk.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SensitiveWordUtil {
    public static Map<String, Object> dictionaryMap = new HashMap<>();

    public static void initMap(Collection<String> words) {
        Map<String, Object> map = new HashMap<>(words.size());
        Map<String, Object> curMap;
        Iterator<String> iterator = words.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            curMap = map;
            int len = word.length();
            for (int i = 0; i < len; i++) {
                String key = String.valueOf(word.charAt(i));
                Map<String, Object> wordMap = (Map<String, Object>) curMap.get(key);
                if (wordMap == null) {
                    wordMap = new HashMap<>(2);
                    wordMap.put("isEnd", "0");
                    curMap.put(key, wordMap);
                }
                curMap = wordMap;
                if (i == len - 1) {
                    curMap.put("isEnd", "1");
                }
            }
        }
        dictionaryMap = map;
    }

    public static int checkWord(String text, int beginIndex) {
        boolean isEnd = false;
        int wordLength = 0;
        Map<String, Object> curMap = dictionaryMap;
        int len = text.length();
        for (int i = beginIndex; i < len; i++) {
            String key = String.valueOf(text.charAt(i));
            curMap = (Map<String, Object>) curMap.get(key);
            if (curMap == null) {
                break;
            } else {
                wordLength++;
                if ("1".equals(curMap.get("isEnd"))) {
                    isEnd = true;
                }
            }
        }
        if (!isEnd) {
            wordLength = 0;
        }
        return wordLength;
    }

    public static boolean mathWord(String text) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            int wordLength = checkWord(text, i);
            if (wordLength > 0) {
                return true;
            }
        }
        return false;
    }
}
