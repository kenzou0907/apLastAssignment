/**
 * 課題名　最終課題 ステップ1
 * 学生証番号　953580
 * 氏名　近藤 英雅
 * 提出日　2020年8月10日
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScoreAnalyzer1{
    void run(String[] args) throws IOException{
        // 事前処理
        if(args.length != 2) return;
        Integer problemNum = Integer.valueOf(args[0]); // 問題番号
        File inputFile = new File(args[1]); // 入力ファイル
        if(!inputFile.exists()) return;

        // 読み込んだファイルの得点状況を辞書にする
        HashMap<String, Integer> scoreMap = this.readFile(inputFile, problemNum);

        // 合計人数の集計を行うメソッド
        Integer totalPeopleNum = this.countTheTotalNumberOfPeople(scoreMap);

        // 点数の出力
        for(Map.Entry<String, Integer> entry: scoreMap.entrySet()){
            printScore(entry.getKey(), entry.getValue(), totalPeopleNum);
        }
    }

    // 合計人数の集計を行うメソッド
    Integer countTheTotalNumberOfPeople(HashMap<String, Integer> scoreMap){
        Integer totalPeopleNum = 0;

        for(Integer value : scoreMap.values()) {
            totalPeopleNum += value;
        }
        return totalPeopleNum;
    }

    // 与えられたファイルを読み込み、ファイルを返すメソッド
    HashMap<String, Integer> readFile(File inputfile, Integer problemNum) throws IOException{
        HashMap<String, Integer> scoreMap = new HashMap<>(); // 点数、人数の辞書
        BufferedReader in = new BufferedReader(new FileReader(inputfile));
        String[] splitLine; // 分割した入力文字列
        String line; // 入力文字列

        // 問題番号の一致を確認した上でリストに点数を格納する処理
        while((line = in.readLine()) != null){
            splitLine = line.split(",", -1);
            if(this.isGivenProbelmNumber(splitLine, problemNum)){
                this.storeScoreInMap(scoreMap, splitLine);
            }
        }

        in.close();
        return scoreMap;
    }

    // マップに点数に対する人数を格納する処理
    void storeScoreInMap(HashMap<String, Integer> ret, String[] splitLine){
        // 与えられた点数をとっているかどうか
        if(ret.get(splitLine[4]) != null){
            ret.replace(splitLine[4], ret.get(splitLine[4]) + 1);
        }
        else{
            ret.put(splitLine[4], 1);
        }
    }

    // 出力を行うメソッド
    void printScore(String score, Integer sum, Integer totalPeopleNum){
        System.out.printf("%2s: %6.3f (%2d/%d)%n", score, (double)sum * 100.0 / totalPeopleNum, sum, totalPeopleNum);
    }

    // 受け取った文字列を分割し,問題番号の一致を確認する
    Boolean isGivenProbelmNumber(String[] splitline, Integer problemNum){
        if(Integer.valueOf(splitline[2]) == problemNum) return true;
        return false;
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer1 app = new ScoreAnalyzer1();
        app.run(args);
    }
}