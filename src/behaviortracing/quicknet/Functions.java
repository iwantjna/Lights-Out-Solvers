package behaviortracing.quicknet;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author MijitR
 */
public abstract class Functions {
    
    private static final Random RAND;
    
    static {
        RAND = new Random();
    }
    
    public static final float sum(final float a, final float b) {
        return Float.sum(a,b);
    }

    //failfast
    public static final float dot(final float[] a, final float[] b) {
        final int maxDex = Math.max(a.length, b.length);
        float sum = 0f;
        for(int i = 0; i < maxDex; i ++) {
            sum += a[i]*b[i];
        }
        return sum;
    }
    
    public static final float[][] transpose(final float[][] t) {
        final float[][] result = new float[t[0].length][t.length];
        
        for(int r = 0; r < t.length; r ++) {
            for(int c = 0; c < t[r].length; c ++) {
                result[c][r] = t[r][c];
            }
        }
        
        return result;
    }
    
    public static final float[][] appendBias(final float[][] values) {
        final float[][] biasedValues = new float[values.length + QuickNet.getBIAS_SPACE_ALLOCATION()][];
        for(int i = 0; i < values.length; i ++) {
            System.arraycopy(values, 0, biasedValues, 0, values.length);
        }
        for(int r = values.length; r < values.length + QuickNet.getBIAS_SPACE_ALLOCATION(); r ++) {
            biasedValues[r] = new float[QuickNet.getBatchSize()];
            Arrays.fill(biasedValues[r], 1f);
        }
        
        return biasedValues;
    }
    
    public static final void activate(final float[][] thruput, final float[][] log, final String act)
    {
        for(int y = 0; y < thruput.length; y ++) {
            for(int x = 0; x < thruput[y].length; x ++) {
                /*if(isOutput) {
                    thruput[y][x] = 1f/(1f + (float)Math.exp(-thruput[y][x]));
                    log[y][x] = thruput[y][x] * (1f - thruput[y][x]);
                }
                //log[y][x] = 1f;
                else {*/
                    switch(act) {
                        default:
                        case "TANH":
                            thruput[y][x] = (float)Math.tanh(thruput[y][x]);
                            log[y][x] = 1f - thruput[y][x]*thruput[y][x];
                            break;
                        case "SIGMOID":
                            thruput[y][x] = 1f/(1f + (float)Math.exp(-thruput[y][x]));
                            log[y][x] = thruput[y][x] * (1f - thruput[y][x]);
                            break;
                        case "LEAKY_RELU":
                            thruput[y][x] = Math.max(0.03f*thruput[y][x],thruput[y][x]);
                            log[y][x] = thruput[y][x] > 0 ? 1f : 0.03f;
                            break;
                        case "VERY_LEAKY_RELU":
                            thruput[y][x] = Math.max(0.618f*thruput[y][x],thruput[y][x]);
                            log[y][x] = thruput[y][x] > 0 ? 1f : 0.618f;
                            break;
                        ////thruput[y][x] = Math.max(0.03f*thruput[y][x],thruput[y][x]);
                        //log[y][x] = thruput[y][x] > 0 ? 1f : 0.03f;
                        case "SQUARE":
                            log[y][x] = 2*Math.abs(thruput[y][x]);
                            thruput[y][x] *= thruput[y][x];
                            break;
                        case "SINUSOID":
                            log[y][x] = (float)Math.sin(thruput[y][x]);
                            thruput[y][x] = (float)Math.cos(thruput[y][x]);
                            break;
                        case "RELU":
                            thruput[y][x] = Math.max(0f, thruput[y][x]);
                            log[y][x] = thruput[y][x] > 0f ? 1f : 0f;
                            break;
                        case "SOFTPLUS":
                            log[y][x] = 1f/(1f+(float)Math.exp(-thruput[y][x]));
                            thruput[y][x] = (float)Math.log(1+Math.exp(thruput[y][x]));
                            break;//log[y][x] = 1f/(float)Math.sqrt(1+thruput[y][x]*thruput[y][x]);
                        case "DROPPEDSOFT":
                            log[y][x] = 1f/(1f+(float)Math.exp(-thruput[y][x]));
                            thruput[y][x] = (float)Math.log(1+Math.exp(thruput[y][x])) - 1f;
                            break;
                        //thruput[y][x] = (float)Math.log(thruput[y][x] + Math.sqrt(thruput[y][x]*thruput[y][x]+1));
                        //thruput[y][x] = (float) Math.tanh(thruput[y][x]);
                        //log[y][x] = 1 - thruput[y][x]*thruput[y][x];
                        //log[y][x] = 1f/(1f+(float)Math.exp(-thruput[y][x]));
                        //thruput[y][x] = (float) Math.log(1f + Math.exp(thruput[y][x]));
                    //}
                }
            }
        }
    }
    
    public static final void decideFromThreshold(final float[][] results,
            final int[][] dest, final float threshold
    ) {
        for(int r = 0; r < results.length; r ++) {
            for(int c = 0; c < results[r].length; c ++) {
                dest[r][c] = results[r][c] >= threshold ? 1 : 0;
            }
        }
    }
    
    /*public static final int[][] decideFromThreshold(final float[][] results, final float threshold
    ) {
        final int[][] dest = new int[results.length][];
        for(int r = 0; r < results.length; r ++) {
            dest[r] = new int[results[r].length];
            for(int c = 0; c < results[r].length; c ++) {
                dest[r][c] = results[r][c] >= threshold ? 1 : 0;
            }
        }
        return dest;
    }*/
    
    public static final void mask(final float[][] results, final float[][] mask) {
        for(int r = 0; r < results.length; r ++) {
            for(int c = 0; c < mask[r].length; c ++) {
                results[r][c] *= mask[r][c];
            }
        }
    }
    
    public static final void dropout(final float[][] values, final float[][] gradients) {
        for(int r = 0; r < Math.max(values.length, gradients.length); r ++) {
            for(int c = 0; c < Math.max(values[r].length, gradients[r].length); c ++) {
                if(RAND.nextFloat()<QuickNet.getDropoutPercentage()) {
                    values[r][c] = 0;
                    gradients[r][c] = 0;
                } else {
                    final float multiplier = 1f/Math.max(Float.MIN_VALUE,(1f-QuickNet.getDropoutPercentage()));
                    values[r][c] *= multiplier;
                    gradients[r][c] *= multiplier;
                }
            }
        }
    }
    
    public static final void normalize(final float[][] values) {
        final float[] sums = new float[QuickNet.getBatchSize()];
        for(int trait = 0; trait < values.length; trait ++) {
            for(int instance = 0; instance < values[trait].length; instance ++) {
                
            }
        }
    }
    
    public static final void scale(final float[][] values, final float scalar) {
        for(int r = 0; r < values.length; r ++) {
            for(int c = 0; c < values[r].length; c ++) {
                values[r][c] *= scalar;
            }
        }
    }
    
    public static final float sum(final float[] a) {
        float sum = 0f;
        for(final float b : a) {
            sum += b;
        }
        return sum;
    }
    
    public static final int absDist(final int[][] map1, final int[][] map2) {
        int dist = 0;
        for(int r = 0; r < Math.max(map1.length, map2.length); r ++) {
            for(int c = 0; c < Math.max(map1[r].length, map2[r].length); c ++) {
                dist += Math.abs(map1[r][c] - map2[r][c]);
            }
        }
        return dist;
    }
    
    public static final float[][] threshHold(final float[][] values,
            final float threshHold) {
        final float[][] decisions = new float[values.length][];
        for(int r = 0; r < values.length; r ++) {
            decisions[r] = new float[values[r].length];
            for(int c = 0; c < values[r].length; c ++) {
                decisions[r][c] = values[r][c] > threshHold ? values[r][c] : 0f;//Math.round(values[r][c]);
            }
        }
        return decisions;
    }
    
    public static final float[][] piecewiseDistance(final float[][] groupA,
            final int[][] groupB) {
        final float[][] result = new float[groupA.length][];
        for(int r = 0; r < groupB.length; r ++) {
            result[r] = new float[groupA[r].length];
            for(int c = 0; c < groupB[r].length; c ++) {
                result[r][c] = Math.abs(groupA[r][c]-groupB[r][c]);
            }
        }
        return result;
    }
    
}
