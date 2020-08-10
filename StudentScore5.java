import java.util.HashMap;
import java.util.Map;

public class StudentScore5{
    Stats stats = new Stats();
    String id = null;
    HashMap<String, Integer> studentScore = new HashMap<>();
    HashMap<String, Integer> studentTime = new HashMap<>();

    void putScore(String test, Integer score){
        this.studentScore.put(test, score);
        this.stats.put(score);
    }

    void putTime(String test, String startTime, String endTime){
        String tmp[];
        Integer time = 0;
        if(!startTime.equals("")){
            tmp = startTime.split(":");
            time -= Integer.valueOf(tmp[0]) * 60 + Integer.valueOf(tmp[1]);
        }

        if(!endTime.equals("")){
            tmp = endTime.split(":");
            time += Integer.valueOf(tmp[0]) * 60 + Integer.valueOf(tmp[1]);
        }
        else time = -1;

        this.studentTime.put(test, time);
    }

    void putId(String id){
        if(this.id == null) this.id = id;
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

    Double averageOfTakenTime(){
        Double ret = 0.0;
        Double count = 0.0;
        for(Map.Entry<String, Integer> entry: this.studentTime.entrySet()){
            if(entry.getValue() != -1){
                ret += (double)entry.getValue();
                count += 1.0;
            }
        }

        if(count != 0.0) return ret / count;
        else return 0.0;
    }
}
