
package com.inja.boggle;

import java.util.List;
import java.util.Stack;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Shakecallback,
        View.OnClickListener
{

    @Bind({R.id.position_1, R.id.position_2, R.id.position_3, R.id.position_4, R.id.position_5,
            R.id.position_6, R.id.position_7, R.id.position_8, R.id.position_9, R.id.position_10,
            R.id.position_11, R.id.position_12, R.id.position_13, R.id.position_14,
            R.id.position_15, R.id.position_16})
    List<DiceView> diceViews;

    @Bind(R.id.current_word)
    TextView currentWord;

    @Bind(R.id.score)
    TextView score;

    @Bind(R.id.time_left)
    TextView timeLeft;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    int scoreNumber = 0;

    @OnClick({R.id.position_1, R.id.position_2, R.id.position_3, R.id.position_4, R.id.position_5,
            R.id.position_6, R.id.position_7, R.id.position_8, R.id.position_9, R.id.position_10,
            R.id.position_11, R.id.position_12, R.id.position_13, R.id.position_14,
            R.id.position_15, R.id.position_16})
    public void onClick(View v)
    {
        DiceView diceView = (DiceView) v;
        Log.d("T", "Dice pressed , " + diceView.getDiceNumber());
        dicePressed(diceView.getDiceNumber());
    }

    /**
     * Inflation animation to give dice-y look.
     */
    Animation inflatingAnimation = null;

    /**
     * Handles dices and words.
     */
    GameDictionary gameDictionary;

    /**
     * Current progress.
     */
    Stack<Integer> dicesSelected;

    /**
     * Current word made up by progress.
     */
    StringBuilder currentCandidateWord = new StringBuilder();

    /**
     * Game count down.
     */
    CountDownTimer gameCountDown = null;

    /**
     * Custom shake detector.
     */
    ShakeDetector shakeDetector = null;

    /**
     * One minute for the gameDictionary.
     */
    private static final int GAME_TIME = 10000*3;

    /**
     * Tick interval for the gameDictionary clock.
     */
    private static final int TICK_INTERVAL = 1000;

    /**
     * This method generates a new board.
     */
    private void generateNewBoard()
    {
        for (int i = 0; i < diceViews.size(); ++i)
        {
            final DiceView diceView = diceViews.get(i);
            diceView.setText(gameDictionary.getCharacterForDice(i));
            diceView.startAnimation(inflatingAnimation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //UI.

        inflatingAnimation = AnimationUtils.loadAnimation(this, R.anim.myanimation);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        /*
         * Initiate the clock for the gameDictionary.
         */
        gameCountDown = startCountDown();
        gameDictionary = new GameDictionary(this);
        dicesSelected = new Stack<>();
        shakeDetector = new ShakeDetector(this);

        generateNewBoard();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        shakeDetector.stop();
        gameCountDown.cancel();
    }

    // Register the Listener when the Activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        shakeDetector.register(this);
    }

    // Unregister the Listener when the Activity is paused
    @Override
    protected void onPause()
    {
        super.onPause();
        shakeDetector.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.roll:
                clearBoard();
                generateNewBoard();
                break;
            case R.id.submit:
                if (gameDictionary.isWordValid(currentCandidateWord.toString()))
                {
                    wordMatched(currentCandidateWord.length());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
                    clearBoard();
                }
                break;
            case R.id.back:
                if (currentCandidateWord.length() > 0)
                {
                    currentCandidateWord = new StringBuilder(currentCandidateWord.substring(0, currentCandidateWord.length() - 1));
                    currentWord.setText(currentCandidateWord.toString());
                    int diceNumber = dicesSelected.pop();
                    updateDiceUI(diceNumber);

                }
                break;
            case R.id.start:
                clearBoard();
                generateNewBoard();
                LinearLayout matrix = (LinearLayout) findViewById(R.id.matrix);;
                matrix.setVisibility(View.VISIBLE);
                clearScore();
                gameCountDown.cancel();
                gameCountDown = startCountDown();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShake()
    {
        generateNewBoard();
    }

    /**
     * Updates the dice UI upon selection.
     * @param diceNumber
     */
    private void updateDiceUI(int diceNumber)
    {
        TextView dice = diceViews.get(diceNumber - 1);
        dice.setTextColor(Color.BLACK);
        dice.setBackgroundColor(Color.parseColor("#ffe3e3"));
    }

    /**
     * Called when a word was matched.
     *
     * @param wordSize the size of the word matched.
     */
    private void wordMatched(int wordSize)
    {
        Toast.makeText(this, "Splendid!", Toast.LENGTH_SHORT).show();
        clearBoard();
        scoreNumber += wordSize;
        score.setText(String.valueOf(scoreNumber));
    }

    /**
     * Initiates a dice selection.
     *
     * @param diceNumber
     */
    private void dicePressed(int diceNumber)
    {
        Log.d("t", diceNumber + " =  and " + dicesSelected.size());
        if (!dicesSelected.contains(diceNumber))
        {
            if (!dicesSelected.isEmpty())
            {
                int last = dicesSelected.peek();
                Log.d("d", "Processing = " + diceNumber + " last " + last);
                if (isAdjacent(diceNumber, last))
                {
                    addDiceLetter(diceNumber);
                }
            }
            else
            {
                addDiceLetter(diceNumber);
            }
        }
    }

    /**
     * Clear the board and logic from selected dices.
     */
    private void clearBoard()
    {
        while (!dicesSelected.isEmpty())
        {
            int last = dicesSelected.pop();
            updateDiceUI(last);
        }
        currentCandidateWord = new StringBuilder();
        currentWord.setText(currentCandidateWord.toString());
    }

    private void clearScore()
    {
        scoreNumber = 0;
        score.setText(String.valueOf(0));
    }

    /**
     * Attemps to add the content of the dice to the current word.
     *
     * @param diceNumber
     */
    private void addDiceLetter(int diceNumber)
    {
        TextView current = diceViews.get(diceNumber - 1);
        currentCandidateWord.append(current.getText());
        currentWord.setText(currentCandidateWord.toString());
        dicesSelected.add(diceNumber);
        current.setBackgroundColor(Color.GREEN);
        current.setTextColor(Color.RED);
    }

    private CountDownTimer startCountDown() {
        return new CountDownTimer(GAME_TIME, TICK_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                timeLeft.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                LinearLayout matrix = (LinearLayout) findViewById(R.id.matrix);
                ;
                matrix.setVisibility(View.INVISIBLE);
            }

        }.start();
    }

    // FIXME : This is not sustainable,we should move to a method that takes a {matrix} and {i,j}.
    private boolean isAdjacent(int diceNumber, int lastPosition)
    {
        switch (diceNumber)
        {
            case 1:
                return ((lastPosition == 2) || (lastPosition == 4) || (lastPosition == 5));
            case 2:
                return ((lastPosition == 1) || (lastPosition == 3) || (lastPosition == 5)
                        || (lastPosition == 6) || lastPosition == 7);
            case 3:
                return lastPosition == 2 || lastPosition == 4 || lastPosition == 6
                        || lastPosition == 7 || lastPosition == 8;
            case 4:
                return lastPosition == 3 || lastPosition == 7 || lastPosition == 8;
            case 5:
                return lastPosition == 1 || lastPosition == 6 || lastPosition == 2
                        || lastPosition == 9 || lastPosition == 10;
            case 6:
                return lastPosition == 5 || lastPosition == 2 || lastPosition == 7
                        || lastPosition == 3 || lastPosition == 1 || lastPosition == 9
                        || lastPosition == 10 || lastPosition == 11;
            case 7:
                return lastPosition == 6 || lastPosition == 8 || lastPosition == 2
                        || lastPosition == 3 || lastPosition == 4 || lastPosition == 10
                        || lastPosition == 11 || lastPosition == 12;
            case 8:
                return lastPosition == 3 || lastPosition == 4 || lastPosition == 7
                        || lastPosition == 11 || lastPosition == 12;
            case 9:
                return lastPosition == 5 || lastPosition == 6 || lastPosition == 10
                        || lastPosition == 13 || lastPosition == 14;
            case 10:
                return lastPosition == 5 || lastPosition == 6 || lastPosition == 7
                        || lastPosition == 9 || lastPosition == 11 || lastPosition == 13
                        || lastPosition == 14 || lastPosition == 15;
            case 11:
                return lastPosition == 10 || lastPosition == 12 || lastPosition == 6
                        || lastPosition == 7 || lastPosition == 8 || lastPosition == 14
                        || lastPosition == 15 || lastPosition == 16;
            case 12:
                return lastPosition == 7 || lastPosition == 8 || lastPosition == 11
                        || lastPosition == 16 || lastPosition == 15;
            case 13:
                return lastPosition == 9 || lastPosition == 10 || lastPosition == 14;
            case 14:
                return lastPosition == 13 || lastPosition == 9 || lastPosition == 10
                        || lastPosition == 11;
            case 15:
                return lastPosition == 14 || lastPosition == 10 || lastPosition == 11
                        || lastPosition == 12 || lastPosition == 16;
            case 16:
                return lastPosition == 15 || lastPosition == 11 || lastPosition == 12;
            default:
                return false;

        }
    }
}
