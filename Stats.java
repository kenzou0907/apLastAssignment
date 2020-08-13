public class Stats{
    private Integer count = 0;
    private Integer max = Integer.MIN_VALUE;
    private Integer min = Integer.MAX_VALUE;
    private Integer sum = 0;
    private Double avr = 0.0;

    public void setStats(Integer num){
        if(num >= 0){
            if(num > this.max) this.max = num;
            if(num < this.min) this.min = num;
            this.sum += num;
            this.count++;
            this.avr = (double)this.sum / this.count;
        }
    }

    public Double getAvr(){
        if(this.count !=  0) return this.avr;
        return - 1.0;
    }

    public Integer getMax(){
        return this.max;
    }

    public Integer getMin(){
        return this.min;
    }

    public Integer getCount(){
        return this.count;
    }
}
