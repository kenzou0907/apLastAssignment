import java.util.Comparator;

public class StudentIdComparator implements Comparator<StudentScore5> {
    public int compare(StudentScore5 ss1, StudentScore5 ss2){
        return Integer.valueOf(ss1.getId()).compareTo(Integer.valueOf(ss2.getId()));
    }
}
