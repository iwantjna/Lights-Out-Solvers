package behaviortracing.quicknet;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author MijitR
 */
public class FlowMatrix {
    
    private static final Random RAND;
    
    static {
        RAND = new Random();
    }
    
    //features, batch size
    private final float[][] values;
    
    //protected FlowMatrix(final int numFeatures, final int batchSize) {
    //    this.values = new float[numFeatures + 1][batchSize];
    //}
    
    public FlowMatrix(final float[][] values) {
        /*this.values = new float[values.length][];
        for(int r = 0; r < values.length; r ++) {
            this.values[r] = new float[values[r].length];
            System.arraycopy(values[r],0,this.values[r],0,values[r].length);
            //this.values[r][values[r].length] = 1f
        }
        //modify with biases
        //for(int r = values.length; r < values.length + QuickNet.BIAS_SPACE_ALLOCATION; r ++) {
        //    this.values[r] = new float[maxLen];
        //    Arrays.fill(this.values[r], 1f);
        //}*/
        this.values = new float[values.length][];
        for(int i = 0; i < values.length; i ++) {
            System.arraycopy(values, 0, this.values, 0, values.length);
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder builder
                = new StringBuilder("");
        Arrays.stream(values).forEach(n ->
            builder.append(Arrays.toString(n)).append("\n")
        );
        return builder.toString();
    }
    
}
