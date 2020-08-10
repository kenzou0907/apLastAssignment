import java.util.Comparator;

public class StudentTakenTimeComparator implements Comparator<StudentScore5> {
    public int compare(StudentScore5 ss1, StudentScore5 ss2){
        return Double.compare(ss1.averageOfTakenTime(), ss2.averageOfTakenTime());
    }
}
