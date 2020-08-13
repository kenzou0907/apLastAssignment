import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.ImageIO;

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
        HashMap<String, StudentScore> scoreMap = this.readFileScoreAnalyzer3(inputFile);
        ArrayList<Integer> problemNumList = this.makeProblemNumberList(scoreMap);
        HashMap<Integer, Stats> totalStats = this.makeTotalStats(problemNumList);

        // 書き出す
        this.manageScoreOutput(scoreMap, this.makeProblemNumberList(scoreMap), totalStats);
        this.makeHeatMap(scoreMap, problemNumList, this.makeStringIdList(scoreMap), totalStats);
    }

    // 与えられたファイルを読み込むメソッド
    public HashMap<String, StudentScore> readFileScoreAnalyzer3(File inputFile) throws IOException{
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

    // マップを変更するメソッド
    private void storeScoreInMap(HashMap<String, StudentScore> scoreMap, String[] splitLine){
        // 学生番号がすでにマップに存在している時
        if(scoreMap.get(splitLine[3]) != null){
            if(!splitLine[4].equals("")){
                scoreMap.get(splitLine[3]).set(splitLine[2], Integer.valueOf(splitLine[4]));
            }else{
                scoreMap.get(splitLine[3]).set(splitLine[2], -1);
            }
        }
        // 学生IDがマップにない時
        else{
            StudentScore tmp = new StudentScore();
            if(!splitLine[4].equals("")){
                tmp.set(splitLine[2], Integer.valueOf(splitLine[4]));
            }else{
                tmp.set(splitLine[2], -1);
            }
            scoreMap.put(splitLine[3], tmp);
        }
    }

    // 問題番号が何かを作成する
    ArrayList<Integer> makeProblemNumberList(HashMap<String, StudentScore> scoreMap){
        ArrayList<Integer> problemNumberList = new ArrayList<>();
        for(Map.Entry<String, StudentScore> entry: scoreMap.entrySet()){
            for(String problemNum: entry.getValue().getStudentScoreKeySet()){
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

    // ヒートマップを実際に表示する
    void makeHeatMap(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList, ArrayList<String> idList, HashMap<Integer, Stats> totalStats) throws IOException{
        EZ.initialize(idList.size() * 3, problemNumList.size() * 3);
        Integer width = 0, height = 0;

        // ヒートマップへの表示
        for(String id: idList){
            height = 0;
            for(Integer problemNum: problemNumList){
                this.drawHeatMap(scoreMap.get(id).getStudentScore(Integer.toString(problemNum)), totalStats.get(problemNum).getMax(), width, height);
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

    // IDを文字列で列挙する
    ArrayList<String> makeStringIdList(HashMap<String, StudentScore> scoreMap){
        ArrayList<String> idList = new ArrayList<>();
        for(String id: scoreMap.keySet()){
            idList.add(id);
        }
        return idList;
    }

    // 色の決定
    Color calculatePixelColor(Integer score, Integer maxScore){
        if(score == null || score == -1){
            return new Color(0xff, 0xff, 0xff, 0xff); // 白の透明色
        }

        Double color = Double.valueOf(255.0 * score / maxScore);
        return new Color(0, color.intValue(), color.intValue()); // 赤の場合
    }

    // 出力を管理するメソッド
    void manageScoreOutput(HashMap<String, StudentScore> scoreMap, ArrayList<Integer> problemNumList, HashMap<Integer, Stats> totalStats){
        Collections.sort(problemNumList);

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
            if(studentData.getCount() != 0) System.out.print(Integer.toString(studentData.getMax()));
            System.out.print(",");

            // 最小値の出力
            if(studentData.getCount() != 0) System.out.print(Integer.toString(studentData.getMin()));
            System.out.print(",");

            // 平均値の出力
            if(studentData.getCount() != 0) System.out.printf("%7.6f", studentData.getAvr());
            System.out.println();
        }
    }

    // 全体の平均とかに追加するメソッド
    void replaceTotalStats(Integer problemNum, HashMap<Integer, Stats> totalStats, StudentScore studentData){
        if(studentData.getStudentScore(Integer.toString(problemNum)) != null){
            totalStats.get(problemNum).setStats(studentData.getStudentScore(Integer.toString(problemNum)));
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
        if(studentData.getStudentScore(Integer.toString(problemNum)) == null || studentData.getStudentScore(Integer.toString(problemNum)) == -1){
            System.out.print(",");
        }else{
            System.out.print(Integer.toString(studentData.getStudentScore(Integer.toString(problemNum))) + ",");
        }
    }

    // 全体の値の出力
    void outputTheTotalScore(HashMap<Integer, Stats> totalStats, ArrayList<Integer> problemNumList){
        // 最大値の出力
        for(Integer problemNum: problemNumList){
            if(totalStats.get(problemNum).getMax() != Integer.MIN_VALUE)
                System.out.print("," + Integer.toString(totalStats.get(problemNum).getMax()));
        }
        System.out.println();

        // 最小値の出力
        for(Integer problemNum: problemNumList){
            if(totalStats.get(problemNum).getMin() != Integer.MAX_VALUE)
                System.out.print("," + Integer.toString(totalStats.get(problemNum).getMin()));
        }
        System.out.println();

        // 平均値の出力
        for(Integer problemNum: problemNumList){
            if(totalStats.get(problemNum).getAvr() != -1)
                System.out.printf(",%.6f", totalStats.get(problemNum).getAvr());
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
