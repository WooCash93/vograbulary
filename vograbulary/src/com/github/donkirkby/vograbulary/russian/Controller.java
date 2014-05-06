package com.github.donkirkby.vograbulary.russian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class Controller {
    private RussianDollsScreen screen;
    private ArrayList<String> clues = new ArrayList<String>();
    private int clueNumber;

    /**
     * Load puzzles in from the source file, one puzzle per line.
     * @param reader contains the puzzles, will be closed before this method
     * returns.
     */
    public void loadPuzzles(Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String puzzle;
            while (null != (puzzle = bufferedReader.readLine())) {
                clues.add(puzzle);
            }
            next();
            
            reader.close();
        } catch (IOException ex) {
//  TODO:          throw new RuntimeException("Loading puzzles failed.", ex);
        }
        
    }
    
    public void setScreen(RussianDollsScreen screen) {
        this.screen = screen;
    }

    public void next() {
        String clue = clues.get(clueNumber++);
        screen.setPuzzle(new Puzzle(clue));
    }

}
