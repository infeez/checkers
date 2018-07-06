package com.infeez.simple.utils;

import java.util.HashMap;
import java.util.Map;

public class BoardCommandUtil {

    private static final Map<Character, Integer> xPanel = new HashMap<>();
    private static final Map<Character, Integer> yPanel = new HashMap<>();

    static {
        xPanel.put('a', 0);
        xPanel.put('b', 1);
        xPanel.put('c', 2);
        xPanel.put('d', 3);
        xPanel.put('e', 4);
        xPanel.put('f', 5);
        xPanel.put('g', 6);
        xPanel.put('h', 7);

        yPanel.put('1', 7);
        yPanel.put('2', 6);
        yPanel.put('3', 5);
        yPanel.put('4', 4);
        yPanel.put('5', 3);
        yPanel.put('6', 2);
        yPanel.put('7', 1);
        yPanel.put('8', 0);
    }

    public static BoardArrayPosition parseCommand(String command) {
        if (command.length() != 2) {
            throw new IllegalStateException("The command string length must be 2!");
        }
        BoardArrayPosition boardArrayPosition = new BoardArrayPosition();
        boardArrayPosition.setIndexFirst(xPanel.get(command.charAt(0)));
        boardArrayPosition.setIndexSecond(yPanel.get(command.charAt(1)));
        return boardArrayPosition;
    }

    public static String checkerPositionToCommand(BoardArrayPosition boardArrayPosition) {
        final StringBuilder result = new StringBuilder(2);
        for (Map.Entry<Character, Integer> entry : xPanel.entrySet()) {
            if (entry.getValue() == boardArrayPosition.getIndexFirst()) {
                result.append(entry.getKey());
            }
        }
        for (Map.Entry<Character, Integer> entry : yPanel.entrySet()) {
            if (entry.getValue() == boardArrayPosition.getIndexFirst()) {
                result.append(entry.getKey());
            }
        }

        return result.toString();
    }


}
