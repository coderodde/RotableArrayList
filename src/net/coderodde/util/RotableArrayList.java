package net.coderodde.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
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
        if (coll.isEmpty()) {
            return false;
        }
        
        super.addAll(finger, coll);
        finger += coll.size();
        return true;
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends E> coll) {
        if (coll.isEmpty()) {
            return false;
        }
        
        int actualIndex = finger + index;
        
        if (actualIndex >= size()) {
            actualIndex %= size();
            finger += coll.size();
        } 
        
        super.addAll(actualIndex, coll);
        return true;
    }
    
    @Override
    public E remove(int index) {
        checkRemovalIndex(index);
        E ret = this.get(index);
        super.remove((finger + index) % size());
        
        if (finger + index > size()) {
            --finger;
        }
        
        return ret;
    }
    
    @Override
    public void clear() {
        super.clear();
        finger = 0;
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
        return new Iterator<E>() {

            private final ListIterator<E> listIterator = listIterator(0);
            
            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }

            @Override
            public E next() {
                return listIterator.next();
            }
            
            @Override
            public void remove() {
                listIterator.remove();
            }
        };
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
        Object[] array = new Object[size()];
        int index = 0;
        
        for (E element : this) {
            array[index++] = element;
        }
        
        return array;
    }

    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < size()) {
            a = Arrays.copyOf(a, size());
        }
        
        int index = 0;
        
        for (Object element : this) {
            a[index++] = (E) element;
        }
        
        if (a.length > size()) {
            a[size()] = null;
        }
        
        return a;
    }

    @Override
    public boolean remove(Object o) {
        int size = size();

        for (int index = 0; index < size; ++index) {
            if (Objects.equals(o, get(index))) {
                remove(index);
                // size = 10, finger = 7, index = 4
                if (index + finger >= size()) {
                    --finger;
                }
                
                return true;
            }
        }

        return false;
    }
    
    @Override
    public boolean removeAll(Collection<?> coll) {
        if (coll.isEmpty()) {
            return false;
        }
        
        Set<?> set = (coll instanceof HashSet) ? 
                                 (Set<?>) coll : 
                                 new HashSet<>(coll);
        
        Iterator<E> iterator = this.iterator();
        
        while (iterator.hasNext()) {
            E current = iterator.next();
            
            if (set.contains(current)) {
                iterator.remove();
            }
        }
        
        return true;
    }
    
    @Override
    public boolean retainAll(Collection<?> coll) {
        if (coll.isEmpty()) {
            return false;
        }
        
        Set<?> set = (coll instanceof HashSet) ? 
                                 (Set<?>) coll : 
                                 new HashSet<>(coll);
        
        Iterator<E> iterator = iterator();
        
        while (iterator.hasNext()) {
            E current = iterator.next();
            
            if (!set.contains(current)) {
                iterator.remove();
            }
        }
        
        return true;
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
        private int expectedModCount = RotableArrayList.super.modCount;
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
            checkConcurrentModification();
            
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
            checkConcurrentModification();
            
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
            
            checkConcurrentModification();
            E ret = RotableArrayList.this.remove(indexOfIteratedElement);
            indexOfIteratedElement = -1;
            expectedModCount = RotableArrayList.super.modCount;
        
            if (lastMoveWasNext) {
                index--;
            }
        }

        @Override
        public void set(E e) {
            if (indexOfIteratedElement == -1) {
                throw new IllegalStateException("There is no current element.");
            }
            
            checkConcurrentModification();
            RotableArrayList.this.set(indexOfIteratedElement, e);
            expectedModCount = RotableArrayList.super.modCount;
        }

        @Override
        public void add(E e) {
            checkConcurrentModification(); 
            RotableArrayList.this.add(nextIndex(), e);
            index++;
            indexOfIteratedElement = -1;
            expectedModCount = RotableArrayList.super.modCount;
        }
        
        private void checkConcurrentModification() {
            if (expectedModCount != RotableArrayList.super.modCount) {
                throw new ConcurrentModificationException(
                        "Expected mod count: " + expectedModCount + ", " + 
                        "actual mod count: " + RotableArrayList.super.modCount);
            }
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
