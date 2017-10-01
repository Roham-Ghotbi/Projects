
public class AcceleratingSawToothGenerator extends SawToothGenerator{
    private double factor;

    public AcceleratingSawToothGenerator(int samplingPeriod, double factor) {
        super(samplingPeriod);
        this.factor = factor;
    }

    public double next() {
        state = (state + 1) % period;
        if (state == 0) {
            if (period * factor < 2) {
                period = 2;
            } else {
                period *= factor;
            }

        }
        return normalize();
    }
}
