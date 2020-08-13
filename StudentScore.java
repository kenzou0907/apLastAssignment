import java.util.HashMap;

public class StudentScore{
    Stats stats = new Stats();
    HashMap<String, Integer> studentScore = new HashMap<>();

    void put(String test, Integer score){
        this.studentScore.put(test, score);
        this.stats.put(score);
    }

    Integer getMax(){
        return this.stats.max;
    }

    Integer getMin(){
        return this.stats.min;
    }

    Double getAvr(){
        return this.stats.avr;
    }
}
