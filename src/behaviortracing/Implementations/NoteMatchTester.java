package behaviortracing.Implementations;

import behaviortracing.Implementation;
import behaviortracing.quicknet.QuickNet;

/**
 *
 * @author MijitR
 */
public class NoteMatchTester implements Implementation {
    
    private QuickNet net;/*

    @Override
    public boolean implement(final QuickNet net) {
        this.net = net;
        
        final AudioFormat af =
            new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
        final SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, Note.SAMPLE_RATE);
            line.start();
            for  (Note n : Note.values()) {
                play(line, n, 500);
                play(line, Note.REST, 10);
            }
            line.drain();
            line.close();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(NoteMatchTester.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        
        return true;
    }

    private static void play(SourceDataLine line, Note note, int ms) {
        ms = Math.min(ms, Note.SECONDS * 1000);
        int length = Note.SAMPLE_RATE * ms / 1000;
        int count = line.write(note.data(), 9, length);
    }
       
    enum Note {

    REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
    public static final int SECONDS = 2;
    private byte[] sin = new byte[SECONDS * SAMPLE_RATE];

    Note() {
        int n = this.ordinal();
        if (n > 0) {
            double exp = ((double) n - 1) / 12d;
            double f = 440d * Math.pow(2d, exp);
            for (int i = 0; i < sin.length; i++) {
                double period = (double)SAMPLE_RATE / f;
                double angle = 2.0 * Math.PI * i / period;
                sin[i] = (byte)(Math.sin(angle) * 127f);
            }
        }
    }

    public byte[] data() {
        return sin;
    }
}

    private static enum INSTRUMENT {PIANO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
            SEVEN(7), EIGHT(8), PERCUSSION(9); 
            
            final int channel;
            INSTRUMENT(final int channel) {
                this.channel = channel;
            }
    };*/

    @Override
    public boolean implement(QuickNet net) throws InterruptedException {
        
        return true;
    }
    
    
}
