package hangman;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) throws EmptyDictionaryException, IOException, GuessAlreadyMadeException {
        //Create File object with provided argument
        File dictionaryFile = new File(args[0]);
        //Cast the String argument to an int
        int wordLength = Integer.parseInt(args[1]);
        //Cast the String argument to an int
        int guesses = Integer.parseInt(args[2]);
        //Create Scanner for user input
        Scanner in = new Scanner(System.in);
        //Variable to store player's guess
        char guess = '_';

        EvilHangmanGame game = new EvilHangmanGame();
        game.startGame(dictionaryFile, wordLength);

        while (guesses != 0) {
            System.out.println("You have " + guesses + " guesses left");
            System.out.println("Used letters: " + game.getGuessedLetters().toString());
            System.out.println("Word: " + game.getCombinedKeys());
            System.out.print("Enter a guess: ");

            //Gets the guess results from the game class
            Set<String> guessResults = new HashSet<>();
            while (guessResults.size() == 0) {
                //Reads user input
                guess = in.next().charAt(0);
                if ((guess >= 'A' && guess <= 'Z') || (guess >= 'a' && guess <= 'z')) {
                    try {
                        guessResults = game.makeGuess(guess);

                    } catch (GuessAlreadyMadeException ex) {
                        ex.printError();
                        System.out.print("Try again: ");
                    }
                }
                else {
                    System.out.println("Invalid input.");
                    System.out.print("Try again: ");
                }
            }

            //If correct guess
            if (game.getLargestSetKey().contains(Character.toString(guess))) {
                //If there isn't any '_' left in the set
                if (!game.getCombinedKeys().contains("_")) {
                    System.out.println("Congrats! You win!");
                    System.out.println("The word was: " + game.getCombinedKeys());
                    break;
                } else {
                    System.out.println("Yes, there is/are " + game.getLargestSetLetterCount() + " letter(s)");
                }
            } else {
                guesses--;
                //If the player ran out of guesses
                if (guesses == 0) {
                    Iterator<String> it = guessResults.iterator();

                    System.out.println("You lose!");
                    System.out.println("The word was " + it.next());
                } else {
                    System.out.println("Sorry, there are no " + guess + "'s");
                }
            }
            System.out.print('\n');
        }
    }

}
