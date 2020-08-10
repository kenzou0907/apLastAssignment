import java.io.BufferedReader;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.util.Objects;

public class ScoreAnalyzer5{
    void run(String[] args) throws IOException{
        // 一応事前処理を作成しておく
        if(args.length < 1) {
            System.out.println("最低引数を１つ入力してください");
            return;
        }

        // 与えられた引数を処理する
        Arguments arguments = new Arguments();
        arguments.parse(args);

        // ヘルプメッセージの有無
        if(!arguments.help){
            this.ScoreAnalyzer(arguments);
        }else{
            this.printHelpMessage();
        }
    }

    // ヘルプメッセージを出力しない際の処理
    void ScoreAnalyzer(Arguments args) throws IOException{
        for(String arg: args.arguments){
            // 引数に受け取ったファイルが存在するかどうかを確認する
            File inputFile = new File(arg);
            if(!inputFile.exists()){
                System.out.println(arg + " は存在しません");
                continue;
            }

            // 読み込んだファイルの得点状況を辞書にする
            HashMap<String, StudentScore5> scoreMap = this.readFile(inputFile);

            // 書き出す
            this.manageScoreOutput(scoreMap, this.makeProblemNumberList(scoreMap), args);
        }
    }

    // ヘルプメッセージを出力するメソッド
    void printHelpMessage(){
        System.out.println("java ScoreAnalyzer5 [OPTIONS] <FILENAME.CSV>");
        System.out.println("OPTIONS");
        System.out.println("    --help           このメッセージを表示して終了する．");
        System.out.println("    --dest <DEST>    ヒートマップの出力先を指定する．");
        System.out.println("    --sort <ITEM>    指定された項目の昇順でソートする．");
        System.out.println("    --heatmap <TYPE> ヒートマップの種類を指定する．scoreもしくはtime．");
    }

    // 与えられたファイルを読み込むメソッド
    HashMap<String, StudentScore5> readFile(File inputFile) throws IOException{
        HashMap<String, StudentScore5> scoreMap = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String[] splitLine; // csvファイルの1行を分割して受け取る
        String line; // csvファイルの1行を受け取る

        // 問題番号の一致を確認した上で辞書に点数を格納する処理
        while((line = in.readLine()) != null){
            splitLine = line.split(",", -1);
            this.storeScoreInMap(scoreMap, splitLine); // マップを更新する
        }

        in.close();
        return scoreMap;
    }

    // 問題番号が何かを作成する
    ArrayList<Integer> makeProblemNumberList(HashMap<String, StudentScore5> scoreMap){
        ArrayList<Integer> problemNumberList = new ArrayList<>();
        for(Map.Entry<String, StudentScore5> entry: scoreMap.entrySet()){
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
    void storeScoreInMap(HashMap<String, StudentScore5> scoreMap, String[] splitLine){
        // 学生IDがすでに存在する時
        if(scoreMap.get(splitLine[3]) != null){
            this.studentScoreAppend(scoreMap.get(splitLine[3]), splitLine[2], splitLine[4]);
            this.studentTimeAppend(scoreMap.get(splitLine[3]), splitLine[2], splitLine[5], splitLine[6]);
        }

        // 学生IDが登録されていない時
        else{
            StudentScore5 tmp = new StudentScore5();
            this.studentScoreAppend(tmp, splitLine[2], splitLine[4]);
            this.studentTimeAppend(tmp, splitLine[2], splitLine[5], splitLine[6]);
            tmp.putId(splitLine[3]);
            scoreMap.put(splitLine[3], tmp);
        }
    }

    // 生徒の得点を追加するメソッド
    void studentScoreAppend(StudentScore5 studentScore, String problemNum, String score){
        if(!score.equals("")){
            studentScore.putScore(problemNum, Integer.valueOf(score));
        }else{
            studentScore.putScore(problemNum, -1);
        }
    }

    // 時間を追加するメソッド
    void studentTimeAppend(StudentScore5 studentScore, String problemNum, String startTime, String endTime){
        studentScore.putTime(problemNum, startTime, endTime);
    }

    // ターミナル出力と同時に出力を行うメソッド
    void printAndWrite(String line, PrintWriter out) throws IOException{
        System.out.print(line);
        out.print(line);
    }

    // 平均値を出力するメソッド
    void avrPrintfAndWrite(Double line, PrintWriter out) throws IOException{
        System.out.printf("%7.6f", line);
        out.printf("%7.6f", line);
    }

    // ターミナル出力と同時に出力を行うメソッド
    void printlnAndWrite(PrintWriter out) throws IOException{
        System.out.println();
        out.println();
    }

    // 出力を行うメソッド
    void manageScoreOutput(HashMap<String, StudentScore5> scoreMap, ArrayList<Integer> problemNumList, Arguments args) throws IOException{
        PrintWriter out = new PrintWriter(new File("ScoreAnalyzer5.csv"));
        ArrayList<StudentScore5> sortedList = this.sortedStudentList(scoreMap, args.sort);
        HashMap<Integer, Stats> totalStats = this.makeTotalStats(problemNumList);

        for(StudentScore5 studentScore: sortedList){
            // 出力
            this.outputStatsForEachStudent(studentScore, out, problemNumList, totalStats, args);
        }

        // 全体の点数の出力
        this.outputTheTotalScore(totalStats, problemNumList, out);
        out.close();

        // ヒートマップの作成
        this.makeHeatMap(sortedList, problemNumList, totalStats, args);
    }

    // 出力をまとめたメソッド
    void outputStatsForEachStudent(StudentScore5 studentData, PrintWriter out, ArrayList<Integer> problemNumList, HashMap<Integer, Stats> totalStats, Arguments args) throws IOException {
        // idと点数の出力
        this.printAndWrite(studentData.id + ",", out);
        for(Integer problemNum: problemNumList){
            this.studentScoreWrite(problemNum, studentData, out);
            this.studentTimeWrite(problemNum, studentData, out);
            // 全体の点数に反映
            this.removeAllStats(problemNum, totalStats, studentData);
        }

        // 最大値の出力
        this.printAndWrite(Integer.toString(studentData.getMax()) + ",", out);

        // 最小値の出力
        this.printAndWrite(Integer.toString(studentData.getMin()) + ",", out);

        // 平均値の出力
        this.avrPrintfAndWrite(studentData.getAvr(), out);
        this.printlnAndWrite(out);
    }

    // ソートされた生徒のリストの表示
    ArrayList<StudentScore5> sortedStudentList(HashMap<String, StudentScore5> map, String sortKey){
        ArrayList<StudentScore5> list = new ArrayList<>(map.values());
        if(Objects.equals(sortKey, "id")){
            Collections.sort(list, new StudentIdComparator());
        }
        else if(Objects.equals(sortKey, "score")){
            Collections.sort(list, new StudentScoreComparator());
        }
        else if(Objects.equals(sortKey, "time")){
            Collections.sort(list, new StudentTakenTimeComparator());
        }
        return list;
    }

    // ヒートマップを実際に表示する
    void makeHeatMap(ArrayList<StudentScore5> students, ArrayList<Integer> problemNum, HashMap<Integer, Stats> all, Arguments args) throws IOException{
        EZ.initialize(students.size() * 3, problemNum.size() * 3);
        Integer width = 0, height = 0;

        // ヒートマップへの表示
        for(StudentScore5 student: students){
            height = 0;
            for(Integer pnum: problemNum){
                this.drawHeatMap(student.studentScore.get(pnum.toString()), all.get(pnum).max, width, height);
                height += 3;
            }
            width += 3;
        }
        this.outputImage(width, height, args);
    }

    // 描画する
    void drawHeatMap(Integer score, Integer maxScore, Integer width, Integer height) throws IOException{
        EZRectangle sq = EZ.addRectangle(width, height, 3, 3, this.calculatePixelColor(score, maxScore), true);
    }

    // 色の決定
    Color calculatePixelColor(Integer score, Integer maxScore){
        if(score == null || score == -1){
            return new Color(0xff, 0xff, 0xff, 0xff); // 白の透明色
        }

        Double color = Double.valueOf(255.0 * score / maxScore);
        return new Color(0, color.intValue(), color.intValue()); // 赤の場合
    }

    // IDをStringで列挙する処理
    ArrayList<String> makeStringIdList(HashMap<String, StudentScore5> scoreMap){
        ArrayList<String> ret = new ArrayList<>();
        for(String id: scoreMap.keySet()){
            ret.add(id);
        }
        return ret;
    }

    // 全体の平均とかに追加するメソッド
    void removeAllStats(Integer pnum, HashMap<Integer, Stats> all, StudentScore5 value){
        if(value.studentScore.get(Integer.toString(pnum)) != null){
            all.get(pnum).put(value.studentScore.get(Integer.toString(pnum)));
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
    void studentScoreWrite(Integer pnum, StudentScore5 value, PrintWriter out) throws IOException{
        if(value.studentScore.get(Integer.toString(pnum)) == null || value.studentScore.get(Integer.toString(pnum)) == -1){
            this.printAndWrite(",", out);
        }else{
            this.printAndWrite(Integer.toString(value.studentScore.get(Integer.toString(pnum))) + ",", out);
        }
    }

    // 生徒がかけた時間を出力
    void studentTimeWrite(Integer pnum, StudentScore5 value, PrintWriter out) throws IOException{
        if(value.studentTime.get(Integer.toString(pnum)) == null || value.studentTime.get(Integer.toString(pnum)) == -1){
            this.printAndWrite(",", out);
        }else{
            this.printAndWrite(Integer.toString(value.studentTime.get(Integer.toString(pnum))) + ",", out);
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
    // pngファイルで出力する
    void outputImage(Integer width, Integer height, Arguments args) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphic = image.createGraphics();
        EZ.app.paintComponent(graphic); // image に図形を描画する．

        // heatmapオプションの有無で名前を決定する
        File heatMap = new File("heatmap.png");
        if(args.dest != null) heatMap = new File(this.updateExtension(args.dest,".png"));

        ImageIO.write(image, "png", heatMap);
    }

    // 拡張子を調節するメソッド
    String updateExtension(String fileName, String png){
        if(fileName.endsWith(png)){
            return fileName;
        }
        return fileName + png;
    }

    public static void main(String[] args) throws IOException{
        ScoreAnalyzer5 app = new ScoreAnalyzer5();
        app.run(args);
    }
}
