import java.util.Comparator;

public class StudentScoreComparator implements Comparator<StudentScore5> {
    public int compare(StudentScore5 ss1, StudentScore5 ss2){
        return Double.compare(ss1.getAvr(), ss2.getAvr());
    }
}
