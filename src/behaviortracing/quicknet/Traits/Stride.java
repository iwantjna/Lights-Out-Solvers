package behaviortracing.quicknet.Traits;

/**
 *
 * @author MijitR
 */
final class Stride {
    private final int stride;
    protected Stride(final int stride) {
        this.stride = stride;
    } protected int getStride() {
        return this.stride;
    }
}
