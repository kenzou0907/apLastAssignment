import java.util.HashMap;
import java.util.Set;

public class StudentScore{
    private Stats stats = new Stats();
    private HashMap<String, Integer> studentScore = new HashMap<>();

    public void set(String test, Integer score){
        this.studentScore.put(test, score);
        this.stats.setStats(score);
    }

    public Integer getStudentScore(String problemNum){
        return this.studentScore.get(problemNum);
    }

    public Set<String> getStudentScoreKeySet(){
        return this.studentScore.keySet();
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
}
