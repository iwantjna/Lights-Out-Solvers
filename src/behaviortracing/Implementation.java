package behaviortracing;

import behaviortracing.quicknet.QuickNet;

/**
 *
 * @author MijitR
 */
public interface Implementation {
    public abstract boolean implement(final QuickNet net) throws InterruptedException;
}
