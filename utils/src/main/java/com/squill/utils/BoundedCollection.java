package com.squill.utils;
import java.util.Collection;

/**
 * Defines a collection that is bounded in size.
 * <p>
 * The size of the collection can vary, but it can never exceed a preset
 * maximum number of elements. This interface allows the querying of details
 * associated with the maximum number of elements.
 *
 * @see CollectionUtils#isFull
 * @see CollectionUtils#maxSize
 *
 * @since 3.0
 * @version $Id: BoundedCollection.java 1477779 2013-04-30 18:55:24Z tn $
 */
public interface BoundedCollection<E> extends Collection<E> {

    /**
     * Returns true if this collection is full and no new elements can be added.
     *
     * @return <code>true</code> if the collection is full
     */
    boolean isFull();

    /**
     * Gets the maximum size of the collection (the bound).
     *
     * @return the maximum number of elements the collection can hold
     */
    int maxSize();

}