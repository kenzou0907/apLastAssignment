import java.io.BufferedReader;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;

public class ScoreAnalyzer4{
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

    // 出力を行うメソッド
    void manageScoreOutput(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList) throws IOException{
        Collections.sort(problemNumList);
        HashMap<Integer, Stats> totalStats = this.makeTotalStats(problemNumList);

        for(Map.Entry<String, StudentScore> entry: scoreMap.entrySet()){
            // 出力
            this.outputStatsForEachStudent(entry.getKey(), entry.getValue(), problemNumList, totalStats);
        }

        // 全体の点数の出力
        this.outputTheTotalScore(totalStats, problemNumList);

        // ヒートマップの作成
        this.makeHeatMap(scoreMap, problemNumList, this.makeStringIdList(scoreMap), totalStats);
    }

    // 出力をまとめたメソッド
    void outputStatsForEachStudent(String id, StudentScore studentData, ArrayList<Integer> problemNumList, HashMap<Integer, Stats> totalStats){
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

    // ヒートマップを実際に表示する
    void makeHeatMap(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList, ArrayList<String> idList, HashMap<Integer, Stats> totalStats) throws IOException{
        EZ.initialize(idList.size() * 3, problemNumList.size() * 3);
        Integer width = 0, height = 0;

        // ヒートマップへの表示
        for(String id: idList){
            height = 0;
            for(Integer problemNum: problemNumList){
                this.drawHeatMap(scoreMap.get(id).studentScore.get(Integer.toString(problemNum)), totalStats.get(problemNum).max, width, height);
                height += 3;
            }
            width += 3;
        }
        this.outputImage(width, height);
    }

    // 描画する
    void drawHeatMap(Integer score, Integer maxScore, Integer width, Integer height) throws IOException{
        EZRectangle sq = EZ.addRectangle(width + 1, height + 1, 3, 3, this.calculatePixelColor(score, maxScore), true);
    }

    // 色の決定
    Color calculatePixelColor(Integer score, Integer maxScore){
        if(score == null || score == -1){
            return new Color(0xff, 0xff, 0xff, 0xff); // 白の透明色
        }

        Double color = Double.valueOf(255.0 * score / maxScore);
        return new Color(0, color.intValue(), color.intValue()); // 赤の場合
    }


    // IDを文字列で列挙する
    ArrayList<String> makeStringIdList(HashMap<String, StudentScore> scoreMap){
        ArrayList<String> idList = new ArrayList<>();
        for(String id: scoreMap.keySet()){
            idList.add(id);
        }
        return idList;
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

    // pngファイルで出力する
    void outputImage(Integer width, Integer height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        EZ.app.paintComponent(g); // image に図形を描画する．
        // image を "png" フォーマットで "heatmap.png" に出力する．
        ImageIO.write(image, "png", new File("heatmap.png"));
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer4 app = new ScoreAnalyzer4();
        app.run(args);
    }
}
