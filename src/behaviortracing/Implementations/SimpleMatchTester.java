package behaviortracing.Implementations;

import behaviortracing.Implementation;
import behaviortracing.quicknet.QuickNet;
import java.util.Random;

/**
 *
 * @author MijitR
 */
public class SimpleMatchTester implements Implementation {
    
    public static final Random RAND;
    
    static {
        RAND = new Random();
    }

    @Override
    public final boolean implement(final QuickNet net) {
        final int[][] inputBatch =
                new int[QuickNet.getNumFeaturesPerObject()][QuickNet.getBatchSize()];
        
        for(int feature = 0; feature < inputBatch.length; feature ++) {
            for(int object = 0; object < inputBatch[feature].length; object ++) {
                inputBatch[feature][object] = RAND.nextInt(QuickNet.getNumUniqueStatesPerFeature());
            }
        }
        
        final int[][] answers =
                new int[net.getOutputHeight()][QuickNet.getBatchSize()];
        
        for(int feature = 0; feature < answers.length; feature ++) {
            for(int object = 0; object < answers[feature].length; object ++) {
                answers[feature][object] = RAND.nextInt(2);
            }
        }
        
        long epoc = 0; float cost = 0f;
        do {
            
            if(epoc % 100 == 1) {
                System.out.println(cost);
            }
            
            net.feed(inputBatch);
            net.train(answers);
            
            epoc ++;
        } while((cost = net.getCost()) > 0.0001f);
        
        System.out.println(epoc + " : :: >> " + net.getCost());
        
        return true;
    }
    
}
