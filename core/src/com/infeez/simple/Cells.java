package com.infeez.simple;

import com.infeez.simple.base.GameSpriteBatch;
import com.infeez.simple.entity.Cell;
import com.infeez.simple.utils.BoardArrayPosition;
import com.infeez.simple.utils.Constants.GameEnvTypes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Cells implements Iterable<Cell> {

    private static final int COUNT_CELL = 8;
    private final Cell[][] cells = new Cell[COUNT_CELL][COUNT_CELL];

    public Cells() {
    }

    public void createBoard(@Nonnull GameSpriteBatch batch) {
        float x = 0, y = 0;
        for (int i = 0; i < COUNT_CELL; i++) {
            for (int j = 0; j < COUNT_CELL; j++) {
                GameEnvTypes type = GameEnvTypes.BLACK;
                if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                    type = GameEnvTypes.WHITE;
                }
                Cell cell = new Cell(i, j, x, y, type, batch);
                setCell(cell, i, j);
                x += Cell.CELL_SIZE;
            }
            x = 0;
            y += Cell.CELL_SIZE;
        }
    }

    public void startCellsPosition() {
        clearCheckers();
        for (int i = 0; i < COUNT_CELL; i++) {
            for (int j = 0; j < COUNT_CELL; j++) {
                if (i <= 2 && cells[i][j].isBlackType()) {
                    cells[i][j].setChecker(GameEnvTypes.BLACK);
                } else if (i >= 5 && cells[i][j].isBlackType()) {
                    cells[i][j].setChecker(GameEnvTypes.WHITE);
                }
            }
        }
    }

    public Cell getCell(@Nonnull BoardArrayPosition boardArrayPosition) {
        return cells[boardArrayPosition.getIndexFirst()][boardArrayPosition.getIndexSecond()];
    }

    public void setCell(@Nonnull Cell cell, int i, int j) {
        cells[i][j] = cell;
    }

    public void clearCheckers() {
        for (Cell cell : this) {
            cell.removeChecker();
        }
    }

    public Cell findCellByCoordinatesAndHaveChecker(int x, int y) {
        for (Cell cell : this) {
            if (cell.contains(x, y) && cell.isChecker()) {
                return cell;
            }
        }
        return null;
    }

    public List<Cell> toList() {
        List<Cell> result = new ArrayList<>(COUNT_CELL + COUNT_CELL);
        for (Cell[] a : cells) {
            Collections.addAll(result, a);
        }
        return result;
    }

    public Iterator<Cell> iterator() {
        return toList().iterator();
    }
}
