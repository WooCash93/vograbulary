package com.github.donkirkby.vograbulary.poemsorting;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PoemTest {
    @Test
    public void loadLines() {
        List<Poem> poems = Poem.load("The first line,", "And the second.");
        
        assertThat("poems length", poems.size(), is(1));
        Poem poem = poems.get(0);
        List<String> lines = poem.getLines();
        assertThat("line count", lines.size(), is(2));
        assertThat("second line", lines.get(1), is("And the second."));
    }
    
    @Test
    public void loadTitle() {
        List<Poem> poems = Poem.load(
                "# A Poem #",
                "The first line,",
                "And the second.");
        
        assertThat("poems length", poems.size(), is(1));
        Poem poem = poems.get(0);
        List<String> lines = poem.getLines();
        assertThat("line count", lines.size(), is(2));
        assertThat("second line", lines.get(1), is("And the second."));
        assertThat("title", poem.getTitle(), is("A Poem"));
    }
    
    @Test
    public void loadTwoPoems() {
        List<Poem> poems = Poem.load(
                "# A Poem #",
                "The first line,",
                "And the second.",
                "# Another Poem #",
                "With one line");
        
        assertThat("poems length", poems.size(), is(2));
        assertThat("first line count", poems.get(0).getLines().size(), is(2));
        assertThat("first title", poems.get(0).getTitle(), is("A Poem"));
        assertThat("second line count", poems.get(1).getLines().size(), is(1));
        assertThat("second title", poems.get(1).getTitle(), is("Another Poem"));
    }
    
    @Test
    public void sortWords() {
        List<Poem> poems = Poem.load(
                "# A Poem #",
                "The first line,",
                "And the second.");
        Poem poem = poems.get(0);
        
        Poem sorted = poem.sortWords();
        
        assertThat("lines", sorted.getLines(), is(Arrays.asList(
                "eht first ,eiln",
                "adn eht .cdenos")));
        assertThat("title", poem.getTitle(), is("A Poem"));
    }
}