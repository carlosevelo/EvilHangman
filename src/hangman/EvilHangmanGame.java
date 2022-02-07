package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    private Set<String> wordSet;
    private int wordLength;
    private SortedSet<Character> guessedLetters;
    private Map<String, Set<String>> partitionMap;
    private String largestSetKey;
    private int largestLetterCount;
    private String combinedKeys;
    private char currentGuess;

    public EvilHangmanGame () {
        wordSet = new HashSet<>();
        wordLength = 0;
        guessedLetters = new TreeSet<>();
        partitionMap = new HashMap<>();
        setLargestSetKey(wordLength);
        largestLetterCount = 0;
        combinedKeys = largestSetKey;
    }

    //Variable getters
    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public String getCombinedKeys() {
        return combinedKeys;
    }

    public Set<String> getWordSet() {
        return wordSet;
    }

    public String getLargestSetKey() {
        return largestSetKey;
    }

    public int getLargestSetLetterCount() {
        return largestLetterCount;
    }

    //Primary functions. In order called.
    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        wordSet.clear();
        this.wordLength = wordLength;
        guessedLetters.clear();
        setLargestSetKey(wordLength);
        processFile(dictionary);
        largestLetterCount = 0;
        combinedKeys = largestSetKey;

        if (wordSet.size() == 0) {
            throw new EmptyDictionaryException();
        }
    }

    public Set<String> processFile(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("(\\s+)+");

        //Populate the Set
        while (scanner.hasNext()) {
            String str = scanner.next();
            if (str.length() == wordLength) {
                wordSet.add(str);
            }
        }

        return wordSet;
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        currentGuess = Character.toLowerCase(guess);
        if (guessedLetters.contains(currentGuess)) {
            throw new GuessAlreadyMadeException();
        } else {
            guessedLetters.add(currentGuess);
        }

        //Partition wordSet relative to "guess"
        partitionMap = getPartitionMap(currentGuess);

        //Largest set in partitionMap becomes new wordSet
        wordSet = getLargestSet();

        return wordSet;
    }

    public Map<String, Set<String>> getPartitionMap(char guess) {
        Map<String, Set<String>> map = new HashMap<>();
        String keyString;
        String tempWord;
        for (String s : wordSet) {
            tempWord = s;
            keyString = getStringKey(tempWord, guess);

            if (map.containsKey(keyString)) {
                map.get(keyString).add(tempWord);
            } else {
                map.put(keyString, new HashSet<>());
                map.get(keyString).add(tempWord);
            }
        }
        return map;
    }

    public String getStringKey(String word, char guessedLetter) {
        StringBuilder stringKey = new StringBuilder();
        char[] tempCharArray = word.toCharArray();
        for (char c : tempCharArray) {
            if (c == guessedLetter) {
                stringKey.append(guessedLetter);
            } else {
                stringKey.append('_');
            }
        }
        return stringKey.toString();
    }

    public Set<String> getLargestSet() {
        Set<String> largestSet;
        List<String> maxKeys = new ArrayList<>();

        int max = 0;
        for (Map.Entry<String, Set<String>> entry : partitionMap.entrySet()) {
            if (entry.getValue().size() > max) {
                max = entry.getValue().size();
            }
        }
        for (Map.Entry<String, Set<String>> entry : partitionMap.entrySet()) {
            if (entry.getValue().size() == max) {
                maxKeys.add(entry.getKey());
            }
        }

        while (maxKeys.size() > 1) {
            maxKeys = tieBreaker(maxKeys);
        }
        largestSet = partitionMap.get(maxKeys.get(0));
        combinedKeys = combineKeys(combinedKeys, maxKeys.get(0));
        largestSetKey = maxKeys.get(0);
        largestLetterCount = getLetterCount(largestSetKey);

        return largestSet;
    }

    public List<String> tieBreaker(List<String> keys) {
        List<String> narrowedKeys = new ArrayList<>();

        //Returns the key that doesn't contain the letter
        for (String key : keys) {
            if (!key.contains(Character.toString(currentGuess))) {
                narrowedKeys.add(key);
                return narrowedKeys;
            }
        }
        //Returns the key(s) with the fewest letters
        int min = wordLength;
        for (String key : keys) {
            if (getLetterCount(key) < min) {
                min = getLetterCount(key);
            }
        }
        for (String key : keys) {
            if (getLetterCount(key) == min) {
                narrowedKeys.add(key);
            }
        }
        //Returns rightmost keys until only one left
        while (narrowedKeys.size() > 1) {
            narrowedKeys = getRightmostKeys(narrowedKeys);
        }
        return narrowedKeys;
    }

    public List<String> getRightmostKeys(List<String> keys) {
        List<String> rightMostKeys = new ArrayList<>();
        List<String> tempKeys = new ArrayList<>(keys);
        for (int i = 0; i < wordLength; i++) {
            for (int j = 0; j < keys.size(); j++) {
                if (tempKeys.get(j).substring((wordLength - 1) - i, (wordLength) - i).equals(Character.toString(currentGuess))) {
                    rightMostKeys.add(keys.get(j));
                }
            }
            if (rightMostKeys.size() == 1) {
                return rightMostKeys;
            }
            if (rightMostKeys.size() > 1) {
                tempKeys.clear();
                tempKeys.addAll(rightMostKeys);
                rightMostKeys.clear();
            }
        }
        return rightMostKeys;
    }

    public String combineKeys(String currentString, String toCombineString) {
        StringBuilder combinedString = new StringBuilder(currentString);
        for (int i = 0; i < toCombineString.length(); i++) {
            if (toCombineString.charAt(i) == currentGuess) {
                combinedString.deleteCharAt(i);
                combinedString.insert(i, toCombineString.charAt(i));
            }
        }
        return combinedString.toString();
    }

    //Helper functions
    public void setLargestSetKey(int wordLength) {
        largestSetKey = "_".repeat(Math.max(0, wordLength));
    }

    public int getLetterCount(String key) {
        int count = 0;
        char[] tempCharArray = key.toCharArray();
        for (char c : tempCharArray) {
            if (c == currentGuess) {
                count++;
            }
        }
        return count;
    }


}
