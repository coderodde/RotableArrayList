package net.coderodde.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class RotableArrayListTest {

    private final RotableArrayList<Integer> list = new RotableArrayList<>();

    @Before
    public void before() {
        list.clear();
    }

    private void load(int n) {
        for (int i = 0; i < n; ++i) {
            list.add(i);
        }
    }

    @Test
    public void testGet() {
        load(5);

        for (int i = 0; i < 5; ++i) {
            assertEquals(Integer.valueOf(i), list.get(i));
        }

        list.rotate(2);

        assertEquals(Integer.valueOf(3), list.get(0));
        assertEquals(Integer.valueOf(4), list.get(1));
        assertEquals(Integer.valueOf(0), list.get(2));
        assertEquals(Integer.valueOf(1), list.get(3));
        assertEquals(Integer.valueOf(2), list.get(4));

        list.rotate(-4);

        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(3), list.get(1));
        assertEquals(Integer.valueOf(4), list.get(2));
        assertEquals(Integer.valueOf(0), list.get(3));
        assertEquals(Integer.valueOf(1), list.get(4));

        list.rotate(-8);

        for (int i = 0; i < 5; ++i) {
            assertEquals(Integer.valueOf(i), list.get(i));
        }
    }

    @Test
    public void testSet() {
        load(4);

        list.set(0, 3);
        list.set(1, 2);
        list.set(2, 1);
        list.set(3, 0);

        assertEquals(Integer.valueOf(3), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(1), list.get(2));
        assertEquals(Integer.valueOf(0), list.get(3));
    }

    @Test
    public void testAdd_int_GenericType() {
        load(2);

        list.add(1, 10);
        list.add(0, 11);
        list.add(4, 12);

        assertEquals(Integer.valueOf(11), list.get(0));
        assertEquals(Integer.valueOf(0), list.get(1));
        assertEquals(Integer.valueOf(10), list.get(2));
        assertEquals(Integer.valueOf(1), list.get(3));
        assertEquals(Integer.valueOf(12), list.get(4));
    }

    @Test
    public void testAdd_GenericType() {
        load(2);

        list.add(10);

        assertEquals(Integer.valueOf(0), list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertEquals(Integer.valueOf(10), list.get(2));
    }

    @Test
    public void testRemove_int() {
        load(5);

        list.remove(4);
        list.remove(2);
        list.remove(0);

        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(3), list.get(1));

        list.clear();

        load(5);

        list.rotate(2);
        list.remove(0);

        assertEquals(Integer.valueOf(4), list.get(0));
        assertEquals(Integer.valueOf(0), list.get(1));
        assertEquals(Integer.valueOf(1), list.get(2));
        assertEquals(Integer.valueOf(2), list.get(3));
        
        list.clear();
        load(10);
        list.rotate(3);
        list.remove(Integer.valueOf(3));
    }

    @Test
    public void testIndexOf() {
        load(10);

        list.rotate(-3);
        assertEquals(1, list.indexOf(4));
    }

    @Test
    public void testLastIndexOf() {
        load(10);

        list.rotate(-3);
        assertEquals(8, list.lastIndexOf(1));
    }

    @Test
    public void testSort() {
        list.add(5);
        list.add(2);
        list.add(1);
        list.add(4);
        list.rotate(-1);
        list.sort(Integer::compare);
        assertEquals(Integer.valueOf(1), list.get(0));
    }

    @Test
    public void testRemove_Object() {
        load(5);

        list.rotate(-1);
        list.remove(Integer.valueOf(3));
        assertEquals(4, list.size());

        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(4), list.get(2));
        assertEquals(Integer.valueOf(0), list.get(3));
    }

    @Test
    public void testRotate() {
        load(10);

        list.rotate(2);

        assertEquals(Integer.valueOf(8), list.get(0));
        assertEquals(Integer.valueOf(9), list.get(1));
        assertEquals(Integer.valueOf(0), list.get(2));

        list.rotate(-5);

        assertEquals(Integer.valueOf(3), list.get(0));
        assertEquals(Integer.valueOf(4), list.get(1));
        assertEquals(Integer.valueOf(5), list.get(2));
    }
    
    @Test
    public void testListIteratorNextAndHasNext() {
        load(5);
        
        ListIterator<Integer> iterator = list.listIterator(2);
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(2), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(3), iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testListIteratorPreviousAndHasPrevious() {
        load(5);
        
        ListIterator<Integer> iterator = list.listIterator(2);
        
        assertTrue(iterator.hasPrevious());
        assertEquals(Integer.valueOf(1), iterator.previous());
        
        assertTrue(iterator.hasPrevious());
        assertEquals(Integer.valueOf(0), iterator.previous());
        
        assertTrue(iterator.hasNext());
        assertFalse(iterator.hasPrevious());
        
        try {
            iterator.previous();
            fail();
        } catch (NoSuchElementException ex) {
            
        }
    }
    
    @Test
    public void testListIteratorAdd() {
        List<Integer> list2 = new ArrayList<>();
        load(5);
        
        for (int i = 0; i < 5; ++i) {
            list2.add(i);
        }
        
        ListIterator<Integer> iter  = list.listIterator();
        ListIterator<Integer> iter2 = list2.listIterator();
        
        iter.add(10);
        iter.add(11);
        
        iter2.add(10);
        iter2.add(11);
        
        assertTrue(listsEqual(list, list2));
        
        try {
            iter.remove();
            fail("List should have thrown IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iter2.remove();
            fail("List should have thrown IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
    }
    
    @Test
    public void testListIteratorSet() {
        List<Integer> list2 = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        load(5);
        
        ListIterator<Integer> iter = list.listIterator(2);
        ListIterator<Integer> iter2 = list2.listIterator(2);
        
        try {
            iter2.set(10);
            fail("ListIterator.set should have thrown IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iter.set(10);
            fail("ListIterator.set should have thrown IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        iter.previous();
        iter2.previous();
        
        iter.set(10);
        iter2.set(10);
        
        assertTrue(listsEqual(list, list2));
    }
    
    @Test
    public void testListIteratorRemove() {
        List<Integer> list2 = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        load(5);
        
        ListIterator<Integer> iter = list.listIterator(2);
        ListIterator<Integer> iter2 = list2.listIterator(2);
        
        try {
            iter2.remove();
            fail("ListIterator.remove should have thrown " + 
                 "IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iter.remove();
            fail("ListIterator.remove should have thrown " +
                 "IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        iter.next();
        iter2.next();
        
        iter.remove();
        iter2.remove();
        
        assertTrue(listsEqual(list, list2));
        
        try {
            iter2.remove();
            fail("ListIterator.remove should have thrown " + 
                 "IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        try {
            iter.remove();
            fail("ListIterator.remove should have thrown " +
                 "IllegalStateException.");
        } catch (IllegalStateException ex) {
            
        }
        
        iter.next();
        iter2.next();
        iter.remove();
        iter2.remove();
        
        assertTrue(listsEqual(list, list2));
        
        iter.previous();
        iter2.previous();
        iter.remove();
        iter2.remove();
        
        assertTrue(listsEqual(list, list2));
    }
    
    @Test  
    public void testAddAll() {
        load(4);
        list.rotate(-2); // 2, 3, 0, 1
        list.addAll(Arrays.asList(4, 5, 6));
        
        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(3), list.get(1));
        assertEquals(Integer.valueOf(0), list.get(2));
        assertEquals(Integer.valueOf(1), list.get(3));
        assertEquals(Integer.valueOf(4), list.get(4));
        assertEquals(Integer.valueOf(5), list.get(5));
        assertEquals(Integer.valueOf(6), list.get(6));
        
        List<Integer> list2 = new ArrayList<>();
        
        assertFalse(list.addAll(Arrays.asList()));
        assertFalse(list2.addAll(Arrays.asList()));
        
        assertTrue(list.addAll(Arrays.asList(1)));
        assertTrue(list2.addAll(Arrays.asList(1)));
    }
    
    @Test
    public void testAddAllInt() {
        load(4);
        list.rotate(-2); // 2, 3, 0, 1
        list.addAll(1, Arrays.asList(4, 5, 6));
        
        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(4), list.get(1));
        assertEquals(Integer.valueOf(5), list.get(2));
        assertEquals(Integer.valueOf(6), list.get(3));
        assertEquals(Integer.valueOf(3), list.get(4));
        assertEquals(Integer.valueOf(0), list.get(5));
        assertEquals(Integer.valueOf(1), list.get(6));
       
        List<Integer> list2 = new ArrayList<>(list);
        
        assertFalse(list.addAll(1, Arrays.asList()));
        assertFalse(list2.addAll(1, Arrays.asList()));
        
        assertTrue(list.addAll(1, Arrays.asList(1)));
        assertTrue(list2.addAll(1, Arrays.asList(1)));
        
        list.clear();
        
        load(5);
        list.rotate(2); // 3, 4, 0, 1, 2
        list.addAll(3, Arrays.asList(10, 11));
        
        assertEquals(Integer.valueOf(3),  list.get(0));
        assertEquals(Integer.valueOf(4),  list.get(1));
        assertEquals(Integer.valueOf(0),  list.get(2));
        assertEquals(Integer.valueOf(10), list.get(3));
        assertEquals(Integer.valueOf(11), list.get(4));
        assertEquals(Integer.valueOf(1),  list.get(5));
        assertEquals(Integer.valueOf(2),  list.get(6));
        
        list.clear();
        load(5);
        list.rotate(2); // 3, 4, 0, 1, 2
        list.addAll(1, Arrays.asList(10, 11)); // 3, 10, 11, 4, 0, 1, 2
        
        assertEquals(Integer.valueOf(3),  list.get(0));
        assertEquals(Integer.valueOf(10),  list.get(1));
        assertEquals(Integer.valueOf(11),  list.get(2));
        assertEquals(Integer.valueOf(4), list.get(3));
        assertEquals(Integer.valueOf(0), list.get(4));
        assertEquals(Integer.valueOf(1),  list.get(5));
        assertEquals(Integer.valueOf(2),  list.get(6));
    }
    
    @Test
    public void testToArray() {
        load(5);
        
        list.rotate(3); // 2, 3, 4, 0, 1
        
        Object[] array = list.toArray();
        
        assertEquals(2, array[0]);
        assertEquals(3, array[1]);
        assertEquals(4, array[2]);
        assertEquals(0, array[3]);
        assertEquals(1, array[4]);
    }
    
    @Test
    public void testGenericToArray() {
        load(5);
        list.rotate(-2); // 2, 3, 4, 0, 1
        
        Integer[] array = new Integer[4];
        Integer[] result = list.toArray(array);
        
        assertTrue(array != result);
        assertEquals(5, result.length);
        
        assertEquals(Integer.valueOf(2), result[0]);
        assertEquals(Integer.valueOf(3), result[1]);
        assertEquals(Integer.valueOf(4), result[2]);
        assertEquals(Integer.valueOf(0), result[3]);
        assertEquals(Integer.valueOf(1), result[4]);
        
        array = new Integer[5];
        result = list.toArray(array);
        
        assertTrue(array == result);
        
        assertEquals(5, result.length);
        
        assertEquals(Integer.valueOf(2), result[0]);
        assertEquals(Integer.valueOf(3), result[1]);
        assertEquals(Integer.valueOf(4), result[2]);
        assertEquals(Integer.valueOf(0), result[3]);
        assertEquals(Integer.valueOf(1), result[4]);
        
        array = new Integer[6];
        result = list.toArray(array);
        
        assertTrue(array == result);
        assertEquals(6, array.length);
        
        assertEquals(Integer.valueOf(2), result[0]);
        assertEquals(Integer.valueOf(3), result[1]);
        assertEquals(Integer.valueOf(4), result[2]);
        assertEquals(Integer.valueOf(0), result[3]);
        assertEquals(Integer.valueOf(1), result[4]);
        assertNull(result[5]); // Cut off value.
    }
    
    @Test
    public void testRemoveAll() {
        load(5);
        list.rotate(3);
        Set<Integer> set = new HashSet<>(Arrays.asList(2, 3, 1));
        
        list.removeAll(set);
        
        assertEquals(Integer.valueOf(4), list.get(0));
        assertEquals(Integer.valueOf(0), list.get(1));
        
        list.clear();
        load(10);
        list.rotate(3); // 7, 8, 9, 0, 1, 2, 3, 4, 5, 6
        set.clear();
        set.addAll(Arrays.asList(0, 2, 3, 6, 9));
        list.removeAll(set);
        
        assertEquals(Integer.valueOf(7), list.get(0));
        assertEquals(Integer.valueOf(8), list.get(1));
        assertEquals(Integer.valueOf(1), list.get(2));
        assertEquals(Integer.valueOf(4), list.get(3));
        assertEquals(Integer.valueOf(5), list.get(4));
    }
    
    @Test
    public void testRetainAll() {
        load(5);
        list.rotate(3); // 2, 3, 4, 0, 1
        Set<Integer> set = new HashSet<>(Arrays.asList(2, 3, 1));
        
        list.retainAll(set); // 2, 3, 1
        
        assertEquals(Integer.valueOf(2), list.get(0));
        assertEquals(Integer.valueOf(3), list.get(1));
        assertEquals(Integer.valueOf(1), list.get(2));
        
        list.clear();
        load(10);
        list.rotate(3); // 7, 8, 9, 0, 1, 2, 3, 4, 5, 6
        set.clear();
        set.addAll(Arrays.asList(0, 2, 3, 6, 9));
        list.retainAll(set);
        
        assertEquals(Integer.valueOf(9), list.get(0));
        assertEquals(Integer.valueOf(0), list.get(1));
        assertEquals(Integer.valueOf(2), list.get(2));
        assertEquals(Integer.valueOf(3), list.get(3));
        assertEquals(Integer.valueOf(6), list.get(4));
    }
    
    private boolean listsEqual(List<Integer> list, List<Integer> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        
        for (int i = 0; i < list.size(); ++i) {
            if (!list.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        
        return true;
    }
}
