/**
 * 課題名　最終課題
 * 学生証番号　953580
 * 氏名　近藤 英雅
 */

import java.util.Comparator;

public class StudentIdComparator implements Comparator<StudentScore5> {
    public int compare(StudentScore5 ss1, StudentScore5 ss2){
        return Integer.valueOf(ss1.id).compareTo(Integer.valueOf(ss2.id));
    }
}
