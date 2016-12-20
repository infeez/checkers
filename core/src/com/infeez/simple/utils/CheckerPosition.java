package com.infeez.simple.utils;

public class CheckerPosition {

    private int indexFirst;
    private int indexSecond;

    public CheckerPosition(){
    }

    public CheckerPosition(int indexFirst, int indexSecond) {
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

    public void set(CheckerPosition checkerPosition){
        setIndexFirst(checkerPosition.getIndexFirst());
        setIndexSecond(checkerPosition.getIndexSecond());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckerPosition that = (CheckerPosition) o;
        return indexFirst == that.indexFirst && indexSecond == that.indexSecond;
    }

    public int hashCode() {
        int result = indexFirst;
        result = 31 * result + indexSecond;
        return result;
    }

    public String toString() {
        return "CheckerPosition{" +
                "indexFirst=" + indexFirst +
                ", indexSecond=" + indexSecond +
                '}';
    }
}
