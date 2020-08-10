public class Stats{
    Integer count = 0;
    Integer max = Integer.MIN_VALUE;
    Integer min = Integer.MAX_VALUE;
    Integer sum = 0;
    Double avr = 0.0;

    void put(Integer num){
        if(num >= 0){
            if(num > this.max) this.max = num;
            if(num < this.min) this.min = num;
            sum += num;
            count++;
            avr = (double)sum / count;
        }
    }
}
