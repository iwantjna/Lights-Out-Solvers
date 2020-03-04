package behaviortracing.quicknet.Traits;

/**
 *
 * @author MijitR
 */
final class Depth {
    private final int depth;
    protected Depth(final int depth){
        this.depth = depth;
    } protected int getDepth() {
        return this.depth;
    }
}
