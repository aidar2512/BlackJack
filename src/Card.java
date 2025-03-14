public class Card {
    private int rank;
    private int suit;

    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final String[] SUITS = {"H", "D", "S", "C"};
    public static final int ACE = 12;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getValue() {
        if (rank >= 9) {
            return 10;
        }
        if (rank == ACE) {
            return 11; // Ace
        }
        return rank + 2;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return RANKS[rank] + SUITS[suit];
    }
}
