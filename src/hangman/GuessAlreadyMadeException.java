package hangman;

public class GuessAlreadyMadeException extends Exception {
    public void printError() {
        System.out.println("You've already guessed that letter.");
    }
}
