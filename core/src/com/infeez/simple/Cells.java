package com.infeez.simple;

import java.util.*;
import java.util.function.Consumer;

public class Cells implements Iterable<Cell> {

    public static final int COUNT_CELL = 8;
    private Cell[][] cells = new Cell[COUNT_CELL][COUNT_CELL];

    public Iterator<Cell> iterator() {
        return toList().iterator();
    }

    public void forEach(Consumer<? super Cell> action) {
        for (Cell[] c1 : cells) {
            for(Cell c2 : c1) {
                action.accept(c2);
            }
        }
    }

    public Spliterator<Cell> spliterator() {
        return toList().spliterator();
    }

    public Cell getCell(int i, int j){
        return cells[i][j];
    }

    public void setCell(Cell cell, int i, int j){
        cells[i][j] = cell;
    }

    public List<Cell> toList() {
        List<Cell> list = new LinkedList<>();
        forEach(list::add);
        return list;
    }

}
