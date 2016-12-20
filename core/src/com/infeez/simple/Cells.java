package com.infeez.simple;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.infeez.simple.entity.Cell;
import com.infeez.simple.entity.Checker;
import com.infeez.simple.utils.Constants;


import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Cells implements Iterable<Cell> {

    public static final int COUNT_CELL = 8;
    private Cell[][] cells = new Cell[COUNT_CELL][COUNT_CELL];

    public Cells(){
    }

    public void createBoard(SpriteBatch batch){
        float x = 0, y = 0;
        for (int i = 0; i < Cells.COUNT_CELL; i++) {
            for (int j = 0; j < Cells.COUNT_CELL; j++) {
                Constants.GameEnvTypes type = Constants.GameEnvTypes.BLACK;
                if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                    type = Constants.GameEnvTypes.WHITE;
                }
                Cell cell = new Cell(x, y, type, batch);
                setCell(cell, i, j);
                x += Cell.CELL_SIZE;
            }
            x = 0;
            y += Cell.CELL_SIZE;
        }
    }

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

    public Stream<Cell> stream(){
        return StreamSupport.stream(this.spliterator(), false);
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
