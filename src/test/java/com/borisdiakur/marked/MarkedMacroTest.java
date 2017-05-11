package com.borisdiakur.marked;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MarkedMacroTest {

    private MarkedMacro markedMacro;

    @Before
    public void setUp() {
        markedMacro = new MarkedMacro();
    }

    @Test
    public void convertsCodeFence() {
        // given a markdown containing code fence
        String markdown = "```\nfoobar\n```";

        // when converting markdown to html
        String html = markedMacro.convertToHtml(markdown);

        // then code tag is wrapped in table
        assertEquals("foo", html);
    }

}