import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getValue() {
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            value += card.getValue();
            if (card.getValue() == 11) {
                aces++;
            }
        }

        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;
    }

    public boolean busted() {
        return getValue() > 21;
    }

    public boolean hasBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public void printDealerHand() {
        System.out.println("X " + cards.get(1));
    }

    public String toString() {
        StringBuilder handString = new StringBuilder();
        for (Card card : cards) {
            handString.append(card).append(" ");
        }
        handString.append("(").append(getValue()).append(")");
        return handString.toString();
    }
}
