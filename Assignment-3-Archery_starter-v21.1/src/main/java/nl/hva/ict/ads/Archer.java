package nl.hva.ict.ads;

import java.util.Arrays;

public class Archer {
    public static int MAX_ARROWS = 3;
    public static int MAX_ROUNDS = 10;


    private static int nextId = 135788;
    private final int id;
    private String firstName;
    private String lastName;

    // Will int[] suffice for scores?
    private int[] scores = new int[30];

    /**
     * Constructs a new instance of Archer and assigns a unique id to the instance.
     * Each new instance should be assigned a number that is 1 higher than the last one assigned.
     * The first instance created should have ID 135788;
     *
     * @param firstName the archers first name.
     * @param lastName the archers surname.
     */
    public Archer(String firstName, String lastName) {
        this.id = nextId;
        nextId++;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Registers the points for each of the three arrows that have been shot during a round.
     *
     * @param round the round for which to register the points. First round has number 1.
     * @param points the points shot during the round, one for each arrow.
     */
    public void registerScoreForRound(int round, int[] points) {
        int index = (round - 1) * 3;
        for (int point : points) {
            this.scores[index] = point;
            index++;
        }
    }


    /**
     * Calculates/retrieves the total score of all arrows across all rounds
     * @return
     */
    public int getTotalScore() {
        return Arrays.stream(this.scores).sum();
    }

    /**
     * compares the scores/id of this archer with the scores/id of the other archer according to
     * the scoring scheme: highest total points -> least misses -> earliest registration
     * The archer with the lowest id has registered first
     * @param other     the other archer to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestTotalScoreWithLeastMissesAndLowestId(Archer other) {
        // TODO compares the scores/id of this archer with the other archer
        //  and return the result according to Comparator conventions
        if (this.equals(other)) {
            return 0;
        }

        if (this.getTotalScore() > other.getTotalScore()) {
            return 1;
        }

        if (this.getTotalMisses() < other.getTotalMisses()) {
            return 1;
        }

        if (this.getId() < other.getId()) {
            return 1;
        }

        return -1;
    }

    public int getTotalMisses() {
        return (int) Arrays.stream(this.scores)
                           .filter(x -> x == 0)
                           .count();
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return this.id + " (" + this.getTotalScore() + ") " + this.getFirstName() + " " + this.getLastName();
    }
}
