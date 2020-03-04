package behaviortracing.LightsOut.Utils;

import behaviortracing.LightsOut.Board;
import static behaviortracing.LightsOut.Board.HEIGHT;
import static behaviortracing.LightsOut.Board.WIDTH;
import java.util.Arrays;

/**
 *
 * @author MijitR
 */
public class BoardGroup {

    private final int[][] boardGroup;
    
    private final int[][] clickPathGroup;
    
    public BoardGroup(final int BATCH_SIZE) {
        boardGroup = new int[3*HEIGHT*WIDTH][BATCH_SIZE];
        clickPathGroup = new int[HEIGHT*WIDTH][BATCH_SIZE];
        
        initBoardGroup(BATCH_SIZE);
    }
    
    public final void initBoardGroup(final int BATCH_SIZE) {
        
        //final int DEPTH = 5;
        //final Board bRef;// = Board.fetch();
        
        for(int i = 1; i < BATCH_SIZE; i ++) {
            
            final int[] linState = Board.fetch().getState();//Board.fetch(QuickNet.NUM_UNIQUE_STATES_PER_FEATURE-1).getState();
            
            for(int tile = 0; tile < 3*HEIGHT*WIDTH; tile ++) {
                boardGroup[tile][i] = linState[tile];
            }
            final int[] linClickPath = Board.fetch().getClickPath();
            for(int tile = 0; tile < HEIGHT*WIDTH; tile ++) {
                clickPathGroup[tile][i] = linClickPath[tile];
            }
            
        }
    }
    
    public final int[][] getBatch() {
        final int[][] batchVals = new int[3*HEIGHT*WIDTH][];
        for(int i = 0; i < batchVals.length; i ++) {
            batchVals[i] = boardGroup[i].clone();
        }
        return batchVals;
    }
    
    public final int[][] getAnswers() {
        final int[][] batchAnswers = new int[HEIGHT*WIDTH][];
        for(int i = 0; i < batchAnswers.length; i ++) {
            batchAnswers[i] = clickPathGroup[i].clone();
        }
        return batchAnswers;
    }
    
    public final void print() {
        final StringBuilder builder = new StringBuilder();
        for(int r = 0; r < boardGroup.length; r ++) {
            builder.append(Arrays.toString(boardGroup[r]))
                    .append(r < boardGroup.length / 3 ? new StringBuilder("\t").append(Arrays.toString(clickPathGroup[r])) : "")
                .append("\n");
        }
        System.out.println(builder);
    }
    
    public static final void print(final int[][] boardGroup, final int[][] clickPathGroup) {
        final StringBuilder builder = new StringBuilder();
        for(int r = 0; r < boardGroup.length; r ++) {
            builder.append(Arrays.toString(boardGroup[r]))
                    .append(r < boardGroup.length / 3 ? new StringBuilder("\t").append(Arrays.toString(clickPathGroup[r])) : "")
                .append("\n");
        }
        System.out.println(builder);
    }
    
}
