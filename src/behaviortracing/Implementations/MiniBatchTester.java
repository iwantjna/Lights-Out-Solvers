package behaviortracing.Implementations;

import behaviortracing.Implementation;
import behaviortracing.LightsOut.Utils.BoardGroup;
import behaviortracing.quicknet.Functions;
import behaviortracing.quicknet.QuickNet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author MijitR
 */
public class MiniBatchTester implements Implementation {
    
    private static final int MINI_BATCH_COUNT;
    
    static {
        MINI_BATCH_COUNT = 200
                ;
    }
    
    //private final int[][] batch1, ans1, batch2, ans2, guess1, guess2;
    
    private final int[][][] batches, answers, guesses;
    
    private final List<Integer> batchIndices, miniBatchIndices;
    
    public MiniBatchTester() {
        
        final BoardGroup group = new BoardGroup(QuickNet.getBatchSize());
        /*batch1 = group.getBatch();
        ans1 = group.getAnswers();
        
        group.initBoardGroup(QuickNet.getBatchSize());
        batch2 = group.getBatch();
        ans2 = group.getAnswers();*/
        
        batches = new int[MINI_BATCH_COUNT][][];
        answers = new int[MINI_BATCH_COUNT][][];
        guesses = new int[MINI_BATCH_COUNT][][];
        
        batchIndices = new ArrayList<>();
        miniBatchIndices = new ArrayList<>();
        
        for(int i = 0; i < batches.length; i ++) {
            batches[i] = group.getBatch();
            batchIndices.add(i);
            answers[i] = group.getAnswers();
            for(int r = 0; r < answers[i].length; r ++) {
                guesses[i] = new int[answers[i].length][answers[i][r].length];
            }
            if(i < batches.length - 1) {
                group.initBoardGroup(QuickNet.getBatchSize());
            }
        }
        
        for(int i = 0; i < QuickNet.getBatchSize(); i ++) {
            miniBatchIndices.add(i);
        }
        
        /*guess1 = new int[ans1.length][];
        guess2 = new int[ans2.length][];
        for(int r = 0; r < guess1.length; r ++) {
            guess1[r] = new int[ans1[r].length];
            guess2[r] = new int[ans2[r].length];
        }*/
    }

    @Override
    public boolean implement(final QuickNet net) throws InterruptedException {
        /*System.out.println("Board 1:");
        BoardGroup.print(batches[0], answers[0]);
        System.out.println("Board 2:");
        BoardGroup.print(batches[1], answers[1]);*/
        
        long epoc = 0; float cost;
        
        System.out.println("Cost: average distance between answer set and"
                + "\nsolved set per board per epoc (linearly measured [i.e."
                + "\na value of 0.5 means that a board would most likely have 1"
                + "\nresult from the network that is incorrect out of 25 (since"
                + "\nbeing within 0.5 indicates being correct)], s.t. the"
                + "\nhigher the cost, the more appropriately it can be thought of"
                + "\nas the number of incorrect decisions made by the network per"
                + "\nboard per batch out of the 25 per board)"
                + "\n");
        
        do {
            cost = 0f;
            
            Collections.shuffle(batchIndices);
            Collections.shuffle(miniBatchIndices);
            
            for(int i = 0; i < batches.length; i ++) {
                //Collections.shuffle(Arrays.asList(batches[batchIndices.get(i)]).);
                //System.arraycopy(Functions.decideFromThreshold(net.feed(batches[i]), 0.5f),
                //        0, guesses[i], 0, guesses[i].length);
                /*Functions.decideFromThreshold(*/
                net.feed(batches[batchIndices.get(i)])
                        ;
                //, guesses[i], 0.5f);
                
                net.train(answers[batchIndices.get(i)]);
                
                cost += net.getLinearCost();
                
            }
            
            cost /= batches.length / 25f
                    ;
            
            /*System.arraycopy(Functions.decideFromThreshold(net.feed(batch1), 0.5f),
                    0, guess1, 0, guess1.length);
            net.train(ans1);
            
            cost += net.getCost() / 2f;*/
            if(epoc % 100 == 0) {
                System.out.println("Epoc: " + epoc + " Cost: " + (cost) + " (exit loop at 0.03)"
                );
            }
            
            /*final int[][] results2 = Functions.decideFromThreshold(net.feed(batch2), 0.5f);
            System.arraycopy(results2,
                    0, guess2, 0, Math.max(results2.length, guess2.length));
            net.train(ans2);
            
            cost += net.getCost() / 2f;
            if(epoc % 100 == 0) {
                System.out.println("Epoc: " + epoc + " Cost: " + cost);
            }*/
            
            epoc ++;
            
        } while(epoc < 25000 && cost >  0.03f);
        
        QuickNet.setDropoutActive(false);
        
        System.out.println("\nEpocs Trained: " + epoc + "\n");

        for(int i = 0; i < batches.length; i ++) {
            //System.arraycopy(Functions.decideFromThreshold(net.feed(batches[i]), 0.5f),
            //            0, guesses[i], 0, guesses[i].length);
            Functions.decideFromThreshold(net.feed(batches[i]), guesses[i], 0.5f);
            /*System.arraycopy(Functions.decideFromThreshold(net.feed(batches[1]), 0.5f),
                        0, guesses[1], 0, guesses[1].length);

            
            System.out.println(epoc + " --> " + cost);

            System.out.println("Num wrong batch 1: " + Functions.absDist(answers[0], guesses[0]));*/
            final long numWrong;
            System.out.println("Num wrong batch " + i + ": " + (numWrong = Functions.absDist(answers[i], guesses[i])) + " (" + (float)numWrong/(guesses.length*guesses[0].length)+ "%)");
            //System.out.println(new FlowMatrix([i]));
            
        }
        
        QuickNet.printEmbeddings(net);
        //QuickNet.print(net);
        
        return true;
    }
    
    

}
