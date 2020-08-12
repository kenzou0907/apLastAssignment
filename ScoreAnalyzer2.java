import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScoreAnalyzer2{
    void run(String[] args) throws IOException{
        // 一応事前処理を作成しておく
        if(args.length < 1) return;
        File inputFile = new File(args[0]);
        if(!inputFile.exists()) return;

        // 読み込んだファイルの得点状況を辞書にして出力する
        this.manageScoreOutput(this.readFile(inputFile));
    }

    // 与えられたファイルを読み込むメソッド
    HashMap<String, HashMap<String, Integer>> readFile(File inputFile) throws IOException{
        HashMap<String, HashMap<String, Integer>> scoreMap = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String[] splitLine; // 分割した入力文字列
        String line; // 入力文字列

        // 問題番号の一致を確認した上でリストに点数を格納する処理
        while((line = in.readLine()) != null){
            splitLine = line.split(",", -1);
            this.storeScoreInMap(scoreMap, splitLine);
        }

        in.close();
        return scoreMap;
    }

    // マップを変更するメソッド
    void storeScoreInMap(HashMap<String, HashMap<String, Integer>> scoreMap, String[] splitLine){
        // すでに課題番号が登録されている時
        if(scoreMap.get(splitLine[2]) != null){
            if(scoreMap.get(splitLine[2]).get(splitLine[4]) != null){
                scoreMap.get(splitLine[2]).replace(splitLine[4], scoreMap.get(splitLine[2]).get(splitLine[4]) + 1);
            }else{
                scoreMap.get(splitLine[2]).put(splitLine[4], 1);
            }
        }
        // まだ課題番号が登録されていない時
        else{
            HashMap<String, Integer> tmp = new HashMap<>();
            tmp.put(splitLine[4], 1);
            scoreMap.put(splitLine[2], tmp);
        }
    }

    // 出力全体を管理するメソッド
    void manageScoreOutput(HashMap<String, HashMap<String, Integer>> scoreMap){
        ArrayList<String> scores = this.scoreCollect(scoreMap);

        // スコアを文字列の順に並べる
        Collections.sort(scores);

        // 1行目の出力
        this.outputFirstLine(scores);
        this.outputAfterSecondLine(scoreMap, scores);
    }

    // 合計人数の集計を行うメソッド
    Integer countTheTotalNumberOfPeople(HashMap<String, Integer> scoreMap){
        Integer totalPeopleNum = 0;

        for(Integer peopleNum : scoreMap.values()) {
            totalPeopleNum += peopleNum;
        }
        return totalPeopleNum;
    }

    // 1行目の出力
    void outputFirstLine(ArrayList<String> scores){
        for(String score: scores){
            System.out.print(",");
            System.out.print(score);
        }
        System.out.println();
    }

    // 2行目以降の出力
    void outputAfterSecondLine(HashMap<String, HashMap<String, Integer>> scoreMap, ArrayList<String> scores){
        // 2行目以降の出力
        for(Map.Entry<String, HashMap<String, Integer>> entry: scoreMap.entrySet()){
            String key = entry.getKey();
            HashMap<String, Integer> value = entry.getValue();

            // 合計人数の取得(問題ごとに人数が異なるためこの処理はここで行う)
            Integer totalPeopleNum = this.countTheTotalNumberOfPeople(value);

            // 出力
            System.out.print(key + ",");
            this.outputOfScoreforEachProblem(value, scores, totalPeopleNum);
        }
    }

    // 存在する点数をリスト化する
    ArrayList<String> scoreCollect(HashMap<String, HashMap<String, Integer>> scoreMap){
        ArrayList<String> scores = new ArrayList<>();

        // 点数がどのように定められているかを集計
        for(Map.Entry<String, HashMap<String, Integer>> entry: scoreMap.entrySet()){
            HashMap<String, Integer> value = entry.getValue();
            for(String k: value.keySet()){
                this.appendScore(scores, k);
            }
        }
        return scores;
    }

    // 存在する場合点数を追加する処理
    void appendScore(ArrayList<String> scores, String k){
        if(!scores.contains(k)){
            scores.add(k);
        }
    }

    // 2行目以降の出力
    void outputOfScoreforEachProblem(HashMap<String, Integer> scorevalue, ArrayList<String> scores, Integer totalPeopleNum){
        for(String score: scores){
            if(scorevalue.get(score) != null){
                System.out.printf("%.3f", (double)scorevalue.get(score) * 100.0 / totalPeopleNum);
            }
            if(!scores.get(scores.size() - 1).equals(score)){
                System.out.print(",");
            }else{
                System.out.println();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer2 app = new ScoreAnalyzer2();
        app.run(args);
    }
}
