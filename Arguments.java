import java.util.Objects;
import java.util.ArrayList;

public class Arguments{
    private ArrayList<String> arguments = new ArrayList<>();
    private String dest = null;
    private String sort = null;
    private String heatmap = null;
    private Boolean help = false;

    // 引数の整理を行うメソッド
    public void parse(String[] args){
        for(Integer i = 0; i < args.length; i++){
            if(!args[i].startsWith("--")){
                this.arguments.add(args[i]);
            }
            else{
                i = this.parseOption(args, i);
            }
        }
    }

    // オプションの解析を行うメソッド
    private Integer parseOption(String[] args, Integer i){
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

    public Boolean getHelp(){
        return this.help;
    }

    public String getDest(){
        return this.dest;
    }

    public String getSort(){
        return this.sort;
    }

    public String getHeatmap(){
        return this.heatmap;
    }

    public ArrayList<String> getArgument(){
        return this.arguments;
    }
}
