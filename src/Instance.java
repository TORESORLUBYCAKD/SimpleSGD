

public class Instance {
    public int label;
    public double[] x;

    public Instance(int label, double[] x) {
        this.label = label;
        this.x = x;
    }

    public int getLabel() {
        return label;
    }

    public double[] getX() {
        return x;
    }
}
