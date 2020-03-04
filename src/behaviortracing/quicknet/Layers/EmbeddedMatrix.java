package behaviortracing.quicknet.Layers;

import behaviortracing.quicknet.FlowMatrix;
import behaviortracing.quicknet.Functions;
import behaviortracing.quicknet.QuickNet;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author MijitR
 */
public class EmbeddedMatrix {
    
    private static final Random RAND;
    
    static {
        final long seed = 3l;
        RAND = new Random();//seed);
        
    }

    //object, catagory
    private final float[][] lookupTable, deltas, velocities;
    
    private final int[][] feedIndices;
    
    public EmbeddedMatrix() {
        lookupTable = new float[QuickNet.getNumFeaturesPerObject()][QuickNet.getNumUniqueStatesPerFeature()];
        deltas = new float[QuickNet.getNumFeaturesPerObject()][QuickNet.getNumUniqueStatesPerFeature()];
        velocities = new float[QuickNet.getNumFeaturesPerObject()][QuickNet.getNumUniqueStatesPerFeature()];
        
        feedIndices = new int[QuickNet.getNumFeaturesPerObject()][QuickNet.getBatchSize()];
        
        //this.fill(lookupTable[0]);
        //for(int i = 1; i < lookupTable.length; i ++) {
        //    System.arraycopy(lookupTable[i-1], 0, lookupTable[i], 0, lookupTable[i-1].length);
        //}
        
        Arrays.stream(lookupTable).forEach(
                x -> this.fill(x)
        );
        //System.out.println("LookupTable :\n" + new FlowMatrix(lookupTable));
    }
    
    private void fill(final float[] stateWeights) {
        for(int f = 0; f < stateWeights.length; f ++) {
            stateWeights[f] = f%2==0?0f:0f;//f / 7f;
            //f % 2 == 0 ? 2f*(RAND.nextFloat()) - 1f : -1f*stateWeights[f-1];
        }
    }
    
    //DIMS(tileStates) = traits X batch instance
    public final float[][] embedRowMajor(final int[][] tileStates) {
        //final float[][] netInput = new float[QuickNet.NUM_FEATURES_PER_OBJ+QuickNet.BIAS_SPACE_ALLOCATION][QuickNet.BATCH_SIZE];
        final float[][] netInput = new float[tileStates.length][];
        for(int tileIndex = 0; tileIndex < QuickNet.getNumFeaturesPerObject(); tileIndex ++) {
            //System.out.println("Embedding tileIndex #" + tileIndex + " of value " + (inputsByCatagory[tileIndex]) + " len " + inputsByCatagory.length);
            //System.out.println("input tileIndex size perobj: " + netInput.length);
            //System.out.println("batch_size at input: " + netInput[tileIndex].length);
            //System.out.println("possibleFeatures: " + lookupTable[tileIndex].length);
            //netInput[tileIndex][tileIndex%QuickNet.BATCH_SIZE]
            //        = lookupTable[tileIndex][inputsByCatagory[tileIndex]];//Functions.dot(inputsByCatagory[tileIndex], lookupTable[tileIndex]);
            netInput[tileIndex] = new float[tileStates[tileIndex].length];
            for(int obj = 0; obj < QuickNet.getBatchSize(); obj ++) {
                final int tileState = tileStates[tileIndex][obj];
                netInput[tileIndex][obj]
                        = lookupTable[tileIndex][tileState];
                        //= tileState;
                feedIndices[tileIndex][obj] = tileState;
            }
        }
        
        return Functions.appendBias(netInput);
    }
    
    public final void updateEmbedding(final float[][] losses) {
        
        for(int r = 0; r < feedIndices.length; r ++) {
            for(int c = 0; c < feedIndices[r].length; c ++) {
                deltas[r][feedIndices[r][c]] +=
                        losses[r][c] / QuickNet.getBatchSize();
            }
        }
        
        for(int r = 0; r < lookupTable.length; r ++) { 
            for(int c = 0; c < lookupTable[r].length; c ++) {
                velocities[r][c]
                        *= QuickNet.getMOMENTUM_INIT();
                
                velocities[r][c]
                        +=  (1d - QuickNet.getMOMENTUM_INIT()) * QuickNet.getETA_INIT()
                            * deltas[r][c];
                
                lookupTable[r][c] -= velocities[r][c];
                
                deltas[r][c] = 0f;
            }
        }
        
    }
    
    public final void print() {
        System.out.println(new FlowMatrix(lookupTable));
    }
    
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder("\nEmbedding Matrix (off/on values for each light"
                + "\n[only displaying weights for the most recent "
                + "\n25 lights in recorded board state])\n");
        
        for(int light = 0; light < lookupTable.length / 3f; light ++) {
            builder.append("Light: ")
                    .append(String.valueOf(light))
                        .append(" Off Value = ").append(lookupTable[light][0])
                    .append(" On Value = ").append(lookupTable[light][1])
               .append("\n");
        }
     
        return builder.toString();
    }
    
}
