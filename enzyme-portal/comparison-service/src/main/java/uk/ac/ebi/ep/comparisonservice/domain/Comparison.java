package uk.ac.ebi.ep.base.comparison;

import java.util.Map;

/**
 * Basic comparison of objects to state if they differ or not.
 * 
 * @author rafa
 * @since 1.1.0
 * 
 */
public interface Comparison<T> {

    /**
     * Do these objects differ?
     * 
     * @return <code>true</code> if there is something different.
     */
    public boolean isDifferent();

    /**
     * Getter for the compared items.
     * 
     * @return the compared items.
     */
    public T[] getCompared();

    /**
     * Retrieves the underlying comparisons made in order to know if the objects
     * differ.
     * 
     * @return A map of keywords (representing the compared items) to
     *      comparisons.
     */
    public Map<String, Comparison<?>> getSubComparisons();
}
