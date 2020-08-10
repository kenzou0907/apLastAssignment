import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
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

    // ターミナル出力と同時に出力を行うメソッド
    void printAndWrite(String line, PrintWriter out) throws IOException{
        System.out.print(line);
        out.print(line);
    }

    void avrPrintfAndWrite(Double line, PrintWriter out) throws IOException{
        System.out.printf("%7.6f", line);
        out.printf("%7.6f", line);
    }

    // ターミナル出力と同時に出力を行うメソッド
    void printlnAndWrite(PrintWriter out) throws IOException{
        System.out.println();
        out.println();
    }

    // 出力を管理するメソッド
    void manageScoreOutput(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList) throws IOException{
        PrintWriter out = new PrintWriter(new File("ScoreAnalyzer3.csv"));
        Collections.sort(problemNumList);
        HashMap<Integer, Stats> totalStats = this.makeTotalStats(problemNumList);

        // 生徒のデータの出力
        this.outputStudentScoreData(scoreMap, problemNumList, totalStats, out);
        // 全体の点数の出力
        this.outputTheTotalScore(totalStats, problemNumList, out);
        out.close();
    }

    // 生徒のデータの出力
    void outputStudentScoreData(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList, HashMap<Integer, Stats> totalStats, PrintWriter out) throws IOException{
        for(Map.Entry<String, StudentScore> entry: scoreMap.entrySet()){
            String id = entry.getKey();
            StudentScore studentData = entry.getValue();

            // idと点数の出力
            this.printAndWrite(id + ",", out);
            for(Integer problemNum: problemNumList){
                this.studentscoreWrite(problemNum, studentData, out);
                // 全体の点数に反映
                this.replaceTotalStats(problemNum, totalStats, studentData);
            }

            // 最大値の出力
            this.printAndWrite(Integer.toString(studentData.getMax()) + ",", out);

            // 最小値の出力
            this.printAndWrite(Integer.toString(studentData.getMin()) + ",", out);

            // 平均値の出力
            this.avrPrintfAndWrite(studentData.getAvr(), out);
            this.printlnAndWrite(out);
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
    void studentscoreWrite(Integer problemNum, StudentScore studentData, PrintWriter out) throws IOException{
        if(studentData.studentScore.get(Integer.toString(problemNum)) == null || studentData.studentScore.get(Integer.toString(problemNum)) == -1){
            this.printAndWrite(",", out);
        }else{
            this.printAndWrite(Integer.toString(studentData.studentScore.get(Integer.toString(problemNum))) + ",", out);
        }
    }

    // 全体の値の出力
    void outputTheTotalScore(HashMap<Integer, Stats> totalStats, ArrayList<Integer> problemNumList, PrintWriter out) throws IOException{
        // 最大値の出力
        for(Integer problemNum: problemNumList){
            this.printAndWrite("," + Integer.toString(totalStats.get(problemNum).max), out);
        }
        this.printlnAndWrite(out);

        // 最小値の出力
        for(Integer problemNum: problemNumList){
            this.printAndWrite("," + Integer.toString(totalStats.get(problemNum).min), out);
        }
        this.printlnAndWrite(out);

        // 平均値の出力
        for(Integer problemNum: problemNumList){
            this.printAndWrite(",", out);
            this.avrPrintfAndWrite(totalStats.get(problemNum).avr, out);
        }
        this.printlnAndWrite(out);
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer3 app = new ScoreAnalyzer3();
        app.run(args);
    }
}
