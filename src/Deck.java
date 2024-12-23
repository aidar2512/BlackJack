import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<>();

        // Create a deck of 52 cards (13 ranks x 4 suits)
        for (int rank = 0; rank < 13; rank++) { // Ranks: 0-12 (2 to Ace)
            for (int suit = 0; suit < 4; suit++) { // Suits: 0-3
                deck.add(new Card(rank, suit));
            }
        }
    }

    // Shuffle the deck
    public void shuffle() {
        Collections.shuffle(deck);
    }

    // Deal a card from the deck
    public Card deal() {
        if (deck.isEmpty()) {
            System.out.println("Deck is empty, reshuffling.");
            return null; // Or handle reshuffling logic here
        }
        return deck.remove(0);
    }

    // Get the number of remaining cards in the deck
    public int getSize() {
        return deck.size();
    }
}
