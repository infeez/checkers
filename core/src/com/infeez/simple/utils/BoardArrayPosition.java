package com.infeez.simple.utils;

public class BoardArrayPosition {

    private int indexFirst;
    private int indexSecond;

    public BoardArrayPosition(){
    }

    public BoardArrayPosition(int indexFirst, int indexSecond) {
        this.indexFirst = indexFirst;
        this.indexSecond = indexSecond;
    }

    public int getIndexFirst() {
        return indexFirst;
    }

    public void setIndexFirst(int indexFirst) {
        this.indexFirst = indexFirst;
    }

    public int getIndexSecond() {
        return indexSecond;
    }

    public void setIndexSecond(int indexSecond) {
        this.indexSecond = indexSecond;
    }

    public void set(BoardArrayPosition boardArrayPosition){
        setIndexFirst(boardArrayPosition.getIndexFirst());
        setIndexSecond(boardArrayPosition.getIndexSecond());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardArrayPosition that = (BoardArrayPosition) o;
        return indexFirst == that.indexFirst && indexSecond == that.indexSecond;
    }

    public int hashCode() {
        int result = indexFirst;
        result = 31 * result + indexSecond;
        return result;
    }

    public String toString() {
        return "BoardArrayPosition{" +
                "indexFirst=" + indexFirst +
                ", indexSecond=" + indexSecond +
                '}';
    }
}
