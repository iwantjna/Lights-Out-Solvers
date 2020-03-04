package behaviortracing.quicknet.Traits;

/**
 *
 * @author MijitR
 */
final class Padding {
    private final int padding;
    protected Padding(final int padding) {
        this.padding = padding;
    }
    protected final int getPadding() {
        return this.padding;
    }
}
