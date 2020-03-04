package behaviortracing.quicknet.Layers;

import behaviortracing.quicknet.QuickNet;
import behaviortracing.quicknet.Traits.Traits;

/**
 *
 * @author MijitR
 */
public class ConvLayer extends NeuralLayer {
    
    private final int kSize, instances;
    
    public ConvLayer(final Traits t, final int inputSize, final boolean isOutput) {
        super(t, inputSize, isOutput);
        kSize = (int)Math.sqrt(t.size());
        instances = (int)(Math.sqrt(inputSize-QuickNet.getBIAS_SPACE_ALLOCATION())
                + 2*traits.padding() - kSize)
                    / Math.max(1,traits.padding()) + 1;
    }
    
    @Override
    public final float[][] processImage(final float[][] image) {
        
        //By now, each board is a row, capped by the bias at the end
        
        
        //System.out.println(Math.sqrt(image[0].length-1));
        
        final float[][] result = new float[super.traits.depth()*instances*instances][QuickNet.getBatchSize()];
        
        for(int neuron = 0; neuron < super.traits.depth(); neuron ++) {
            final int translatory2DOffset = neuron * instances*instances;
            for(int instance = 0; instance < instances * instances; instance ++) {
                
                System.out.println(instance + translatory2DOffset);
            
            }
            
        }
        
        /*for(final float[] line : image) {
        System.out.println(Arrays.toString(line));
        } System.out.println();*/
        
        return result;
    }
    
    @Override
    public final int getOutputSize() {
        return instances * instances * traits.depth();
    }

}
