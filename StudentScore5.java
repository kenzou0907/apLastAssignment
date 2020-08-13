import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class StudentScore5{
    private Stats stats = new Stats();
    private String id = null;
    private HashMap<String, Integer> studentScore = new HashMap<>();
    private HashMap<String, Integer> studentTime = new HashMap<>();

    public void putScore(String test, Integer score){
        this.studentScore.put(test, score);
        this.stats.setStats(score);
    }

    public void putTime(String test, String startTime, String endTime){
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

    public Integer getStudentTime(String problemNum){
        return this.studentTime.get(problemNum);
    }

    public void putId(String id){
        if(this.id == null) this.id = id;
    }

   public Integer getMax(){
        return this.stats.getMax();
    }

    public Integer getMin(){
        return this.stats.getMin();
    }

    public Double getAvr(){
        return this.stats.getAvr();
    }

    public Integer getCount(){
        return this.stats.getCount();
    }

    public Integer getStudentScore(String problemNum){
        return this.studentScore.get(problemNum);
    }

    public Set<String> getStudentScoreKeySet(){
        return this.studentScore.keySet();
    }

    public String getId(){
        return this.id;
    }

    public Collection<Integer> getStudentTimeValueSet(){
        return this.studentTime.values();
    }

    public Double averageOfTakenTime(){
        Double ret = 0.0;
        Double count = 0.0;
        for(Map.Entry<String, Integer> entry: this.studentTime.entrySet()){
            if(entry.getValue() != -1){
                ret += (double)entry.getValue();
                count += 1.0;
            }
        }

        if(count != 0.0) return ret / count;
        else return -1.0;
    }
}
