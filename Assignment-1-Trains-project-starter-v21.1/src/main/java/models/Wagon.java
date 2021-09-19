package models;

public abstract class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return this.nextWagon != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return this.previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it, if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon tail = this;
        while (tail.hasNextWagon()) {
            tail = tail.getNextWagon();
        }

        return tail;
    }

    /**
     * @return the length of the tail of wagons towards the end of the sequence
     * excluding this wagon itself.
     */
    public int getTailLength() {
        int len = 0;
        Wagon tail = this;

        while (tail.hasNextWagon()) {
            len++;
            tail = tail.getNextWagon();
        }

        return len;
    }

    /**
     * Attaches the tail wagon behind this wagon, if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     */
    public void attachTail(Wagon tail) {


        // verify the exceptions
        if (this.hasNextWagon()) {
            throw new IllegalStateException("Cannot attach a tail (" + tail.toString() + ") to (" + this.toString() + "); wagon already has a tail attached");
        }

        if (tail.hasPreviousWagon()) {
            throw new IllegalStateException("The tail(" + tail.toString() + ") already has a wagon attached in front(" + tail.previousWagon.toString() + ") of it: " + this.toString());
        }

        // attach the tail wagon to this wagon (sustaining the invariant propositions).
        this.nextWagon = tail;
        tail.setFront(this); // TODO implement attachFront function
    }

    private void setFront(Wagon front) {
        this.previousWagon = front;
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if (this.nextWagon == null) {
            return null;
        }

        Wagon tail = this.nextWagon;
        this.nextWagon = null;
        tail.previousWagon = null;

        return tail;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        if (this.previousWagon == null) {
            return null;
        }

        Wagon frontWagon = this.previousWagon;
        this.previousWagon = null;
        frontWagon.nextWagon = null;

        return frontWagon;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {

        //  detach any existing connections that will be rearranged
        //  attach this wagon to its new predecessor front (sustaining the invariant propositions).

        // detach front from the current wagon
        if (this.hasPreviousWagon())
            this.detachFront();
        // detach tail from the front of the current wagon
        if (front.hasNextWagon())
            front.detachTail();

        front.attachTail(this);
//        this.setFront(front);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if it exists.
     */
    public void removeFromSequence() {

        // detach tail and front from current wagon
        Wagon tail = this.detachTail();
        Wagon front = this.detachFront();

        // attach to the front if the there is tail of the current wagon
        if (tail != null && front != null) {
            front.attachTail(tail);
            tail.setFront(front);
        }
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // TODO provide an iterative implementation,
        //   using attach- and detach methods of this class


        /* Algorithm is as follows:
         *
         * Set two pointers: one to the second-to-last wagon, one to the last wagon
         * Detach the last wagon and make it the first
         * Attach the second-to-last wagon to the new first wagon
         * Move the last pointer forwards
         * Move the second-to-last pointer backwards
         * Detach the tail of the second-to-last wagon
         * Attach the second-to-last wagon to the tail of the last wagon
         * Repeat until the we've iterated backwards over the entire sequence
         */

        if (this.hasNextWagon()) {


            Wagon last = this.getLastWagonAttached();
            Wagon first = last;
            Wagon prev = last.getPreviousWagon();


            if (this.hasPreviousWagon()) {
                Wagon front = this.getPreviousWagon();
                last.reAttachTo(front);
            }


            while (prev != null) {

                Wagon prevWagon = prev;
                prev = prev.getPreviousWagon();

                prevWagon.detachFront();
                prevWagon.detachTail();

                last.attachTail(prevWagon);
                last = last.getNextWagon();
            }


            if (last != null) {
                this.detachFront();
                this.detachTail();

                last.attachTail(this);
            }

            return first;
        }

        return this;
    }

    @Override
    public String toString() {
        return "[Wagon-" + id + "]";
    }

    // TODO
}
