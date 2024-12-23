import java.util.Scanner;
import java.util.concurrent.*;

public class Blackjack {
    // The starting bankroll for the player.
    private static final int STARTING_BANKROLL = 100;

    // Create a scanner object to read user input
    private static final Scanner scanner = new Scanner(System.in);
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    // Enum for game modes
    private enum GameMode {
        CLASSIC,
        JACKPOT,
        SPEED,
        TRAP
    }

    private String getPlayerMove() {
        while (true) {
            System.out.print("Enter move (hit/stand): ");
            String move = scanner.nextLine();
            move = move.toLowerCase();

            if (move.equals("hit") || move.equals("stand")) {
                return move;
            }
            System.out.println("Please try again.");
        }
    }

    private boolean playerTurn(Hand player, Deck deck) {
        while (true) {
            String move = getPlayerMove();

            if (move.equals("hit")) {
                Card c = deck.deal();
                System.out.println("Your card was: " + c);
                player.addCard(c);
                System.out.println("Player's hand");
                System.out.println(player);

                if (player.busted()) {
                    return true;
                }
            } else {
                // If we didn't hit, the player chose to stand, which means the turn is over.
                return false;
            }
        }
    }

    private void dealerTurn(Hand dealer, Deck deck) {
        while (true) {
            System.out.println("Dealer's hand");
            System.out.println(dealer);

            int value = dealer.getValue();
            System.out.println("Dealer's hand has value " + value);

            System.out.print("Enter to continue...");
            scanner.nextLine();

            if (value < 17) {
                System.out.println("Dealer hits");
                Card c = deck.deal();
                dealer.addCard(c);

                System.out.println("Dealer card was " + c);

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
            System.out.println("Player wins!");

            if (player.hasBlackjack()) {
                return 1.5 * bet;
            }

            return bet;
        } else if (push(player, dealer)) {
            System.out.println("You push");
            return 0;
        } else {
            System.out.println("Dealer wins");
            return -bet;
        }
    }

    private double playRound(double bankroll) {
        int bet;
        while (true) {
            System.out.print("What is your bet? ");
            bet = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character left by nextInt()

            if (bet > 0 && bet <= bankroll) {
                break;
            }
            System.out.println("Invalid bet. Your bet must be > 0 and <= " + bankroll + ".");
        }

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Player's Hand");
        System.out.println(player);

        System.out.println("Dealer's hand");
        dealer.printDealerHand();

        boolean playerBusted = playerTurn(player, deck);

        if (playerBusted) {
            System.out.println("You busted :(");
        }

        System.out.print("Enter for dealer turn...");
        scanner.nextLine();

        dealerTurn(dealer, deck);

        double bankrollChange = findWinner(dealer, player, bet);

        bankroll += bankrollChange;

        System.out.println("New bankroll: " + bankroll);

        return bankroll;
    }

    private GameMode selectGameMode() {
        while (true) {
            System.out.println("=====================================");
            System.out.println("          Select a Game Mode        ");
            System.out.println("=====================================");
            System.out.println("          1. Classic                ");
            System.out.println("          2. Jackpot                ");
            System.out.println("          3. Speed                  ");
            System.out.println("          4. Trap                   ");
            System.out.println("          5. Exit                   ");
            System.out.println("=====================================");
            System.out.print("Enter the number of your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    return GameMode.CLASSIC;
                case 2:
                    return GameMode.JACKPOT;
                case 3:
                    return GameMode.SPEED;
                case 4:
                    return GameMode.TRAP;
                case 5:
                    System.out.println("Exiting the game.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void run() {
        double bankroll = STARTING_BANKROLL;
        System.out.println("Starting bankroll: " + bankroll);

        while (true) {
            GameMode mode = selectGameMode(); // Select game mode

            while (true) {
                switch (mode) {
                    case CLASSIC:
                        bankroll = playRound(bankroll);
                        break;
                    case JACKPOT:
                        bankroll = playJackpotRound(bankroll); // Placeholder for Jackpot mode
                        break;
                    case SPEED:
                        bankroll = playSpeedRound(bankroll);
                        break;
                    case TRAP:
                        bankroll = playTrapRound(bankroll); // Placeholder for Trap mode
                        break;
                }

                if (bankroll <= 0) {
                    System.out.println("You are out of money! Game over.");
                    return; // Exit the game
                }
                System.out.println(" ");
                String playAgain;
                while (true) {
                    System.out.print("Would you like to play again in the same mode? (Y/N) ");
                    playAgain = scanner.nextLine().trim();
                    if (playAgain.equalsIgnoreCase("Y") || playAgain.equalsIgnoreCase("N")) {
                        break; // Exit input loop if input is valid
                    } else {
                        System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
                    }
                }

                if (playAgain.equalsIgnoreCase("N")) {
                    break; // Exit to the game mode selection
                }
            }
        }
    }

    private String getPlayerMoveWithTimeout() {
        Future<String> futureMove = executorService.submit(() -> {
            while (true) {
                System.out.println("You have 15 seconds to make a move H(it)/S(tand): ");
                if (scanner.hasNextLine()) {
                    String move = scanner.nextLine().toUpperCase();
                    if (move.equals("H") || move.equals("S")) {
                        return move;
                    } else {
                        System.out.println("Invalid input. Please enter 'H' or 'S'.");
                    }
                }
            }
        });

        try {
            return futureMove.get(15, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            futureMove.cancel(true);
            System.out.println("Time's up! Automatically choosing 'H(it)'.");
            return "H";
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "H";
        }
    }

    private boolean playerTurnTime(Hand player, Deck deck) {
        while (true) {
            String move = getPlayerMoveWithTimeout();

            if (move.equals("H")) {
                Card c = deck.deal();
                System.out.println("Your card was: " + c);
                player.addCard(c);
                System.out.println("Player's hand");
                System.out.println(player);

                if (player.busted()) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private double playSpeedRound(double bankroll) {
        System.out.println("Playing Speed mode...");

        int bet;
        while (true) {
            System.out.print("What is your bet? ");
            bet = scanner.nextInt();
            scanner.nextLine();

            if (bet > 0 && bet <= bankroll) {
                break;
            }
            System.out.println("Invalid bet. Your bet must be > 0 and <= " + bankroll + ".");
        }

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Player's Hand");
        System.out.println(player);

        System.out.println("Dealer's hand");
        dealer.printDealerHand();

        boolean playerBusted = playerTurnTime(player, deck);

        if (playerBusted) {
            System.out.println("You busted :(");
            bankroll -= bet; // Уменьшаем банкролл на сумму ставки
            System.out.println("New bankroll: " + bankroll);
            return bankroll;
        } else {
            System.out.print("Enter for dealer turn...");
            scanner.nextLine();
            dealerTurn(dealer, deck);
            double bankrollChange = findWinner(dealer, player, bet);
            bankroll += bankrollChange;
            System.out.println("New bankroll: " + bankroll);
            return bankroll;
        }
    }

    private double playJackpotRound(double bankroll) {
        System.out.println("Playing Jackpot mode...");

        int bet;
        while (true) {
            System.out.print("What is your bet? ");
            bet = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            if (bet > 0 && bet <= bankroll) {
                break;
            }
            System.out.println("Invalid bet. Your bet must be > 0 and <= " + bankroll + ".");
        }

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Player's Hand");
        System.out.println(player);

        System.out.println("Dealer's hand");
        dealer.printDealerHand();

        boolean playerBusted = playerTurn(player, deck);

        if (playerBusted) {
            System.out.println("You busted :(");
            // Apply 50% chance of losing half the bet or the entire bet
            if (Math.random() < 0.5) {
                System.out.println("You lost half your bet.");
                bankroll -= bet / 2.0;
            } else {
                System.out.println("You lost your entire bet.");
                bankroll -= bet;
            }
            System.out.println("New bankroll: " + bankroll);
            return bankroll;
        }

        System.out.print("Enter for dealer turn...");
        scanner.nextLine();

        dealerTurn(dealer, deck);

        if (player.getValue() == 21) {
            System.out.println("You hit Blackjack!");
            if (Math.random() < 0.7) {
                System.out.println("Congratulations! You doubled your bet.");
                bankroll += 2 * bet;
            } else {
                System.out.println("Jackpot! You tripled your bet.");
                bankroll += 3 * bet;
            }
        } else if (dealer.getValue() > 21 || player.getValue() > dealer.getValue()) {
            System.out.println("You win this round!");
            System.out.println("You keep your bet.");
            // No change to bankroll; player keeps their money
        } else {
            System.out.println("Dealer wins. You lost your bet.");
            bankroll -= bet;
        }

        System.out.println("New bankroll: " + bankroll);
        return bankroll;
    }


    private double playTrapRound(double bankroll) {
        int bet;
        while (true) {
            System.out.print("What is your bet? ");
            bet = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character left by nextInt()

            if (bet > 0 && bet <= bankroll) {
                break;
            }
            System.out.println("Invalid bet. Your bet must be greater than 0 and less than or equal to your bankroll of " + bankroll + ".");
        }

        Deck deck = new Deck();
        deck.shuffle();

        Hand player = new Hand();
        Hand dealer = new Hand();

        player.addCard(deck.deal());
        dealer.addCard(deck.deal());
        player.addCard(deck.deal());
        dealer.addCard(deck.deal());

        System.out.println("Player's Hand");
        System.out.println(player);

        System.out.println("Dealer's Hand (hidden):");
        dealer.printDealerHand();

        boolean playerBusted = playerTurn(player, deck);

        if (playerBusted) {
            System.out.println("You busted :(");
            bankroll -= bet;
            System.out.println("New bankroll: " + bankroll);
            return bankroll;
        }

        System.out.print("Enter for dealer turn...");
        scanner.nextLine();

        dealerTurn(dealer, deck);

        // Randomly decide if a card exchange happens with 1/3 probability
        if (Math.random() < 1.0 / 3.0) {
            System.out.println("Trap activated! Exchanging cards between player and dealer.");

            // Exchange one random card between the player and the dealer
            Card playerCard = player.removeRandomCard();
            Card dealerCard = dealer.removeRandomCard();

            if (playerCard != null && dealerCard != null) {
                player.addCard(dealerCard);
                dealer.addCard(playerCard);
            }

            System.out.println("After exchange:");
            System.out.println("Player's Hand");
            System.out.println(player);

            System.out.println("Dealer's Hand:");
            System.out.println(dealer);
        } else {
            System.out.println("No trap activated this round.");
        }

        double bankrollChange = findWinner(dealer, player, bet);

        bankroll += bankrollChange;

        System.out.println("New bankroll: " + bankroll);

        return bankroll;
    }


    public static void main(String[] args) {
        Blackjack game = new Blackjack(); // Создаем экземпляр Blackjack
        game.run(); // Запускаем игру

    }
}