package com.inja.boggle;

import android.util.Pair;

/**
 * Created by nejasix on 11/27/15.
 */
//UNUSED:
public class GameGrid {

    private int width;

    private int height;

    public GameGrid(int x, int y)
    {
        this.width = x;
        this.height = y;
    }

    public boolean isDiceNumberAdjacent(int numberOfCurrentDice,int numberOfLastDice)
    {
        return false;
    }

    private Pair<Integer,Integer> getPositionFromDiceNumber(int diceNumber)
    {
        return new Pair<>(0,0);
    }
}
