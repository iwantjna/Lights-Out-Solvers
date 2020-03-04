package behaviortracing.quicknet.Layers;

import behaviortracing.quicknet.Functions;
import behaviortracing.quicknet.QuickNet;
import behaviortracing.quicknet.Traits.*;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author MijitR
 */
public class NeuralLayer {
    
    private static final Random RAND;
    
    static {
        final long seed = 1001l;
        RAND = new Random(seed);
    }
    
    final Traits traits;
    protected final int inputSize;
    
    private final float[][] weights, gradients, dwrtI, dwrtW,
            deltaWeights, velocities;
    
    private final boolean isOutput;
    
    public NeuralLayer(final Traits t, final int inputSize, final boolean isOutput) {
        this.traits = t.clone();
        this.inputSize = inputSize;
        this.weights = new float
            [this.traits.size()]
            [this.inputSize]
        ;
        this.gradients = new float
            [this.traits.size()]
            [QuickNet.getBatchSize()]
        ;
        this.dwrtI = new float
            [this.traits.size()]
            [this.inputSize]
        ;
        this.dwrtW = new float
            [this.inputSize]
            [QuickNet.getBatchSize()]
        ;
        this.deltaWeights = new float
            [this.traits.size()]
            [this.inputSize]
        ;
        this.velocities = new float
            [this.traits.size()]
            [this.inputSize]
        ;
        this.init();
        this.isOutput = isOutput;
    }
    
    final void init() {
        //Arrays.stream(this.weights).forEach
        //    (
        //        (x) -> { fill(x); }
        //    );
        for(final float[] space : weights) {
            this.fill(space);
        }
        
    }
    
    private int spaces = 0, height = 0;
    final void fill(final float[] neuronSpace) {
        for(int f = 0; f < neuronSpace.length; f ++) {
            spaces ++;
            //neuronSpace[f] = 1f;// / (spaces + this.traits.size() * height);//1f/(1f+(spaces+1));//getRand();
            spaces %= neuronSpace.length;
            neuronSpace[f] = getRand();
            //spaces += spaces * this.weights[f];
        }
        height ++;
        height %= this.weights.length;
    }
    
    final float getRand() {
        return //(2f*RAND.nextFloat() - 1f)
                (float)Math.max(-30f,Math.min(30f, (RAND.nextGaussian()* (float) Math.sqrt(2f)
                        / (float)Math.sqrt(/*this.traits.size()*/ 25 + inputSize))))
                ;
    }
    
    public final float[][] transform(final float[][] input) {
        
        //Initialize result matrix R (myRowCount(Layer Size) X inputColCount(Batch size))
        //Initialize jacobian weight matrix(inputColCount(Batch) X myRowCount))
        
        // 1)For the value of each coordinate, calculate the dot product
        //      of the input vector at column x and the weight vector at row y
        
        // 2)DwrtI matrix is of dims(weights^T). Set values per dot product
        //      at (x,y) into DwrtI matrix at (y,x)
        
        //3)DwrtW matrix is of dims(input
        
        //dwrtI = Functions.transpose(weights);
        
        int trait = 0;
        for(final float[] batchesForTrait : input) {
            System.arraycopy(batchesForTrait,0,this.dwrtW[trait],0,batchesForTrait.length);
            trait ++;
        }
        
        int neuron = 0;
        for(final float[] neuronWeights : weights) {
            System.arraycopy(neuronWeights,0,this.dwrtI[neuron],0,neuronWeights.length);
            neuron ++;
        }
        
        final float[][] usefulInput = Functions.transpose(input);
        
        final float[][] result = processImage(usefulInput);/*new float[this.traits.size()][QuickNet.getBatchSize()];
                
                for(neuron = 0; neuron < result.length; neuron ++) {
                
                for(int batchInst = 0; batchInst < QuickNet.getBatchSize(); batchInst ++) {
                result[neuron][batchInst] = Functions.dot(weights[neuron], usefulInput[batchInst]);
                }
                
                }*/
        
        //System.out.println("Input :\n" + new FlowMatrix(input));
        //System.out.println("before :\n" + new FlowMatrix(gradients));
        Functions.activate(result, gradients, this.traits.activations());
        //System.out.println("after :\n" + new FlowMatrix(gradients));
        
        //weights[0][0] += 1f;
        /*if(!this.isOutput && QuickNet.isFeaturesThreshHeld()) {
            final float[][] thruput
                     = new float[result.length][];

            for(int r = 0; r < result.length; r ++) {
                thruput[r] = new float[result[r].length];
            }

            Functions.decideFromThreshold(result, thruput, 0f);

            Functions.mask(result, thruput);
        } */
        if(!this.isOutput && QuickNet.isDropoutActive()) {
            Functions.dropout(result, gradients);
        } else if(!this.isOutput && Float.compare(QuickNet.getDropoutPercentage(), 0f) > 0){
            Functions.scale(result, (1f-QuickNet.getDropoutPercentage()));
        }
        
        return result;
        
    }
    
    protected float[][] processImage(final float[][] input) {
        final float[][] result = new float[this.traits.size()][QuickNet.getBatchSize()];
        
        for(int neuron = 0; neuron < result.length; neuron ++) {
        
            for(int batchInst = 0; batchInst < QuickNet.getBatchSize(); batchInst ++) {
                result[neuron][batchInst] = Functions.dot(weights[neuron], input[batchInst]);
            }
            
        }
        return result;
    }
    
    public final float[][] consider(final float[][] errors) {
        final float[][] influence = new float[this.traits.size()][QuickNet.getBatchSize()];
        for(int y = 0; y < this.traits.size(); y ++) {
            for(int x = 0; x < QuickNet.getBatchSize(); x ++) {
                influence[y][x] = gradients[y][x] * errors[y][x];
            }
        }
        
        for(int neuron = 0; neuron < weights.length; neuron ++) {
            for(int trait = 0; trait < weights[neuron].length; trait ++) {
                deltaWeights[neuron][trait] = 0f;
                for(int batchIndex = 0; batchIndex < QuickNet.getBatchSize(); batchIndex ++) {
                    deltaWeights[neuron][trait] += influence[neuron][batchIndex] * dwrtW[trait][batchIndex] / QuickNet.getBatchSize();
                    /*
                    deltaWeights[neuron][trait] = Math.max(-QuickNet.getGradientClip(),
                            Math.min(deltaWeights[neuron][trait],
                                QuickNet.getGradientClip()));
                    */
                }
            }
        }
        
        final float[][] inputInfluencers = new float[inputSize][QuickNet.getBatchSize()];
        for(int trait = 0; trait < inputSize; trait ++) {
            for(int instance = 0; instance < QuickNet.getBatchSize(); instance ++) {
                for(int neuron = 0; neuron < this.traits.size(); neuron ++) {
                    inputInfluencers[trait][instance] += influence[neuron][instance] * dwrtI[neuron][trait];/// QuickNet.BATCH_SIZE;//this.traits.size();
                }
            }
        }
        
        
        
        for(int neuron = 0; neuron < weights.length; neuron ++) {
            for(int trait = 0; trait < weights[neuron].length; trait ++) {
                velocities[neuron][trait] *= QuickNet.getMOMENTUM_INIT();
                velocities[neuron][trait] += (1d - QuickNet.getMOMENTUM_INIT()) * QuickNet.getETA_INIT() * deltaWeights[neuron][trait];
                //velocities[neuron][trait] = Math.max(-QuickNet.getGradientClip(),
                //        Math.min(velocities[neuron][trait], QuickNet.getGradientClip()));
                weights[neuron][trait] -= velocities[neuron][trait];
            }
        }
        
        //System.out.println("Influence:\n" + new FlowMatrix(inputInfluencers));
        //System.out.println("Convinced:\b" + new FlowMatrix(convinced));
        
        return inputInfluencers;
    }
    
    public final boolean isOutput() {
        return this.isOutput;
    }
    
    public int getOutputSize() {
        return this.traits.size();
    }
    
    @Override
    public final String toString() {
        final StringBuilder builder
                = new StringBuilder(traits.toString())
                        .append(" :: Inputs (").append(inputSize-QuickNet.getBIAS_SPACE_ALLOCATION())
                    .append(" + bias(es))").append("\n");
        Arrays.stream(weights).forEach(n ->
            builder.append(Arrays.toString(n)).append("\n")
        );
        return builder.toString();
    }
}
