/**
 * 課題名　最終課題
 * 学生証番号　953580
 * 氏名　近藤 英雅
 */

import java.util.Objects;
import java.util.ArrayList;

public class Arguments{
    ArrayList<String> arguments = new ArrayList<>();
    String dest = null;
    String sort = null;
    String heatmap = null;
    Boolean help = false;

    // 引数の整理を行うメソッド
    void parse(String[] args){
        for(Integer i = 0; i < args.length; i++){
            if(!args[i].startsWith("--")){
                arguments.add(args[i]);
            }
            else{
                i = parseOption(args, i);
            }
        }
    }

    // オプションの解析を行うメソッド
    Integer parseOption(String[] args, Integer i){
        if(Objects.equals(args[i], "--dest")){
            i++;
            this.dest = args[i];
        }else if(Objects.equals(args[i], "--help")){
            this.help = true;
        }else if(Objects.equals(args[i], "--sort")){
            i++;
            this.sort = args[i];
        }else if(Objects.equals(args[i], "--heatmap")){
            i++;
            this.heatmap = args[i];
        }
        return i;
    }
}
