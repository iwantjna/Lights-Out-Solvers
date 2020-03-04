/*
 *  Dis how ballers do..
 */
package behaviortracing.quicknet;

import behaviortracing.quicknet.Layers.EmbeddedMatrix;
import behaviortracing.quicknet.Layers.NeuralLayer;
import behaviortracing.quicknet.Traits.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author MijitR <MijitR.King @ mijitr.xyz>
 */
public class QuickNet {
    
    private static int BIAS_SPACE_ALLOCATION;
    
    private static int NUM_UNIQUE_STATES_PER_FEATURE;
    
    private static int NUM_FEATURES_PER_OBJ;
    
    private static int BATCH_SIZE;
    
    private static float GRADIENT_CLIP;
    
    private static float ETA_INIT;
    private static float MOMENTUM_INIT;
    private static boolean THRESHHOLD_FEATURE_MAP, DROPOUT;
    
    private static float DROPOUT_PERCENT;
    
    static {
        setBiasSpaceAllocation(1);
        
        setBatchSize(250);
        
        setNumFeaturesPerObject(50);
        
        //Hidden dimension
        setNumUniqueStatesPerFeature(2); //On, Off
        
        setThreshHoldFeatureMaps(false);
        
        setDropoutActive(false);
        
        setDropoutPercentage(0.0f);
        
        setETA(isFeaturesThreshHeld() ? 0.3f : 0.07f);
        
        setGradientClip(Float.MAX_VALUE);
        
        setMOMENTUM(0.9f);
    }
    
    private final List<NeuralLayer> layers;
    
    private final List<Traits> layerTraits;
    
    private EmbeddedMatrix inputMat;
    
    private final float[][] answer;
    private final float[][] cost;
    private final float[] linearCost;
    
    public QuickNet(final List<Traits> layerTraits) {
        this.layerTraits = new ArrayList<>(layerTraits.size());
        this.copyTraits(layerTraits);
        
        this.layers = new ArrayList<>(this.layerTraits.size());
        this.buildLayers();
        
        this.inputMat = new EmbeddedMatrix();
        
        this.answer = new float[this.layerTraits.get(this.layerTraits.size()-1).size()][QuickNet.BATCH_SIZE];
        this.cost = new float[1][QuickNet.BATCH_SIZE];
        this.linearCost = new float[QuickNet.BATCH_SIZE];
    }
    
    final void copyTraits(final List<Traits> layerTraits) {
        this.layerTraits.clear();
        layerTraits.stream().forEachOrdered
            (x -> this.layerTraits.add(x.clone())
        );
    }
    
    final void buildLayers() {
        /*for(int i = 0; i < this.layerTraits.size() - 1; i ++) {
            this.layers.add(new ConvLayer(this.layerTraits.get(i), 
                (i == 0 ? QuickNet.getNumFeaturesPerObject() : this.layers.get(i-1).getOutputSize())
                        + getBIAS_SPACE_ALLOCATION(), false/*i == this.layerTraits.size() - 1*/ /*)
            );
        }*/
        System.out.println("Building Layers");
        for(int i = 0; i < this.layerTraits.size() - 1; i ++) {
            System.out.println(this.layerTraits.get(i));
            this.layers.add(new NeuralLayer(this.layerTraits.get(i), 
                (i == 0 ? QuickNet.getNumFeaturesPerObject() : this.layers.get(i-1).getOutputSize())
                        + getBIAS_SPACE_ALLOCATION(), false/*i == this.layerTraits.size() - 1*/)
            );
        } System.out.println(this.layerTraits.get(this.layerTraits.size()-1) + "\n");
        this.layers.add(new NeuralLayer(this.layerTraits.get(layerTraits.size()-1), 
                this.layers.get(this.layers.size()-1).getOutputSize()
                    + getBIAS_SPACE_ALLOCATION(), true));
        
        //QuickNet.print(this);
    }
    
    public final float[][] feed(final int[][] batch) {
        
        float[][] thruput //= new float[batch.length + 1][];
                = getInputMat().embedRowMajor(batch);
        
        if(QuickNet.isFeaturesThreshHeld()) {
            thruput = Functions.threshHold(thruput, 0f);
        }
        //System.out.println("Clean thruput");
        //for(int r = 0; r < batch.length; r ++) {
          //  System.out.println(Arrays.toString(batch[r]));
            //input[r] = new float[batch[r].length];
            //for(int c = 0; c < batch[r].length; c ++) {
           //     thruput[r][c] = batch[r][c];
            //}
            //System.arraycopy(batch[r],0,thruput[r],0,batch[r].length);
        //}
        
        //input[batch.length] = new float[QuickNet.BATCH_SIZE];
        //for(int c = 0; c < QuickNet.BATCH_SIZE; c ++) {
        //    thruput[batch.length][c] = -1f;
        //}
       // System.out.println(new FlowMatrix(thruput));
        
        //System.out.println(new FlowMatrix(layers.get(0).transform(thruput)));
        
        for(int l = 0; l < this.layers.size() - 1; l ++) {
            thruput = Functions.appendBias(this.layers.get(l).transform(thruput));
        
            //System.out.println(new FlowMatrix(thruput));
        }
        
        thruput = this.layers.get(this.layers.size()-1).transform(thruput);
        
        for(int y = 0; y < thruput.length; y ++) {
            System.arraycopy(thruput[y],0,answer[y],0,thruput[y].length);
        }
        
        //System.out.println("Result :\n"+new FlowMatrix(answer));
        
        return thruput;
        
        //initialze thruput matrix
        
        //feed through each layer
        
        //return the output of the final layer
        
        //return null;
    }
    
    public final float[][] train(final int[][] correctAnswers) {
        
        //Calculate cost function
        //  *) assign loss function values to each output
        
        //Train each layer ('back' to 'front') **)"Consider"
        
        //return the influences on the cost function
        //  that pair with the inputs
        
        //System.out.println("Training :\n"+new FlowMatrix(correctAnswers));
        
        float[][] loss = new float[correctAnswers.length][QuickNet.getBatchSize()];
        
        //float[][] cost = new float[1][QuickNet.BATCH_SIZE];
        
        final float epsilon = Float.MIN_VALUE;
                //(float)Math.pow(10f, -15f);
                //0.00000005f;
        
        Arrays.fill(cost[0], 0f);
        Arrays.fill(linearCost, 0f);
        
        for(int x = 0; x < QuickNet.getBatchSize(); x ++) {
            
            for(int y = 0; y < correctAnswers.length; y ++) {
                //answer[y][x] = Math.max(epsilon, Math.min(1f-epsilon, answer[y][x]));
                cost[0][x] += 0.5f * Math.pow(correctAnswers[y][x]-answer[y][x],2f);// / QuickNet.BATCH_SIZE;
                
                linearCost[x] += Math.abs(correctAnswers[y][x] - answer[y][x]);
                //loss[y][x] = (answer[y][x] - correctAnswers[y][x])
                //        ;
                //loss[y][x] = ((1-correctAnswers[y][x]) *
                //        1f/(1f-answer[y][x]+epsilon) + correctAnswers[y][x] * -1f/(answer[y][x]+epsilon))
                //            ;/// QuickNet.BATCH_SIZE;
                loss[y][x] = correctAnswers[y][x] == 0 ?
                        1f/(1f-answer[y][x]+epsilon) : -1f/(answer[y][x]+epsilon)
                            ;            /// correctAnswers.length;
                        //correctAnswers[y][x] == 0 ? -1f/(answer[y][x])
                        //    :
                        //-1f/(1f-answer[y][x]);
                        /*final float temp = loss[y][x];
                loss[y][x] = Math.max(-QuickNet.GRADIENT_CLIP,
                        Math.min(loss[y][x],
                                QuickNet.GRADIENT_CLIP));
                if(Float.compare(loss[y][x], temp) != 0) {
                    System.out.println(temp + " -> " + loss[y][x]);
                    System.out.println(correctAnswers[y][x] + " vs " + answer[y][x]);
                }*/
            }
            
            cost[0][x] /= correctAnswers.length;
            linearCost[x] /= correctAnswers.length;
            
        }
        //for(final float[] l : loss)
        //    System.out.println(Arrays.toString(l));
        
        //System.out.println("Training Cost: " + new FlowMatrix(cost));
        
        //System.out.println("Training Loss:\n" + new FlowMatrix(loss));
        
        for(int lay = this.layers.size()-1; lay >= 0; lay --) {
            loss = this.layers.get(lay).consider(loss);
        }
        
        getInputMat().updateEmbedding(loss);
        
        return loss;
    }
    
    public final float getCost() {
        return (float) Arrays.stream(cost).mapToDouble(f -> Functions.sum(f)).sum();
    }
    
    public final float getLinearCost() {
        return Functions.sum(linearCost) / linearCost.length;
    }
    
    public final int getOutputHeight() {
        return this.layerTraits.get(this.layerTraits.size()-1).size();
    }
    
    @Override
    public final String toString() {
        final StringBuilder builder
                = new StringBuilder("");
        this.layers.stream().forEachOrdered
            (x -> builder.append(x.toString()).append("\n"));
        return builder.toString();
    }
    
    ////////////////////////////////
    ////////////////////////////////
    public final static void print(final QuickNet net) {
        System.out.println(net.toString());
    }
    
    public final static void printEmbeddings(final QuickNet net) {
        System.out.println(net.getInputMat().toString());
    }

    /**
     * @return the BIAS_SPACE_ALLOCATION
     */
    public static int getBIAS_SPACE_ALLOCATION() {
        return BIAS_SPACE_ALLOCATION;
    }

    /**
     * @param aBIAS_SPACE_ALLOCATION the BIAS_SPACE_ALLOCATION to set
     */
    public static void setBiasSpaceAllocation(int aBIAS_SPACE_ALLOCATION) {
        BIAS_SPACE_ALLOCATION = aBIAS_SPACE_ALLOCATION;
    }

    /**
     * @return the NUM_UNIQUE_STATES_PER_FEATURE
     */
    public static int getNumUniqueStatesPerFeature() {
        return NUM_UNIQUE_STATES_PER_FEATURE;
    }

    /**
     * @param aNUM_UNIQUE_STATES_PER_FEATURE the NUM_UNIQUE_STATES_PER_FEATURE to set
     */
    public static void setNumUniqueStatesPerFeature(int aNUM_UNIQUE_STATES_PER_FEATURE) {
        NUM_UNIQUE_STATES_PER_FEATURE = aNUM_UNIQUE_STATES_PER_FEATURE;
    }

    /**
     * @return the NUM_FEATURES_PER_OBJ
     */
    public static int getNumFeaturesPerObject() {
        return NUM_FEATURES_PER_OBJ;
    }

    /**
     * @param aNUM_FEATURES_PER_OBJ the NUM_FEATURES_PER_OBJ to set
     */
    public static void setNumFeaturesPerObject(int aNUM_FEATURES_PER_OBJ) {
        NUM_FEATURES_PER_OBJ = aNUM_FEATURES_PER_OBJ;
    }

    /**
     * @return the BATCH_SIZE
     */
    public static int getBatchSize() {
        return BATCH_SIZE;
    }

    /**
     * @param aBATCH_SIZE the BATCH_SIZE to set
     */
    public static void setBatchSize(int aBATCH_SIZE) {
        BATCH_SIZE = aBATCH_SIZE;
    }

    /**
     * @return the ETA_INIT
     */
    public static float getETA_INIT() {
        return ETA_INIT;
    }

    /**
     * @param aETA_INIT the ETA_INIT to set
     */
    public static void setETA(float aETA_INIT) {
        ETA_INIT = aETA_INIT;
    }

    /**
     * @return the MOMENTUM_INIT
     */
    public static float getMOMENTUM_INIT() {
        return MOMENTUM_INIT;
    }

    /**
     * @param aMOMENTUM_INIT the MOMENTUM_INIT to set
     */
    public static void setMOMENTUM(float aMOMENTUM_INIT) {
        MOMENTUM_INIT = aMOMENTUM_INIT;
    }

    /**
     * @return the THRESHHOLD_FEATURE_MAP
     */
    public static boolean isFeaturesThreshHeld() {
        return THRESHHOLD_FEATURE_MAP;
    }

    /**
     * @param aTHRESHHOLD_FEATURE_MAP the THRESHHOLD_FEATURE_MAP to set
     */
    public static void setThreshHoldFeatureMaps(boolean aTHRESHHOLD_FEATURE_MAP) {
        THRESHHOLD_FEATURE_MAP = aTHRESHHOLD_FEATURE_MAP;
    }
    
    public static boolean isDropoutActive() {
        return DROPOUT;
    }
    
    public static void setDropoutActive(final boolean DROPOUT) {
        QuickNet.DROPOUT = DROPOUT;
    }
    
    public static float getDropoutPercentage() {
        return DROPOUT_PERCENT;
    }
    
    public static void setDropoutPercentage(final float percentage) {
        QuickNet.DROPOUT_PERCENT = percentage;
    }
    
    public static final float getGradientClip() {
        return QuickNet.GRADIENT_CLIP;
    }
    
    public static final void setGradientClip(final float gC) {
        QuickNet.GRADIENT_CLIP = gC;
    }

    

    /**
     * @return the layers
     */
    public final NeuralLayer[] getLayers() {
        return (NeuralLayer[])layers.toArray();
    }

    /**
     * @param layers the layers to set
     */
    public void setLayers(List<NeuralLayer> layers) {
        this.layers.clear();
        this.layers.addAll(layers);
    }

    /**
     * @return the layerTraits
     */
    public final Traits[] getLayerTraits() {
        return (Traits[])layerTraits.toArray();
    }

    /**
     * @param layerTraits the layerTraits to set
     */
    public void setLayerTraits(List<Traits> layerTraits) {
        this.layerTraits.clear();
        this.layerTraits.addAll(layerTraits);
    }

    /**
     * @return the inputMat
     */
    public EmbeddedMatrix getInputMat() {
        return inputMat;
    }

    /**
     * @param inputMat the inputMat to set
     */
    public void setInputMat(EmbeddedMatrix inputMat) {
        this.inputMat = inputMat;
    }
    
    /**
     * @return the answer
     */
    public float[][] getAnswer() {
        return answer.clone();
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(float[][] answer) {
        //this.answer = answer.clone();
        System.arraycopy(answer,0,this.answer,0,answer.length);
        Arrays.fill(this.answer, 0, answer.length, this.answer.length);
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(float[][] cost) {
        System.arraycopy(cost,0,this.cost,0,cost.length);
        Arrays.fill(this.cost, 0, cost.length, this.cost.length);
    }
    
}
