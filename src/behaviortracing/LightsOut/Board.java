package behaviortracing.LightsOut;

import behaviortracing.quicknet.QuickNet;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author MijitR
 */
public class Board {
    
    private static final Random RAND;
    
    
    private static final State STATE;
    public static final int HEIGHT, WIDTH,
            CLICK_EFFECT, INIT_DEPTH;
    
    static {
        RAND = new Random(123l);
        STATE = new State();
        
        HEIGHT = WIDTH = 5;
        
        CLICK_EFFECT = +1;
        
        INIT_DEPTH = 5;
    }
    
    private static Board me;
    
    private Board() {
        init(INIT_DEPTH);
    }
    
    private Board(final int depth) {
        init(depth);
    }
    
    public static final void init(final int depth) {
        if(STATE.state == null) {
            STATE.init();
        } else {
            STATE.reset();
        }
        for(int z = 0; z < RAND.nextInt(depth) + 1; z ++) {
            for(int y = 0; y < HEIGHT; y ++) {
                STATE.pokeAt(RAND.nextInt(WIDTH), y);
            }
        }
    }
    
    public static final Board fetch() {
        //return me == null ? (me = new Board()) : me;
        return new Board();
    }
    
    public static final Board fetch(final int depth) {
        //return me == null ? (me = new Board(depth)) : me;
        return new Board(depth);
    }
    
    public final void click(final int x, final int y) {
        STATE.pokeAt(x, y);
    }
    
    public final int[] getState() {
        final int[] linearState = new int[3*HEIGHT*WIDTH];
        
        for(int y = 0; y < HEIGHT; y ++) {
            for(int x = 0; x < WIDTH; x ++) {
                linearState[y*WIDTH+x] = STATE.valueAt(x, y);
                linearState[(y*WIDTH+WIDTH*HEIGHT)+x] = STATE.lastValueAt(x, y);
                linearState[(y*WIDTH+2*HEIGHT*WIDTH)+x] = STATE.ancientValueAt(x, y);
            }
        }
        
        return linearState;
    }
    
    public final int[] getClickPath() {
        final int[] clickPath = new int[HEIGHT*WIDTH];
        
        for(int y = 0; y < HEIGHT; y ++) {
            for(int x = 0; x < WIDTH; x ++) {
                clickPath[y*WIDTH+x] = STATE.clickPathAt(x,y);
            }
        }
        
        return clickPath;
    }
            
    private static class State {
        private int[][] state, clickPath, prevState, ancientState;
        
        final void init() {
            state = new int[HEIGHT][WIDTH];
            clickPath = new int[HEIGHT][WIDTH];
            prevState = new int[HEIGHT][WIDTH];
            ancientState = new int[HEIGHT][WIDTH];
        } final void reset() {
            for(final int[] lin : state) {
                Arrays.fill(lin, 0);
            } for(final int[] lin : clickPath) {
                Arrays.fill(lin, 0);
            } for(final int[] lin : prevState) {
                Arrays.fill(lin, 0);
            } for(final int[] lin : ancientState) {
                Arrays.fill(lin, 0);
            }
        }
        
        final void pokeAt(final int x, final int y) {
            ancientState[y][x] = prevState[y][x];
            prevState[y][x] = state[y][x];
            state[y][x] = (state[y][x] + CLICK_EFFECT) % QuickNet.getNumUniqueStatesPerFeature();
            if(y - 1 >= 0) {
                ancientState[y-1][x] = prevState[y-1][x];
                prevState[y-1][x] = state[y-1][x];
                state[y-1][x] = (state[y-1][x] + CLICK_EFFECT) % QuickNet.getNumUniqueStatesPerFeature();
            } if(y + 1 < HEIGHT) {
                ancientState[y+1][x] = prevState[y+1][x];
                prevState[y+1][x] = state[y+1][x];
                state[y+1][x] = (state[y+1][x] + CLICK_EFFECT) % QuickNet.getNumUniqueStatesPerFeature();
            } if(x - 1 >= 0) {
                ancientState[y][x-1] = prevState[y][x-1];
                prevState[y][x-1] = state[y][x-1];
                state[y][x-1] = (state[y][x-1] + CLICK_EFFECT) % QuickNet.getNumUniqueStatesPerFeature();
            } if(x + 1 < WIDTH) {
                ancientState[y][x+1] = prevState[y][x+1];
                prevState[y][x+1] = state[y][x+1];
                state[y][x+1] = (state[y][x+1] + CLICK_EFFECT) % QuickNet.getNumUniqueStatesPerFeature();
            }
            clickPath[y][x] = (clickPath[y][x] + 1) % 2;
        }
        
        final int valueAt(final int x, final int y) {
            return state[y][x];
        }
        
        final int lastValueAt(final int x, final int y) {
            return prevState[y][x];
        }
        
        final int ancientValueAt(final int x, final int y) {
            return ancientState[y][x];
        }
        
        final int clickPathAt(final int x, final int y) {
            return clickPath[y][x];
        }
    }
    
    public final void print() {
        final StringBuilder builder
                = new StringBuilder("[");
        
        for(int y = 0; y < HEIGHT; y ++) {
            for(int x = 0; x < WIDTH; x ++) {
                builder.append(STATE.valueAt(x, y)).append(x < WIDTH - 1 ? ", " : "]\n");
            }
            builder.append(y < HEIGHT - 1 ? "[" : "");
        }
        
        System.out.println(builder);
    }
    
}
