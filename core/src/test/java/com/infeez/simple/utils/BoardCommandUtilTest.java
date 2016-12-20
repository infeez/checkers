package com.infeez.simple.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoardCommandUtilTest {

    @Test
    public void testParse(){
        CheckerPosition checkerPosition = BoardCommandUtil.parseCommand("a1");
        assertTrue(checkerPosition.getIndexFirst() == 0);
        assertTrue(checkerPosition.getIndexSecond() == 7);

        checkerPosition = BoardCommandUtil.parseCommand("g1");
        assertTrue(checkerPosition.getIndexFirst() == 6);
        assertTrue(checkerPosition.getIndexSecond() == 7);
    }

}