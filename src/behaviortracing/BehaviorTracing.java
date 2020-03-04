/*
 *  Dis how ballers do..
 */
package behaviortracing;

import behaviortracing.Implementations.MiniBatchTester;
import behaviortracing.Implementations.NoteMatchTester;
import behaviortracing.Implementations.SimpleMatchTester;
import behaviortracing.quicknet.QuickNet;
import behaviortracing.quicknet.Traits.Activations;
import behaviortracing.quicknet.Traits.Traits;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author MijitR <MijitR.King @ mijitr.xyz>
 */
public class BehaviorTracing {
    
    private static final int BATCH_SIZE_DEFAULT;
    
    static {
        BATCH_SIZE_DEFAULT = 7;
    }

    /**
     * @param args the command line arguments
     */
    public static final void main(final String[] args) {
        final BehaviorTracing tracer =
                new BehaviorTracing(BATCH_SIZE_DEFAULT);
        
        tracer.run(Job.MINI_BATCH_MATCHING);
    }
    
    private final QuickNet net;

    public BehaviorTracing(final int batchSize) {
        
        QuickNet.setBatchSize(batchSize);
        QuickNet.setNumFeaturesPerObject(75);
        QuickNet.setNumUniqueStatesPerFeature(2);
        
        QuickNet.setDropoutActive(false);
        QuickNet.setDropoutPercentage(0.0f);
        
        QuickNet.setThreshHoldFeatureMaps(false);
        
        QuickNet.setETA(0.01f);
        System.out.println("Beginning Leaning Rate at: " + QuickNet.getETA_INIT());
        QuickNet.setMOMENTUM(0.9f);
        System.out.println("Initializing Momentum to: " + QuickNet.getMOMENTUM_INIT());
        
        //QuickNet.setGradientClip(0.05f);
        System.out.println(Float.compare(QuickNet.getGradientClip(),Float.MAX_VALUE)==0 ?
                "No gradient clipping active" : "Gradient clipping active at " + QuickNet.getGradientClip());
        
        final Traits layer1Traits
                = new Traits(
                        Integer.parseInt(JOptionPane.showInputDialog(null, "50 is typical here", "50")), 
                        Activations.values()[JOptionPane.showOptionDialog(null, "RELU is typical here", "FIRST HIDDEN LAYER ACTIVATION", JOptionPane.OK_CANCEL_OPTION, JOptionPane.YES_OPTION, null, Activations.values(), Activations.RELU)]
                );
        
        final Traits layer2Traits
                = new Traits(Integer.parseInt(
                        JOptionPane.showInputDialog(null, "25 is typical here", "25")),
                        Activations.values()[JOptionPane.showOptionDialog(null, "DROPPEDSOFT is typical here", "SECOND HIDDEN LAYER ACTIVATION", JOptionPane.OK_CANCEL_OPTION, JOptionPane.YES_OPTION, null, Activations.values(), Activations.DROPPEDSOFT)]
                );
      
        final Traits layer3Traits
                = new Traits(QuickNet.getNumFeaturesPerObject(), Activations.TANH);
        
        final Traits layer4Traits
                = new Traits(13, Activations.TANH);
        
        final Traits outputLayerTraits
                = new Traits(25, Activations.SIGMOID);
        
        final List<Traits> topology = new ArrayList<>();
        
        topology.add(layer1Traits);
        topology.add(layer2Traits);
        //topology.add(layer3Traits);
        //topology.add(layer4Traits);
        topology.add(outputLayerTraits);
        
        net = new QuickNet(topology);
        
        topology.clear();
        
    }
    
    public final void run(final Job job) {
        final Implementation implementation;
        switch(job) {
            case NOTE_MATCHING:
                implementation = new NoteMatchTester();
                break;
            case MINI_BATCH_MATCHING:
                implementation = new MiniBatchTester();
                break;
            default:
            case SIMPLE_MATCHING:
                implementation = new SimpleMatchTester();
                break;
        }
        try{
            implementation.implement(net);
        } catch(final InterruptedException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
    
}
