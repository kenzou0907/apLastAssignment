import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScoreAnalyzer3{
    void run(String[] args) throws IOException{
        // 一応事前処理を作成しておく
        if(args.length < 1) {
            System.out.println("引数を１つ入力してください");
            return;
        }
        File inputFile = new File(args[0]);
        if(!inputFile.exists()){
            System.out.println(args[0] + "は存在しません");
            return;
        }

        // 読み込んだファイルの得点状況を辞書にする
        HashMap<String, StudentScore> scoreMap = this.readFile(inputFile);

        // 書き出す
        this.manageScoreOutput(scoreMap, this.makeProblemNumberList(scoreMap));
    }

    // 与えられたファイルを読み込むメソッド
    HashMap<String, StudentScore> readFile(File inputFile) throws IOException{
        HashMap<String, StudentScore> scoreMap = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String[] splitLine;
        String line;

        // 問題番号の一致を確認した上で辞書に点数を格納する処理
        while((line = in.readLine()) != null){
            splitLine = line.split(",", -1);
            this.storeScoreInMap(scoreMap, splitLine);
        }

        in.close();
        return scoreMap;
    }

    // 問題番号が何かを作成する
    ArrayList<Integer> makeProblemNumberList(HashMap<String, StudentScore> scoreMap){
        ArrayList<Integer> problemNumberList = new ArrayList<>();
        for(Map.Entry<String, StudentScore> entry: scoreMap.entrySet()){
            for(String problemNum: entry.getValue().studentScore.keySet()){
                this.addProblemNumberList(problemNumberList, problemNum);
            }
        }

        return problemNumberList;
    }

    // 問題番号のリストに番号を追加する
    void addProblemNumberList(ArrayList<Integer> problemNumberList, String problemNum){
        if(!problemNumberList.contains(Integer.valueOf(problemNum))){
            problemNumberList.add(Integer.valueOf(problemNum));
        }
    }

    // マップを変更するメソッド
    void storeScoreInMap(HashMap<String, StudentScore> scoreMap, String[] splitLine){
        // 学生番号がすでにマップに存在している時
        if(scoreMap.get(splitLine[3]) != null){
            if(!splitLine[4].equals("")){
                scoreMap.get(splitLine[3]).put(splitLine[2], Integer.valueOf(splitLine[4]));
            }else{
                scoreMap.get(splitLine[3]).put(splitLine[2], -1);
            }
        }
        // 学生IDがマップにない時
        else{
            StudentScore tmp = new StudentScore();
            if(!splitLine[4].equals("")){
                tmp.put(splitLine[2], Integer.valueOf(splitLine[4]));
            }else{
                tmp.put(splitLine[2], -1);
            }
            scoreMap.put(splitLine[3], tmp);
        }
    }

    // 出力を管理するメソッド
    void manageScoreOutput(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList){
        Collections.sort(problemNumList);
        HashMap<Integer, Stats> totalStats = this.makeTotalStats(problemNumList);

        // 生徒のデータの出力
        this.outputStudentScoreData(scoreMap, problemNumList, totalStats);
        // 全体の点数の出力
        this.outputTheTotalScore(totalStats, problemNumList);
    }

    // 生徒のデータの出力
    void outputStudentScoreData(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList, HashMap<Integer, Stats> totalStats){
        for(Map.Entry<String, StudentScore> entry: scoreMap.entrySet()){
            String id = entry.getKey();
            StudentScore studentData = entry.getValue();

            // idと点数の出力
            System.out.print(id + ",");
            for(Integer problemNum: problemNumList){
                this.studentScoreWrite(problemNum, studentData);
                // 全体の点数に反映
                this.replaceTotalStats(problemNum, totalStats, studentData);
            }

            // 最大値の出力
            System.out.print(Integer.toString(studentData.getMax()) + ",");

            // 最小値の出力
            System.out.print(Integer.toString(studentData.getMin()) + ",");

            // 平均値の出力
            System.out.printf("%7.6f%n", studentData.getAvr());
        }
    }

    // 全体の平均とかに追加するメソッド
    void replaceTotalStats(Integer problemNum, HashMap<Integer, Stats> totalStats, StudentScore studentData){
        if(studentData.studentScore.get(Integer.toString(problemNum)) != null){
            totalStats.get(problemNum).put(studentData.studentScore.get(Integer.toString(problemNum)));
        }
    }

    // 全体の平均とかの外側を作成するメソッド
    HashMap<Integer, Stats> makeTotalStats(ArrayList<Integer> problemNumList){
        HashMap<Integer, Stats> totalStats = new HashMap<>();
        for(Integer problemNum: problemNumList){
            totalStats.put(problemNum, new Stats());
        }
        return totalStats;
    }

    // 生徒の点数を出力
    void studentScoreWrite(Integer problemNum, StudentScore studentData){
        if(studentData.studentScore.get(Integer.toString(problemNum)) == null || studentData.studentScore.get(Integer.toString(problemNum)) == -1){
            System.out.print(",");
        }else{
            System.out.print(Integer.toString(studentData.studentScore.get(Integer.toString(problemNum))) + ",");
        }
    }

    // 全体の値の出力
    void outputTheTotalScore(HashMap<Integer, Stats> totalStats, ArrayList<Integer> problemNumList){
        // 最大値の出力
        for(Integer problemNum: problemNumList){
            System.out.print("," + Integer.toString(totalStats.get(problemNum).max));
        }
        System.out.println();

        // 最小値の出力
        for(Integer problemNum: problemNumList){
            System.out.print("," + Integer.toString(totalStats.get(problemNum).min));
        }
        System.out.println();

        // 平均値の出力
        for(Integer problemNum: problemNumList){
            System.out.printf(",%.6f", totalStats.get(problemNum).avr);
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer3 app = new ScoreAnalyzer3();
        app.run(args);
    }
}
