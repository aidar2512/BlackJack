import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public Card getLastAdded() {
        return cards.get(cards.size() - 1); // Returns the last added card
    }

    public int getValue() {
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            value += card.getValue();
            if (card.getValue() == 11) { // Ace is counted as 11 initially
                aces++;
            }
        }

        while (value > 21 && aces > 0) {
            value -= 10; // Convert an Ace from 11 to 1
            aces--;
        }

        return value;
    }


    public Card removeRandomCard() {
        if (cards.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * cards.size());
        return cards.remove(randomIndex);
    }

    public boolean busted() {
        return getValue() > 21;
    }

    public boolean hasBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    // Method to check if the hand can be split
    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getRank() == cards.get(1).getRank();
    }

    @Override
    public String toString() {
        StringBuilder handString = new StringBuilder();
        for (Card card : cards) {
            handString.append(card).append(" ");
        }
        return handString.toString() + "(" + getValue() + ")";
    }

    public void printDealerHand() {
        System.out.println("X " + cards.get(1));
    }

    // For splitting the hand into two hands
    public Hand[] split() {
        if (!canSplit()) throw new IllegalStateException("Cannot split a non-pair hand");

        Hand firstHand = new Hand();
        firstHand.addCard(cards.remove(1));

        return new Hand[]{this, firstHand};
    }

    public ArrayList<Card> getCards() {
        return cards;
    }
}
