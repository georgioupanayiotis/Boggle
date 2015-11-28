package com.inja.boggle;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by nejasix on 11/27/15.
 */
public class GameSession
{
    private int currentScore = 0;

    private Set<String> wordsTried;

    private Stack<Integer> stack;

    private Set<Integer> currentCubes;

    private StringBuilder currentWordCandidate;

    public GameSession()
    {
        currentCubes = new HashSet<>();
        stack = new Stack<>();
        currentWordCandidate = new StringBuilder();
        wordsTried = new HashSet<>();
    }

    public int increaseScore(int score)
    {
        currentScore+=score;
        return currentScore;
    }


    public boolean addCharacter(String ch)
    {
        boolean result = false;
        /**
         *
         */
        return result;

    }

    public void restartSession()
    {

    }

    public void hasDice(int number)
    {

    }



}
