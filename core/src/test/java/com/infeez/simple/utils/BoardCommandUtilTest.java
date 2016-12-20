package com.infeez.simple.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoardCommandUtilTest {

    @Test
    public void testParse(){
        BoardArrayPosition boardArrayPosition = BoardCommandUtil.parseCommand("a1");
        assertTrue(boardArrayPosition.getIndexFirst() == 0);
        assertTrue(boardArrayPosition.getIndexSecond() == 7);

        boardArrayPosition = BoardCommandUtil.parseCommand("g1");
        assertTrue(boardArrayPosition.getIndexFirst() == 6);
        assertTrue(boardArrayPosition.getIndexSecond() == 7);
    }

}