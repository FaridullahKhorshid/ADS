package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.function.BinaryOperator;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> ordening;   // the comparator that has been used with the latest sort
    protected int nSorted;                      // the number of items that have been ordered by barcode in the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given ordening comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> ordening ) {
        super();
        this.ordening = ordening;
        this.nSorted = 0;
    }

    public Comparator<? super E> getOrdening() {
        return this.ordening;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.ordening = c;
        this.nSorted = this.size();
    }

    // TODO override the ArrayList.add(index, item), ArrayList.remove(index) and Collection.remove(object) methods
    //  such that they sustain the representation invariant of OrderedArrayList
    //  (hint: only change nSorted as required to guarantee the representation invariant, do not invoke a sort)

    @Override
    public boolean add(E element) {
        boolean incrementSorted = shouldIncrementSortedBeforeAdd(this.size(), element);
        if (incrementSorted) {
            this.nSorted++;
        }

        return super.add(element);
    }

    @Override
    public void add(int index, E element) {
        boolean incrementSorted = shouldIncrementSortedBeforeAdd(this.size(), element);
        if (incrementSorted) {
            this.nSorted++;
        }

        super.add(index, element);
    }

    /**
     * Checks if nSorted should be incremented to sustain the invariant if element were to be inserted at index
     * using this.ordening to compare
     *
     * @param index the index were the element is to be inserted
     * @param element the element that is to be inserted
     * @return boolean
     */
    private boolean shouldIncrementSortedBeforeAdd(int index, E element){
        if (this.isEmpty()) {
            // List is sorted by definition if it contains only 1 item
            return true;
        }

        if (this.size() == this.nSorted && this.ordening.compare(element, this.get(this.size() - 1)) >= 0) {
            // All items were sorted AND the item we're adding is greater than the last item in the list
            // OR it is equal to the last item (duplicate), therefore the list remains sorted
            return true;
        }

        if (index < nSorted && this.ordening.compare(element, this.get(index - 1)) >= 0 && this.ordening.compare(element, this.get(index)) <= 0) {
            // We're inserting an element in the sorted part of the list
            // and the element at index - 1 is smaller than or equal to the new element
            // and the element at index (which will be pushed to index + 1) is greater than or equal to the new element
            return true;
        }

        return false;
    }


    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.ordening);
        }
    }

    @Override
    public int indexOf(Object item) {
        if (item != null) {
            return indexOfByIterativeBinarySearch((E)item);
        } else {
            return -1;
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.ordening
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {

        // TODO implement an iterative binary search on the sorted section of the arrayList, 0 <= index < nSorted
        //   to find the position of an item that matches searchItem (this.ordening comparator yields a 0 result)

        int low = 0;
        int high = this.nSorted - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparatorResult = this.ordening.compare(searchItem, this.get(mid));
            if (comparatorResult < 0) {
                high = mid - 1;
            } else if (comparatorResult > 0) {
                low = mid + 1;
            } else {
                return mid;
            }
        }

        // TODO if no match was found, attempt a linear search of searchItem in the section nSorted <= index < size()
        return linearSearch(searchItem);
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.ordening
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        int index = indexofByRecursiveBinarySearch(searchItem, 0, nSorted - 1);
        if (index != -1) { // Binary search found the item
            return index;
        }

        return linearSearch(searchItem);
    }

    /**
     * Recursive implementation of binary search, using the this.ordening comparator for comparison and equality
     * @param searchItem
     * @param low
     * @param high
     * @return
     */
    private int indexofByRecursiveBinarySearch(E searchItem, int low, int high) {
        if (low > high) {
            return -1;
        }

        int mid = low + (high - low) / 2;
        int comparatorResult = this.ordening.compare(searchItem, this.get(mid));
        if (comparatorResult < 0) {
            return indexofByRecursiveBinarySearch(searchItem, low, mid - 1);
        } else if (comparatorResult > 0) {
            return indexofByRecursiveBinarySearch(searchItem, mid + 1, high);
        } else {
            return mid;
        }
    }

    private int linearSearch(E searchItem) {
        ListIterator<E> iterator = this.listIterator(nSorted);
        while(iterator.hasNext()) {
            if (this.ordening.compare(iterator.next(), searchItem) == 0) {
                return iterator.previousIndex();
            }
        }

        return -1;
    }

    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     * @param newItem
     * @param merger    a function that takes two items and returns an item that contains the merged content of
     *                  the two items according to some merging rule.
     *                  e.g. a merger could add the value of attribute X of the second item
     *                  to attribute X of the first item and then return the first item
     * @return  whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            // TODO retrieve the matched item and
            //  replace the matched item in the list with the merger of the matched item and the newItem

            return false;
        }
    }
}
