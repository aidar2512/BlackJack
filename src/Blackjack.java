import java.util.Scanner;

public class Blackjack {
    private static final int STARTING_BANKROLL = 100;

    private final Scanner scanner = new Scanner(System.in);

    private String getPlayerMove() {
        while (true) {
            System.out.print("Enter move (hit/stand): ");
            String move = scanner.nextLine().trim().toLowerCase();
            if (move.equals("hit") || move.equals("stand")) {
                return move;
            }
            System.out.println("Invalid input. Please try again.");
        }
    }

    private void dealerTurn(Hand dealer, Deck deck) {
        while (true) {
            System.out.println("Dealer's hand:");
            System.out.println(dealer);

            int value = dealer.getValue();
            System.out.println("Dealer's hand value: " + value);

            if (value < 17) {
                System.out.println("Dealer hits...");
                Card card = deck.deal();
                dealer.addCard(card);
                System.out.println("Dealer received: " + card);

                if (dealer.busted()) {
                    System.out.println("Dealer busted!");
                    break;
                }
            } else {
                System.out.println("Dealer stands.");
                break;
            }
        }
    }

    private boolean playerTurn(Hand player, Deck deck) {
        while (true) {
            String move = getPlayerMove();

            if (move.equals("hit")) {
                Card card = deck.deal();
                System.out.println("You received: " + card);
                player.addCard(card);
                System.out.println("Your hand:");
                System.out.println(player);

                if (player.busted()) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private boolean playerWins(Hand player, Hand dealer) {
        if (player.busted()) {
            return false;
        }
        if (dealer.busted()) {
            return true;
        }
        return player.getValue() > dealer.getValue();
    }

    private boolean push(Hand player, Hand dealer) {
        return player.getValue() == dealer.getValue();
    }

    private double findWinner(Hand dealer, Hand player, int bet) {
        if (playerWins(player, dealer)) {
            System.out.println("You win!");
            if (player.hasBlackjack()) {
                return 1.5 * bet;
            }
            return bet;
        } else if (push(player, dealer)) {
            System.out.println("It's a tie.");
            return 0;
        } else {
            System.out.println("Dealer wins.");
            return -bet;
        }
    }

    private double playRound(double bankroll) {
        int bet = 0;
        while (true) {
            try {
                System.out.print("Enter your bet: ");
                bet = Integer.parseInt(scanner.nextLine());
                if (bet <= 0 || bet > bankroll) {
                    System.out.println("Invalid bet amount. Please enter a positive number not exceeding your bankroll.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Your hand:");
        System.out.println(player);

        System.out.println("Dealer's hand:");
        dealer.printDealerHand();

        boolean playerBusted = playerTurn(player, deck);

        if (playerBusted) {
            System.out.println("You busted.");
        }

        if (!playerBusted) {
            dealerTurn(dealer, deck);
        }

        double bankrollChange = findWinner(dealer, player, bet);
        bankroll += bankrollChange;

        System.out.println("Your new bankroll: " + bankroll);
        return bankroll;
    }


    public void run() {
        double bankroll = STARTING_BANKROLL;
        System.out.println("Starting bankroll: " + bankroll);

        while (true) {
            bankroll = playRound(bankroll);

            System.out.print("Would you like to play again? (Y/N): ");
            String playAgain = scanner.nextLine().trim().toUpperCase();
            if (playAgain.equals("N")) {
                break;
            }
        }

        System.out.println("Thanks for playing!");
    }

    public static void main(String[] args) {
        new Blackjack().run();
    }
}
