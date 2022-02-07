package hangman;

public class EmptyDictionaryException extends Exception {
	//Thrown when dictionary file is empty or no words in dictionary match the length asked for
    public EmptyDictionaryException() {
        printError();
    }
    public void printError() {
        System.out.println("The current dictionary is empty.");
    }
}
