package com.inja.boggle;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by nejasix on 11/27/15.
 */
public class Game {

    private Context mContext;

    private Set<String> words;

    public Game(Context context)
    {
        this.words = new HashSet<>();
        this.mContext = context;
        //FIXME : This should provide a callback in case it's too big.
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    is = mContext.getAssets().open("words");

                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                String line;
                    while ((line = r.readLine()) != null) {
                        words.add(line.toLowerCase());
                    }
                    Log.d("TAG", "DONE");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isWord(String st)
    {
        return this.words.contains(st.toLowerCase());
    }



    public String getCharacter(int diceNumber)
    {
        int resource = -1;
        switch (diceNumber)
        {
            case 1:
                resource = R.array.dice1;
                break;
            case 2:
                resource = R.array.dice2;
                break;
            case 3:
                resource = R.array.dice3;
                break;
            case 4:
                resource = R.array.dice4;
                break;
            case 5:
                resource = R.array.dice5;
                break;
            case 6:
                resource = R.array.dice6;
                break;
            case 7:
                resource = R.array.dice7;
                break;
            case 8:
                resource = R.array.dice8;
                break;
            case 9:
                resource = R.array.dice9;
                break;
            case 10:
                resource = R.array.dice10;
                break;
            case 11:
                resource = R.array.dice11;
                break;
            case 12:
                resource = R.array.dice12;
                break;
            case 13:
                resource = R.array.dice13;
                break;
            case 14:
                resource = R.array.dice14;
                break;
            case 15:
                resource = R.array.dice15;
                break;
            case 16:
                resource = R.array.dice16;
                break;

            default:
                throw new RuntimeException();

        }
        String[] pool = mContext.getResources().getStringArray(resource);
        Random random = new Random();
        int index = random.nextInt(pool.length);
        return pool[index];
    }
}
