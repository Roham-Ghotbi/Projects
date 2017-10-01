import lab14lib.Generator;
import java.util.ArrayList;
import lab14lib.GeneratorAudioVisualizer;
import lab14lib.MultiGenerator;

public class StrangeBitwiseGenerator implements Generator {
    private int period;
    private int state;
    private int weirdState;

    public StrangeBitwiseGenerator(int samplingPeriod) {
        this.period = samplingPeriod;
        this.state = 0;
        this.weirdState = 0;

    }

    public double next() {
        state = (state + 1);
        weirdState = state & (state >> 7) % period;
        return normalize();
    }

    public double normalize() {
        double scaledState = -1 + weirdState * 2.0 / (period - 1.0);
        return scaledState;
    }
}
