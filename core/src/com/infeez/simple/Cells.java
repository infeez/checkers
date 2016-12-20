package com.infeez.simple;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.entity.Cell;
import com.infeez.simple.entity.Checker;
import com.infeez.simple.utils.CheckerPosition;
import com.infeez.simple.utils.Constants.*;


import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Cells implements Iterable<Cell> {

    private static final int COUNT_CELL = 8;
    private final Cell[][] cells = new Cell[COUNT_CELL][COUNT_CELL];
    private final Object mutex = new Object();

    public Cells(){
    }

    public void createBoard(SpriteBatch batch){
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

    public void startCellsPosition(){
        clearCheckers();
        for (int i = 0; i < COUNT_CELL; i++) {
            for (int j = 0; j < COUNT_CELL; j++) {
                if(i <= 2 && cells[i][j].isBlackType()){
                    cells[i][j].setChecker(GameEnvTypes.BLACK);
                } else if(i >= 5 && cells[i][j].isBlackType()){
                    cells[i][j].setChecker(GameEnvTypes.WHITE);
                }
            }
        }
    }

    public Iterator<Cell> iterator() {
        return toList().iterator();
    }

    public void forEach(Consumer<? super Cell> action) {
        synchronized (mutex) {
            for (Cell[] c1 : cells) {
                for (Cell c2 : c1) {
                    action.accept(c2);
                }
            }
        }
    }

    public Spliterator<Cell> spliterator() {
        return toList().spliterator();
    }

    public Cell getCell(CheckerPosition checkerPosition){
        return getCell(checkerPosition.getIndexFirst(), checkerPosition.getIndexSecond());
    }

    public Cell getCell(int i, int j){
        synchronized (mutex) {
            return cells[i][j];
        }
    }

    public void setCell(Cell cell, int i, int j){
        synchronized (mutex) {
            cells[i][j] = cell;
        }
    }

    public List<Cell> toList() {
        synchronized (mutex) {
            List<Cell> list = new LinkedList<>();
            forEach(list::add);
            return list;
        }
    }

    public Stream<Cell> stream(){
        return StreamSupport.stream(this.spliterator(), false);
    }

    public void clearCheckers(){
        synchronized (mutex) {
            forEach(Cell::removeChecker);
        }
    }

    public Cell findCellByCoordinates(int x, int y){
        return stream().filter(c -> c.contains(x, y)).findFirst().orElse(null);
    }

    public Cell findCellByCoordinatesAndHaveChecker(int x, int y){
        return stream().filter(c -> c.contains(x, y) && c.isChecker()).findFirst().orElse(null);
    }

    public Checker findCheckerByCoords(final int x, final int y){
        Optional<Cell> cell = stream().filter(c -> c.contains(x, y)).findFirst();
        return cell.map(Cell::getChecker).orElse(null);
    }

    public Checker findCheckerById(int id){
        Optional<Cell> cell = stream().filter(c -> c.getId() == id).findFirst();
        return cell.map(Cell::getChecker).orElse(null);
    }

}
