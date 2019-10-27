public class MutableDouble implements Comparable<MutableDouble> {

    private double value;

    public MutableDouble(double d) {
        value = d;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void add(double d) {
        value += d;
    }

    public void subtract(double d) {
        value -= d;
    }

    public void multiply(double d) {
        value *= d;
    }

    public void divide(double d) {
        value /= d;
    }

    public int compareTo(MutableDouble d) {
        if(value < d.getValue()) return -1;
        if(value > d.getValue()) return 1;
        return 0;
    }
}
