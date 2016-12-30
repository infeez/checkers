package com.infeez.simple;

import com.infeez.simple.base.GameSpriteBatch;
import com.infeez.simple.entity.Cell;
import com.infeez.simple.entity.Checker;
import com.infeez.simple.utils.BoardArrayPosition;
import com.infeez.simple.utils.Constants.GameEnvTypes;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public Iterator<Cell> iterator() {
        return toList().iterator();
    }

    public synchronized void forEach(Consumer<? super Cell> action) {
        for (Cell[] c1 : cells) {
            for (Cell c2 : c1) {
                action.accept(c2);
            }
        }
    }

    public Spliterator<Cell> spliterator() {
        return toList().spliterator();
    }

    public synchronized Cell getCell(@Nonnull BoardArrayPosition boardArrayPosition) {
        return getCell(boardArrayPosition.getIndexFirst(), boardArrayPosition.getIndexSecond());
    }

    public synchronized Cell getCell(int i, int j) {
        return cells[i][j];
    }

    public synchronized void setCell(@Nonnull Cell cell, int i, int j) {
        cells[i][j] = cell;
    }

    public synchronized List<Cell> toList() {
        List<Cell> list = new LinkedList<>();
        forEach(list::add);
        return list;
    }

    public Stream<Cell> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public synchronized void clearCheckers() {
        forEach(Cell::removeChecker);
    }

    public synchronized Cell findCellByCoordinates(int x, int y) {
        return stream().filter(c -> c.contains(x, y)).findFirst().orElse(null);
    }

    public synchronized Cell findCellByCoordinatesAndHaveChecker(int x, int y) {
        return stream().filter(c -> c.contains(x, y) && c.isChecker()).findFirst().orElse(null);
    }

    public synchronized Checker findCheckerByCoords(final int x, final int y) {
        Optional<Cell> cell = stream().filter(c -> c.contains(x, y)).findFirst();
        return cell.map(Cell::getChecker).orElse(null);
    }

    public synchronized Checker findCheckerById(int id) {
        Optional<Cell> cell = stream().filter(c -> c.getId() == id).findFirst();
        return cell.map(Cell::getChecker).orElse(null);
    }

}
