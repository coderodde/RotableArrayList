package net.coderodde.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;

/**
 * This class implements a rotable list. Pushing to the front or the end of this
 * list runs in constant amortized time. Rotation runs in constant time.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Mar 24, 2016)
 */
public class RotableArrayList<E> extends ArrayList<E> {

    private int finger;

    @Override
    public E get(int index) {
        checkAccessIndex(index);
        return super.get((index + finger) % size());
    }

    @Override
    public E set(int index, E element) {
        checkAccessIndex(index);
        E ret = get(index);
        super.set((index + finger) % size(), element);
        return ret;
    }

    @Override
    public void add(int index, E element) {
        checkAdditionIndex(index);
        super.add((index + finger) % (size() + 1), element);
    }

    @Override
    public boolean add(E element) {
        add(size(), element);
        return true;
    }
    
    @Override
    public boolean addAll(Collection<? extends E> coll) {
        boolean modified = false;
        
        for (E element : coll) {
            int index = finger - 1;
            
            if (index < 0) {
                finger = size();
            }
            
            super.add(finger, element);
        }
        
        return modified;
    }
    
    @Override
    public E remove(int index) {
        checkRemovalIndex(index);
        E ret = get(index);
        super.remove((index + finger) % size());
        return ret;
    }

    @Override
    public int indexOf(Object o) {
        int size = size();

        for (int index = 0; index < size; ++index) {
            if (Objects.equals(o, get(index))) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int index = size() - 1; index >= 0; --index) {
            if (Objects.equals(o, get(index))) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        finger = 0;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new RotableListIterator(index);
    }

    @Override
    public Spliterator<E> spliterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        int size = size();

        for (int index = 0; index < size; ++index) {
            if (Objects.equals(o, get(index))) {
                remove(index);
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        int size = size();

        for (int index = 0; index < size; ++index) {
            sb.append(get(index));

            if (index < size - 1) {
                sb.append(", ");
            }
        }

        return sb.append("]").toString();
    }

    public void rotate(int offset) {
        finger -= offset;
        finger %= size();

        if (finger < 0) {
            finger += size();
        }
    }

    private void checkAccessIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "The access index is negative: " + index + ".");
        }

        if (index >= size()) {
            throw new IndexOutOfBoundsException(
                    "The access index is too large: " + index + "." + 
                    "The size of the list is " + size() + ".");
        }
    }

    private void checkAdditionIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "The access index is negative: " + index + ".");
        }

        if (index > size()) {
            throw new IndexOutOfBoundsException(
                    "The addition index is too large: " + index + "." + 
                    "The size of the list is " + size() + ".");
        }
    }

    private void checkRemovalIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "The removal index is negative: " + index + ".");
        }

        if (index >= size()) {
            throw new IndexOutOfBoundsException(
                    "The removal index is too large: " + index + "." + 
                    "The size of the list is " + size() + ".");
        }
    }

    private final class RotableListIterator implements ListIterator<E> {

        // Index is an arrow that points between two array elements:
        // array[index - 1] and array[index].
        private int index;
        private int indexOfIteratedElement = -1;
        private boolean lastMoveWasNext;
        
        public RotableListIterator(int index) {
            this.index = index;
        }
        
        @Override
        public boolean hasNext() {
            return index < RotableArrayList.this.size();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException(
                        "No next element in this iterator.");
            }
        
            indexOfIteratedElement = index;
            lastMoveWasNext = true;
            return (E) RotableArrayList.this.get(index++);
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException(
                        "No previous element in this iterator.");
            }
        
            indexOfIteratedElement = --index;
            lastMoveWasNext = false;
            return (E) RotableArrayList.this.get(index);
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (indexOfIteratedElement == -1) {
                throw new IllegalStateException(
                        "There is no element to remove.");
            }
            
            RotableArrayList.this.remove(indexOfIteratedElement);
            indexOfIteratedElement = -1;
        
            if (lastMoveWasNext) {
                index--;
            }
        }

        @Override
        public void set(E e) {
            if (indexOfIteratedElement == -1) {
                throw new IllegalStateException("There is no current element.");
            }
            
            RotableArrayList.this.set(indexOfIteratedElement, e);
        }

        @Override
        public void add(E e) {
            RotableArrayList.this.add(nextIndex(), e);
            index++;
            indexOfIteratedElement = -1;
        }
    }
    
    public static void main(String[] args) {
        RotableArrayList<Integer> list = new RotableArrayList<>();

        for (int i = 0; i < 5; ++i) {
            list.add(i);
        }

        System.out.println("Rotating to the right:");

        for (int i = 0; i < list.size(); ++i) {
            System.out.println(list);
            list.rotate(1);
        }

        System.out.println("Rotating to the left:");

        for (int i = 0; i < list.size(); ++i) {
            System.out.println(list);
            list.rotate(-1);
        }
    }
}
