package com.inja.boggle;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ShakeDetector.ShakeCallBack,View.OnClickListener{

    @Bind({ R.id.position_1, R.id.position_2, R.id.position_3,R.id.position_4, R.id.position_5, R.id.position_6,R.id.position_7, R.id.position_8, R.id.position_9,R.id.position_10, R.id.position_11, R.id.position_12,R.id.position_13, R.id.position_14, R.id.position_15,R.id.position_16 })
    List<DiceView> diceViews;

    @OnClick({ R.id.position_1, R.id.position_2, R.id.position_3,R.id.position_4, R.id.position_5, R.id.position_6,R.id.position_7, R.id.position_8, R.id.position_9,R.id.position_10, R.id.position_11, R.id.position_12,R.id.position_13, R.id.position_14, R.id.position_15,R.id.position_16 })
    public void onClick(View v) {
        DiceView diceView = (DiceView) v;
        Log.d("T","Dice pressed , "+diceView.getDiceNumber());
        dicePressed(diceView.getDiceNumber());
    }

    @Bind(R.id.current_word)
    TextView currentWord;

    @Bind(R.id.score)
    TextView score;

    @Bind(R.id.time_left)
    TextView timeLeft;

    int scoreNumber = 0;

    Animation inflatingAnimation;

    Game game;

    Stack<Integer> dicesSelected;

    Set<Integer> selected;

    StringBuilder st = new StringBuilder();

    ShakeDetector shakeDetector;

    private void generateNewBoard()
    {
        for ( int i = 0 ; i < diceViews.size() ; ++i ){
            final DiceView diceView = diceViews.get(i);
            diceView.setText(game.getCharacter(i + 1));
            diceView.startAnimation(inflatingAnimation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatingAnimation = AnimationUtils.loadAnimation(this, R.anim.myanimation);
        new CountDownTimer(60*10000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLeft.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                LinearLayout matrix = (LinearLayout) findViewById(R.id.matrix);;
                matrix.setVisibility(View.INVISIBLE);
            }

        }.start();
        setContentView(R.layout.activity_main);
        game = new Game(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ButterKnife.bind(this);
        generateNewBoard();
        dicesSelected = new Stack<>();
        selected = new HashSet<>();
        setSupportActionBar(toolbar);
        shakeDetector = new ShakeDetector(this);

    }


    //Register the Listener when the Activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG","resumed");
        shakeDetector.register(this);
    }

    //Unregister the Listener when the Activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "paused");
        shakeDetector.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.roll:
                while ( !dicesSelected.isEmpty()){
                    int last =dicesSelected.pop();
                    selected.remove(last);
                    revertUpdate(last);
                }
                st = new StringBuilder();
                currentWord.setText(st.toString());
                    generateNewBoard();
                break;
            case R.id.submit:

                if ( game.isWord(st.toString())){
                    updatePoints(st.length());
                }
                else{
                    Toast.makeText(getApplicationContext(),"No words",Toast.LENGTH_SHORT).show();
                    while ( !dicesSelected.isEmpty()){
                        int last =dicesSelected.pop();
                        selected.remove(last);
                        revertUpdate(last);
                    }
                    st = new StringBuilder();
                    currentWord.setText(st.toString());

                }
                break;
            case R.id.back:
                if ( st.length() > 0 ){
                    st = new StringBuilder(st.substring(0,st.length()-1));
                    currentWord.setText(st.toString());
                    int l = dicesSelected.pop();
                    selected.remove(l);
                    revertUpdate(l);

                }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void revertUpdate(int l) {
        TextView dice = diceViews.get(l - 1);
        dice.setTextColor(Color.BLACK);
        dice.setBackgroundColor(Color.parseColor("#ffe3e3"));
    }

    private void updatePoints(int p) {
        Toast.makeText(this,"YOU WON ",Toast.LENGTH_SHORT).show();
        while ( !dicesSelected.isEmpty()){
            int last =dicesSelected.pop();
            selected.remove(last);
            revertUpdate(last);
        }
        st = new StringBuilder();
        currentWord.setText(st.toString());
        scoreNumber+=p;
        score.setText(String.valueOf(scoreNumber));

    }



    private void dicePressed(int diceNumber)
    {
        Log.d("t", diceNumber + " = " + selected.size() + " and " + dicesSelected.size());
        if ( !selected.contains(diceNumber)) {
            if (!dicesSelected.isEmpty()) {
                    int last = dicesSelected.peek();
                    Log.d("d", "Processing = " + diceNumber + " last " + last);
                    if (isAdjacent(diceNumber, last)) {
                        addLetter(diceNumber);
                    }

            } else {
                addLetter(diceNumber);

            }
        }
    }

    private void addLetter(int diceNumber) {
        TextView current = diceViews.get(diceNumber - 1);
        st.append(current.getText());
        currentWord.setText(st.toString());
        dicesSelected.add(diceNumber);
        selected.add(diceNumber);
        current.setBackgroundColor(Color.GREEN);
        current.setTextColor(Color.RED);
    }

    //FIXME : This is not sustainble,we should move to a method that takes a {matrix} and {i,j}, By doing so we can expand our grid without writting new logic.
    private boolean isAdjacent(int diceNumber, int lastPosition) {
        switch (diceNumber)
        {
            case 1:
                return ((lastPosition == 2) || (lastPosition == 4 )|| (lastPosition == 5 ));
            case 2:
                return ((lastPosition == 1) || (lastPosition == 3 )|| (lastPosition == 5 ) || ( lastPosition == 6) || lastPosition == 7);
            case 3:
                return lastPosition == 2 || lastPosition == 4 || lastPosition == 6 || lastPosition == 7 || lastPosition == 8 ;
            case 4:
                return lastPosition == 3 || lastPosition == 7 || lastPosition == 8;
            case 5:
                return lastPosition == 1 || lastPosition == 6 || lastPosition == 2 || lastPosition == 9 || lastPosition == 10;
            case 6:
                return lastPosition == 5 || lastPosition == 2 || lastPosition == 7 || lastPosition == 3 || lastPosition == 1 || lastPosition == 9 || lastPosition == 10 || lastPosition == 11;
            case 7:
                return lastPosition == 6 || lastPosition == 8 || lastPosition == 2 || lastPosition == 3 || lastPosition == 4 || lastPosition == 10 || lastPosition == 11 || lastPosition == 12 ;
            case 8:
                return lastPosition == 3 || lastPosition == 4 || lastPosition == 7 || lastPosition == 11 || lastPosition == 12 ;
            case 9:
                return lastPosition == 5 || lastPosition == 6 || lastPosition == 10 || lastPosition == 13 || lastPosition == 14 ;
            case 10:
                return lastPosition == 5 || lastPosition == 6 || lastPosition == 7 || lastPosition == 9 || lastPosition == 11 || lastPosition == 13 || lastPosition == 14 || lastPosition == 15;
            case 11:
                return lastPosition == 10 || lastPosition == 12 || lastPosition == 6 || lastPosition == 7 || lastPosition == 8 || lastPosition == 14 || lastPosition == 15 || lastPosition == 16;
            case 12:
                return lastPosition == 7 || lastPosition == 8 || lastPosition == 11 || lastPosition == 16 || lastPosition == 15;
            case 13:
                return lastPosition == 9 || lastPosition == 10 || lastPosition == 14;
            case 14:
                return lastPosition == 13 || lastPosition == 9 || lastPosition == 10 || lastPosition == 11 ;
            case 15:
                return lastPosition == 14 || lastPosition == 10 || lastPosition == 11 || lastPosition == 12 || lastPosition == 16;
            case 16:
                return lastPosition == 15 || lastPosition == 11 || lastPosition == 12 ;
            default:
                return false;

        }
    }

    @Override
    public void onShake() {
        generateNewBoard();
    }
}
