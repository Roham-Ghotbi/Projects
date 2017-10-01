import lab14lib.Generator;
import java.util.ArrayList;
import lab14lib.GeneratorAudioVisualizer;
import lab14lib.MultiGenerator;

public class SawToothGenerator implements Generator {
    protected int period;
    protected int state;

    public SawToothGenerator(int samplingPeriod) {
        this.period = samplingPeriod;
        this.state = 0;

    }

    public double next() {
        state = (state + 1) % period;
        return normalize();
    }

    public double normalize() {
        double scaledState = -1 + state * 2.0 / (period - 1.0);
        return scaledState;
    }
}
