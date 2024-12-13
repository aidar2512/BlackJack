import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        for (int rank = 0; rank < 13; rank++) {
            for (int suit = 0; suit < 4; suit++) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card deal() {
        return deck.remove(0);
    }
}
