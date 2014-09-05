package com.github.donkirkby.vograbulary.ultraghost;

import java.util.ArrayList;

import com.github.donkirkby.vograbulary.core.ultraghost.WordList;

public class Puzzle {
    public interface Listener {
        /**
         * This is called whenever one of the fields is changed.
         */
        void changed();
        
        /**
         * This is called when the puzzle is finished. Solution and response
         * are set.
         */
        void completed();
    }
    public static String NOT_SET = null;
    public static String NO_SOLUTION = "";
    
    private String letters;
    private String solution;
    private String response;
    private String hint;
    private Student owner;
    private WordList wordList;
    private int minimumWordLength = 4;
    private String previousWord;
    private ArrayList<Listener> listeners = 
            new ArrayList<Listener>();
    private boolean isComplete;
    
    public Puzzle(String letters, Student owner, WordList wordList) {
        if (letters == null) {
            throw new IllegalArgumentException("Puzzle letters were null.");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Puzzle owner was null.");
        }
        this.letters = letters;
        this.owner = owner;
        this.wordList = wordList;
    }
    
    public Puzzle(String letters, Student owner) {
        this(letters, owner, new DummyWordList());
    }
    
    /**
     * A dummy word list that contains all words.
     *
     */
    private static class DummyWordList extends WordList {
        @Override
        public boolean contains(String word) {
            return true;
        }
    }

    /**
     * The three letters that must be matched in a valid solution.
     */
    public String getLetters() {
        return letters;
    }

    /**
     * Get a display version of the three letters, plus any previous word.
     */
    public String getLettersDisplay() {
        return previousWord == null
                ? letters
                : letters + " after " + previousWord;
    }

    /**
     * A solution to the puzzle. If it matches the three letters, then it's 
     * valid. Null means it hasn't been set yet, and empty string means it
     * has been skipped.
     */
    public String getSolution() {
        return solution;
    }
    public void setSolution(String solution) {
        this.solution = solution;
        onChanged();
    }

    /**
     * Raise the changed event to any listeners.
     */
    private void onChanged() {
        boolean isJustCompleted =
                isComplete
                ? false
                : (isComplete = (solution != null && response != null));
        for (Listener listener : listeners) {
            listener.changed();
            if (isJustCompleted) {
                listener.completed();
            }
        }
    }
    
    /**
     * Another solution that tries to improve on the original solution by being
     * shorter or the same length and earlier in the dictionary. Null means
     * it hasn't been set yet, and empty string means that no response is 
     * wanted.
     */
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
        onChanged();
    }
    
    /**
     * Another solution that could have been used, if any exists.
     */
    public String getHint() {
        return hint;
    }
    public void setHint(String hint) {
        this.hint = hint;
        onChanged();
    }
    
    /**
     * The result of this puzzle, including the change in score. It will be
     * UKNOWN until a solution and response have been entered.
     */
    public WordResult getResult() {
        if (solution == NOT_SET) {
            return WordResult.UNKNOWN;
        }
        if (response == NOT_SET) {
            return checkSolution();
        }
        return checkResponse();
    }

    /**
     * Compare the solution and response.
     * @return the result of comparing the two solutions
     */
    private WordResult checkResponse() {
        boolean isSkipped = solution == null || solution.length() == 0;
        if (response.length() == 0) {
            return isSkipped
                    ? WordResult.SKIPPED
                    : WordResult.NOT_IMPROVED;
        }
        String challengeUpper = response.toUpperCase();
        if ( ! isMatch(challengeUpper)) {
            return isSkipped
                    ? WordResult.IMPROVED_SKIP_NOT_A_MATCH
                    : WordResult.IMPROVEMENT_NOT_A_MATCH;
        }
        if ( ! wordList.contains(challengeUpper)) {
            return isSkipped
                    ? WordResult.IMPROVED_SKIP_NOT_A_WORD
                    : WordResult.IMPROVEMENT_NOT_A_WORD;
        }
        if (challengeUpper.length() < getMinimumWordLength()) {
            return isSkipped
                    ? WordResult.IMPROVED_SKIP_TOO_SHORT
                    : WordResult.IMPROVEMENT_TOO_SHORT;
        }
        if (isTooSoon(challengeUpper)) {
            return isSkipped
                    ? WordResult.IMPROVED_SKIP_TOO_SOON
                    : WordResult.IMPROVEMENT_TOO_SOON;
        }
        if (isSkipped) {
            return WordResult.WORD_FOUND;
        }
        return challengeWord(solution.toUpperCase(), challengeUpper);
    }

    /**
     * Check to see if the solution is in the word list and a match for the puzzle
     * letters.
     * @return VALID if the solution is valid, otherwise the reason the solution
     * was rejected.
     */
    private WordResult checkSolution() {
        if (solution.length() == 0) {
            return WordResult.SKIPPED;
        }
        String solutionUpper = solution.toUpperCase();
        if ( ! wordList.contains(solutionUpper)) {
            return WordResult.NOT_A_WORD;
        }
        if (isTooSoon(solutionUpper)) {
            return WordResult.TOO_SOON;
        }
        return ! isMatch(solution)
                ? WordResult.NOT_A_MATCH
                : solution.length() < getMinimumWordLength()
                ? WordResult.TOO_SHORT
                : WordResult.VALID;
    }

    /**
     * Check if a solution or response is too soon (not later or longer than
     * the previous word).
     * @param wordUpper the solution or response, all in upper case
     * @return false if the word is later or longer than the previous word,
     * otherwise true.
     */
    private boolean isTooSoon(String wordUpper) {
        if (previousWord != null) {
            WordResult solutionOverPrevious = 
                    challengeWord(previousWord.toUpperCase(), wordUpper);
            if (solutionOverPrevious != WordResult.LATER &&
                    solutionOverPrevious != WordResult.LONGER) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * The student who was assigned this puzzle. Any score will be given to
     * that student.
     */
    public Student getOwner() {
        return owner;
    }
    
    @Override
    public String toString() {
        return "Puzzle(" + letters + ", " + owner.getName() + ")";
    }

    /**
     * Check if a word is a match to the puzzle letters, but don't check 
     * if it is in the word list.
     * @param word the word to check, case insensitive
     */
    public boolean isMatch(String word) {
        String upper = word.toUpperCase();
        if (upper.charAt(upper.length()-1) != letters.charAt(2)) {
            return false;
        }
        if (upper.charAt(0) != letters.charAt(0)) {
            return false;
        }
        int foundAt = upper.indexOf(letters.charAt(1), 1);
        return 0 < foundAt && foundAt < upper.length() - 1;
    }

    /**
     * Compare a new word with the current best solution. Both words must
     * be valid solutions all in upper case.
     */
    private WordResult challengeWord(String solution, String challenge) {
        return challenge.length() > solution.length()
                ? WordResult.LONGER
                : challenge.length() == solution.length()
                && challenge.compareTo(solution) > 0
                ? WordResult.LATER
                : challenge.length() < solution.length()
                ? WordResult.SHORTER
                : challenge.equals(solution)
                ? WordResult.NOT_IMPROVED
                : WordResult.EARLIER;
    }

    /**
     * Find the most common word that beats both the solution and the 
     * response.
     * @return a valid solution that beats both, or null if none found
     */
    public String findNextBetter() {
        String bestSoFar = 
                isImproved() 
                ? response.toUpperCase() 
                : solution == null ? "" : solution.toUpperCase();
        Puzzle searchPuzzle = new Puzzle(letters, owner);
        searchPuzzle.setPreviousWord(previousWord);
        searchPuzzle.setSolution(bestSoFar);
        for (String word : wordList) {
            if (word.length() < getMinimumWordLength()) {
                continue;
            }
            searchPuzzle.setResponse(word);
            if (searchPuzzle.isImproved()) {
                return word;
            }
        }
        return null; // no improvement found.
    }

    public boolean isImproved() {
        WordResult result = getResult();
        return result == WordResult.SHORTER || 
                result == WordResult.EARLIER || 
                result == WordResult.WORD_FOUND;
    }

    /**
     * Set a limit for how long a word must be to solve the puzzle. Default 4.
     * @param minimumWordLength
     */
    public void setMinimumWordLength(int minimumWordLength) {
        this.minimumWordLength = minimumWordLength;
    }
    public int getMinimumWordLength() {
        return minimumWordLength;
    }

    /**
     * Set the word from the previous puzzle. All solutions to this puzzle must
     * be worse than the previous word. Either longer or the same length but
     * later in the dictionary.
     * @param previousWord must be a word from the dictionary that matches the
     * three letters of this puzzle.
     */
    public void setPreviousWord(String previousWord) {
        this.previousWord = previousWord;
    }
    public String getPreviousWord() {
        return previousWord;
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
