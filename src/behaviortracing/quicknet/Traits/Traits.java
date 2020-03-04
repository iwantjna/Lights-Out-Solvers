package behaviortracing.quicknet.Traits;

/**
 *
 * @author MijitR
 */
public class Traits implements Cloneable {

    private final Size AtomicSize;
    
    private final Stride AtomicStride;
    
    private final Padding AtomicPadding;
    
    private final Depth AtomicDepth;
    
    private final Activations activations;
    
    public Traits(final int size, final Activations activations) {
        this(size, activations, 0);
    }
    
    public Traits(final int size, final Activations activations, final int stride) {
        this(size, activations, stride, 0);
    }
    
    public Traits(final int size, final Activations activations,
            final int stride, final int padding) {
        this(size, activations, stride, padding, 1);
    }
    
    public Traits(final int size, final Activations activations,
            final int stride, final int padding, final int depth) {
        this.AtomicSize = new Size(size);
        this.activations = Activations.valueOf(activations.name());
        this.AtomicStride = new Stride(stride);
        this.AtomicPadding = new Padding(padding);
        this.AtomicDepth = new Depth(depth);
    }
        
    public final int size() {
        return AtomicSize.getSize();
    }
    
    public final String activations() {
        return this.activations.name();
    }
    
    public final int stride() {
        return this.AtomicStride.getStride();
    }
    
    public final int padding() {
        return this.AtomicPadding.getPadding();
    }
    
    public final int depth() {
        return this.AtomicDepth.getDepth();
    }
    
    @Override
    public final Traits clone() {
        return new Traits(this.AtomicSize.getSize(), this.activations, this.AtomicStride.getStride(), this.AtomicPadding.getPadding(), this.AtomicDepth.getDepth());
    }
    
    @Override
    public final String toString() {
        return "Size: "
                .concat(String.valueOf(this.AtomicSize.getSize()))
                    .concat(" -> Activation: ")
            .concat(this.activations()).concat(" -> Stride: ")
                .concat(String.valueOf(this.AtomicStride.getStride()))
            .concat(" -> Padding: " + this.AtomicPadding.getPadding())
                .concat(" -> Depth: ")
            .concat(String.valueOf(this.AtomicDepth.getDepth()));
    }
    
}
