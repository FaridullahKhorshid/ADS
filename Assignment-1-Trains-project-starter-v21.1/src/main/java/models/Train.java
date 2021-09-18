package models;

public class Train {
    private String origin;
    private String destination;
    private Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are useful in other methods */
    public boolean hasWagons() {
        return this.firstWagon != null;
    }

    public boolean isPassengerTrain() {
        return this.firstWagon instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return this.firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     * @param wagon the first wagon of a sequence of wagons to be attached
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int len = 0;
        Wagon tail = this.firstWagon;
        while (tail != null) {
            tail = tail.getNextWagon();
            len++;
        }

        return len;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (!this.hasWagons()) {
            return null;
        }

        Wagon tail = this.firstWagon;

        while (tail.hasNextWagon()) {
            tail = tail.getNextWagon();
        }

        return tail;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (this.isFreightTrain()) {
            return 0;
        }

        if (!this.hasWagons()) {
            return 0;
        }

        int seats = 0;
        PassengerWagon tail = (PassengerWagon) this.firstWagon;

        do {
            seats += tail.getNumberOfSeats();
            tail = (PassengerWagon) tail.getNextWagon();
        } while (tail != null);

        return seats;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        if (this.isPassengerTrain()) {
            return 0;
        }

        if (!this.hasWagons()) {
            return 0;
        }

        int maxWeight = 0;
        FreightWagon tail = (FreightWagon) this.firstWagon;

        do {
            maxWeight += tail.getMaxWeight();
            tail = (FreightWagon) tail.getNextWagon();
        } while (tail != null);

        return maxWeight;
    }

     /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        if (!this.hasWagons()) {
            return null;
        }

        if (position == 0) {
            return null;
        }

        Wagon wagon = this.firstWagon;
        // TODO write test to make sure this works when position = 1
        for (int i = 1; i < position; i++) {
            wagon = wagon.getNextWagon();

            if (wagon == null) {
                // We haven't reached the given position yet, but surpassed the last wagon of the train
                return null;
            }
        }

        return wagon;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon wagon = this.firstWagon;

        while(wagon != null) {
            if (wagon.getId() == wagonId) {
                return wagon;
            }
            wagon = wagon.getNextWagon();
        }

        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return
     */
    public boolean canAttach(Wagon wagon) {
        if (wagon == null) {
            return false; // We can always attach nothing
        }

        if (this.isPassengerTrain() && !(wagon instanceof PassengerWagon)) {
            return false;
        }

        if (this.isFreightTrain() && !(wagon instanceof FreightWagon)) {
            return false;
        }

        int numAlreadyAttached = this.getNumberOfWagons();
        int lenToAttach = 0;
        while (wagon != null) {
            lenToAttach += 1;
            wagon = wagon.getNextWagon();
        }

        return this.engine.getMaxWagons() >= numAlreadyAttached + lenToAttach;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param wagon  the first wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        if (!canAttach(wagon)) {
            return false;
        }

        if (!this.hasWagons()) {
            this.setFirstWagon(wagon);
            return true;
        }

        if (wagon.hasPreviousWagon()) {
            return false;
        }

        Wagon rear = this.getLastWagonAttached();
        rear.attachTail(wagon);

        return true;
    }


    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        if (!canAttach(wagon)) {
            return false;
        }

        if (wagon.hasPreviousWagon()) {
            return false;
        }

        if (!this.hasWagons()) {
            // No wagons attached yet, so we can simply attach the front of the sequence as our first wagon
            this.setFirstWagon(wagon);
            return true;
        }

        Wagon oldFirstWagon = this.firstWagon;

        // Attach the wagon at the front of the sequence as new first wagon
        this.setFirstWagon(wagon);

        // Reattach the old first wagon (and its tail) at the back
        this.attachToRear(oldFirstWagon);

        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        if (!canAttach(wagon)) {
            return false;
        }

        Wagon oldPosition = this.findWagonAtPosition(position);
        if (oldPosition == null) {
            return false; // given position is invalid
        }

        // Get the tail of the sequence we want to insert
        Wagon tailOfNewSequence = wagon.getLastWagonAttached();

        // Attach the new sequence to the wagon before the given position
        // (So the front of the sequence will be *at* the given position)
        wagon.reAttachTo(oldPosition.getPreviousWagon());

        // Reattach the sequence that we removed to attach the sequence in the given position at the end
        // of the inserted sequence.
        tailOfNewSequence.attachTail(oldPosition);

        return true;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId
     * @param toTrain
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        Wagon wagon = this.findWagonById(wagonId);

        if (wagon == null) {
            // No wagon with wagonId could be found
            return false;
        }

        if (!toTrain.canAttach(wagon)) {
            return false;
        }

        Wagon previous = wagon.getPreviousWagon();
        Wagon next = wagon.getNextWagon();

        if (previous != null && next == null) {
            // We're removing the last wagon of this train
            wagon.detachFront();
            previous.detachTail();
            toTrain.attachToRear(wagon);
            return true;
        }

        if (previous == null && next != null) {
            // We're removing the first wagon of this train and it has a tail
            next.detachFront();
            wagon.detachTail();
            this.setFirstWagon(next);
            toTrain.attachToRear(wagon);
            return true;
        }

        if (previous == null && next == null) {
            // We're removing the first wagon of this train and it has no tail
            toTrain.attachToRear(wagon);
            this.setFirstWagon(null);
            return true;
        }

        // We're removing a wagon somewhere in the middle of this train
        wagon.detachFront();
        wagon.detachTail();
        previous.detachTail();
        next.detachFront();
        next.reAttachTo(previous);
        toTrain.attachToRear(wagon);
        return true;
     }

    /**
     * Tries to split this train before the given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position
     * @param toTrain
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        Wagon wagonAtPosition = this.findWagonAtPosition(position);
        if (wagonAtPosition == null) {
            return false;
        }

        if (!toTrain.canAttach(wagonAtPosition)) {
            return false;
        }

        if (!toTrain.hasWagons()) {
            toTrain.setFirstWagon(wagonAtPosition);
            return true;
        }

        Wagon previous = wagonAtPosition.getPreviousWagon();
        if (previous != null) {
            previous.detachTail();
        }

        wagonAtPosition.detachFront();
        toTrain.getLastWagonAttached().attachTail(wagonAtPosition);

        return true;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        if (!this.hasWagons()) {
            return;
        }

        if (!this.firstWagon.hasNextWagon()) {
            return;
        }

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

        Wagon last = this.getLastWagonAttached();
        Wagon pointer = last.getPreviousWagon();
        pointer.detachTail();
        last.detachFront();
        this.setFirstWagon(last);

        while(pointer != null) {
            Wagon attachNext = pointer;
            pointer = pointer.getPreviousWagon();
            attachNext.detachTail();
            attachNext.detachFront();
            last.attachTail(attachNext);
            last = last.getNextWagon();
        }
    }

    // TODO

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[Loc-" + this.engine.getLocNumber() + "]");
        Wagon wagon = this.firstWagon;
        while (wagon != null) {
            output.append("[Wagon-" + wagon.getId() + "]");
            wagon = wagon.getNextWagon();
        }
        output.append(" with " + this.getNumberOfWagons() + " wagons from "
                + this.getOrigin() + " to " + this.getDestination());

        return output.toString();
    }

    public String getOrigin() {
        return this.origin;
    }

    public String getDestination() {
        return this.destination;
    }

//    public boolean attachToRear(PassengerWagon wagon) {
//        return this.attachToRear(wagon);
//    }
//    public boolean attachToRear(FreightWagon wagon) {
//        return this.attachToRear(wagon);
//    }
//    public boolean insertAtFront(FreightWagon wagon) {
//        return this.insertAtFront(wagon);
//    }
}
