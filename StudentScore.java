/**
 * 課題名　最終課題
 * 学生証番号　953580
 * 氏名　近藤 英雅
 */

import java.util.HashMap;

public class StudentScore{
    Stats stats = new Stats();
    HashMap<String, Integer> studentScore = new HashMap<>();

    void put(String test, Integer score){
        this.studentScore.put(test, score);
        this.stats.put(score);
    }

    Integer getMax(){
        return stats.max;
    }

    Integer getMin(){
        return stats.min;
    }

    Double getAvr(){
        return stats.avr;
    }
}
