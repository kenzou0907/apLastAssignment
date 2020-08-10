/**
 * 課題名　最終課題 ステップ2
 * 学生証番号　953580
 * 氏名　近藤 英雅
 * 提出日　2020年8月10日
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScoreAnalyzer2{
    void run(String[] args) throws IOException{
        // 一応事前処理を作成しておく
        if(args.length < 1) return;
        File inputfile = new File(args[0]);
        if(!inputfile.exists()) return;

        // 読み込んだファイルの得点状況を辞書にして出力する
        this.manageScoreOutput(this.readFile(inputfile));
    }

    // 与えられたファイルを読み込むメソッド
    HashMap<String, HashMap<String, Integer>> readFile(File inputfile) throws IOException{
        HashMap<String, HashMap<String, Integer>> scoreMap = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(inputfile));
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
    void storeScoreInMap(HashMap<String, HashMap<String, Integer>> ret, String[] splitLine){
        // すでに課題番号が登録されている時
        if(ret.get(splitLine[2]) != null){
            if(ret.get(splitLine[2]).get(splitLine[4]) != null){
                ret.get(splitLine[2]).replace(splitLine[4], ret.get(splitLine[2]).get(splitLine[4]) + 1);
            }else{
                ret.get(splitLine[2]).put(splitLine[4], 1);
            }
        }
        // まだ課題番号が登録されていない時
        else{
            HashMap<String, Integer> tmp = new HashMap<>();
            tmp.put(splitLine[4], 1);
            ret.put(splitLine[2], tmp);
        }
    }

    // ターミナル出力と同時に出力を行うメソッド
    void printAndWrite(String line, PrintWriter out) throws IOException{
        System.out.print(line);
        out.print(line);
    }

    // ターミナル出力と同時に出力を行うメソッド
    void printlnAndWrite(PrintWriter out) throws IOException{
        System.out.println();
        out.println();
    }

    // 出力全体を管理するメソッド
    void manageScoreOutput(HashMap<String, HashMap<String, Integer>> scoreMap) throws IOException{
        ArrayList<String> scores = this.scoreCollect(scoreMap);
        PrintWriter out = new PrintWriter(new File("ScoreAnalyzer2.csv"));

        // スコアを順に並べる
        Collections.sort(scores);

        // 1行目の出力
        this.outputFirstLine(scores, out);
        this.outputAfterSecondLine(scoreMap, scores, out);

        out.close();
    }

    // 合計人数の集計を行うメソッド
    Integer countTheTotalNumberOfPeople(HashMap<String, Integer> scoreMap){
        Integer totalPeopleNum = 0;

        for(Integer value : scoreMap.values()) {
            totalPeopleNum += value;
        }
        return totalPeopleNum;
    }

    // 1行目の出力
    void outputFirstLine(ArrayList<String> scores, PrintWriter out) throws IOException {
        for(String score: scores){
            this.printAndWrite(",", out);
            this.printAndWrite(score, out);
        }
        this.printlnAndWrite(out);
    }

    // 2行目以降の出力
    void outputAfterSecondLine(HashMap<String, HashMap<String, Integer>> scoreMap, ArrayList<String> scores, PrintWriter out) throws IOException{
        // 2行目以降の出力
        for(Map.Entry<String, HashMap<String, Integer>> entry: scoreMap.entrySet()){
            String key = entry.getKey();
            HashMap<String, Integer> value = entry.getValue();

            // 合計人数の取得(問題ごとに人数が異なるためこの処理はここで行う)
            Integer totalPeopleNum = this.countTheTotalNumberOfPeople(value);

            // 出力
            this.printAndWrite(key + ",", out);
            this.outputOfScoreforEachProblem(value, scores, totalPeopleNum, out);
        }
    }

    // 存在する点数をリスト化する
    ArrayList<String> scoreCollect(HashMap<String, HashMap<String, Integer>> scoremap){
        ArrayList<String> scores = new ArrayList<>();

        // 点数がどのように定められているかを集計
        for(Map.Entry<String, HashMap<String, Integer>> entry: scoremap.entrySet()){
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
    void outputOfScoreforEachProblem(HashMap<String, Integer> scorevalue, ArrayList<String> scores, Integer totalPeopleNum, PrintWriter out) throws IOException{
        for(String score: scores){
            if(scorevalue.get(score) != null){
                System.out.printf("%6.3f", (double)scorevalue.get(score) * 100.0 / totalPeopleNum);
                out.printf("%6.3f", (double)scorevalue.get(score) * 100.0 / totalPeopleNum);
            }
            if(!scores.get(scores.size() - 1).equals(score)){
                this.printAndWrite(",", out);
            }else{
                this.printlnAndWrite(out);
            }
        }
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer2 app = new ScoreAnalyzer2();
        app.run(args);
    }
}
