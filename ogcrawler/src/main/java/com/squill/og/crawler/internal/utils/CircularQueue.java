package com.squill.og.crawler.internal.utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class CircularQueue<E> extends AbstractCollection<E>
    implements Queue<E>, BoundedCollection<E>, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7200919378938868453L;

    private transient E[] elements;

    private transient int start = 0;

    private transient int end = 0;

    private transient boolean full = false;

    private final int maxElements;

    public CircularQueue() {
        this(32);
    }

    @SuppressWarnings("unchecked")
    public CircularQueue(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        elements = (E[]) new Object[size];
        maxElements = elements.length;
    }

    public CircularQueue(final Collection<? extends E> coll) {
        this(coll.size());
        addAll(coll);
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        for (final E e : this) {
            out.writeObject(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        elements = (E[]) new Object[maxElements];
        final int size = in.readInt();
        for (int i = 0; i < size; i++) {
            elements[i] = (E) in.readObject();
        }
        start = 0;
        full = size == maxElements;
        if (full) {
            end = 0;
        } else {
            end = size;
        }
    }

    @Override
    public int size() {
        int size = 0;

        if (end < start) {
            size = maxElements - start + end;
        } else if (end == start) {
            size = full ? maxElements : 0;
        } else {
            size = end - start;
        }

        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return false;
    }

    public boolean isAtFullCapacity() {
        return size() == maxElements;
    }

    public int maxSize() {
        return maxElements;
    }

    @Override
    public void clear() {
        full = false;
        start = 0;
        end = 0;
        Arrays.fill(elements, null);
    }

    @Override
    public boolean add(final E element) {
        if (null == element) {
            return false;
        }

        if (isAtFullCapacity()) {
            remove();
        }

        elements[end++] = element;

        if (end >= maxElements) {
            end = 0;
        }

        if (end == start) {
            full = true;
        }

        return true;
    }

    public E get(final int index) {
        final int sz = size();
        if (index < 0 || index >= sz) {
            throw new NoSuchElementException(
                    String.format("The specified index (%1$d) is outside the available range [0, %2$d)",
                                  Integer.valueOf(index), Integer.valueOf(sz)));
        }

        final int idx = (start + index) % maxElements;
        return elements[idx];
    }

    public boolean offer(E element) {
        return add(element);
    }

    public E poll() {
        if (isEmpty()) {
            return null;
        }
        return remove();
    }

    public E element() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        return peek();
    }

    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[start];
    }

    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        final E element = elements[start];
        if (null != element) {
            elements[start++] = null;

            if (start >= maxElements) {
                start = 0;
            }
            full = false;
        }
        return element;
    }

    private int increment(int index) {
        index++;
        if (index >= maxElements) {
            index = 0;
        }
        return index;
    }

    private int decrement(int index) {
        index--;
        if (index < 0) {
            index = maxElements - 1;
        }
        return index;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int index = start;
            private int lastReturnedIndex = -1;
            private boolean isFirst = full;

            public boolean hasNext() {
                return isFirst || index != end;
            }

            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                isFirst = false;
                lastReturnedIndex = index;
                index = increment(index);
                return elements[lastReturnedIndex];
            }

            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }

                // First element can be removed quickly
                if (lastReturnedIndex == start) {
                    CircularQueue.this.remove();
                    lastReturnedIndex = -1;
                    return;
                }

                int pos = lastReturnedIndex + 1;
                if (start < lastReturnedIndex && pos < end) {
                    // shift in one part
                    System.arraycopy(elements, pos, elements, lastReturnedIndex, end - pos);
                } else {
                    // Other elements require us to shift the subsequent elements
                    while (pos != end) {
                        if (pos >= maxElements) {
                            elements[pos - 1] = elements[0];
                            pos = 0;
                        } else {
                            elements[decrement(pos)] = elements[pos];
                            pos = increment(pos);
                        }
                    }
                }

                lastReturnedIndex = -1;
                end = decrement(end);
                elements[end] = null;
                full = false;
                index = decrement(index);
            }

        };
    }

}