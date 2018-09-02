package com.example.marta.guessinggame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.marta.guessinggame.R.*;

public class MainActivity extends AppCompatActivity {
    private EditText txtGuess;
    private Button btnGuess;
    private TextView lblOutput;
    private int theNumber;
    private int numberOfTries;
    private int maxNumberOfTries = 7;
    private int range = 100;
    private TextView lblRange; // zmieena bo w tekscie już nie będzie wybierz liczbe z zakresu 1-100 tylko wybierz z wybranego zakresu

    public void checkGuess() {
        String guessText = txtGuess.getText().toString();
        int maxNumberOfTries = (int) (Math.log(range) / Math.log(2) + 1);
        String message = "";
        try {
            numberOfTries++;
            int guess = Integer.parseInt(guessText);
            if (guess < theNumber && (numberOfTries <= maxNumberOfTries))
                message = guess + " is too low. Try again";
            else if (guess > theNumber && numberOfTries <= maxNumberOfTries) {
                message = guess + " is too high. Try again";

            } else {
                message = guess + " is correct. You win after " + numberOfTries + " tries!";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                int gamesWon = preferences.getInt("gamesWon", 0) + 1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("gameWon", gamesWon);
                editor.apply();
                newGame();
            }
        } catch (Exception e) {
            message = "Enter a nubmer between 1 and " + range + ".";
        } finally {
            if ((numberOfTries > maxNumberOfTries)) {
                message = "Game Over! Let's play again!";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                int gamesLost = preferences.getInt("gamesLost", 0) + 1;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("gameLost", gamesLost);
                editor.apply();
                newGame();
            }


            lblOutput.setText(message);
            txtGuess.requestFocus(); //dzięki temu kursor znajduje sie w polu tekstowym
            txtGuess.selectAll(); // / zaznacza cały tekst w olu tekstowym co ułatwia korzystanie z gry -
        }
    }

    public void newGame() {
        theNumber = (int) (Math.random() * range + 1);//TU
        lblRange.setText("Enter a nubmer between 1 and " + range + ".");
        txtGuess.setText("" + range / 2);
        txtGuess.requestFocus(); //dzięki temu kursor znajduje sie w polu tekstowym
        txtGuess.selectAll();

        numberOfTries = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        txtGuess = (EditText) findViewById(id.txtGuess);
        // tym czyms łaczy sie elementy z GUI z javA,
        // R jak resource bo andro to są rzeczy z  res (czyli xml)
        btnGuess = (Button) findViewById(id.btnGuess);
        lblOutput = (TextView) findViewById(id.lblOutput);
        lblRange = (TextView) findViewById(id.textView2);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        range = preferences.getInt("range", 100);
        newGame();
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        txtGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkGuess();
                return false;
            }
        });


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

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:

                //poniżej kod do tworzenia okna z menu, charsequence to bardziej wypasiony string
                final CharSequence[] items = {"1 to 10", " 1 to 100", " 1 to 1000"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select the Range:");
                builder.setItems(items, new DialogInterface.OnClickListener() { // listener do wyboru range
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                range = 10;
                                storeRange(10);
                                newGame();
                                break;
                            case 1:
                                range = 100;
                                storeRange(100);
                                newGame();
                                break;
                            case 2:
                                range = 1000;
                                storeRange(1000);
                                newGame();
                                break;

                        }
                        dialog.dismiss();
                    }
                });


                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_newgame:
                return true;
            case R.id.action_gamestats:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((this));
                int gamesLost = preferences.getInt("gamesLost", 0);
                int gamesWon = preferences.getInt("gamesWon", 0);
                AlertDialog statDialog = new AlertDialog.Builder(MainActivity.this).create();

                statDialog.setTitle("Guessing Game Stats");
                statDialog.setMessage("You have won " + gamesWon + " out\n" + (gamesWon + gamesLost) + " games. It is\n " + (100 * gamesWon / (gamesWon + gamesLost)) + "%");
                statDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                statDialog.show();
                return true;
            case R.id.action_about:
                AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
                //powyżej stworzenie wyskakującego okna z alert dialogiem
                aboutDialog.setTitle("About Guessing Game"); //nadajem tytuł
                aboutDialog.setMessage("©Dawno Dawno Temu, Mistrz Yoda."); // info które będzie się wyświetlać
                aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", //przycisk OK
                        new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // dismiss znika okienko
                            }
                        });
                aboutDialog.show(); // polecenie pokazujace okienko dialogowe
                return true;


            default:
                return super.

                        onOptionsItemSelected(item);
        }
    }


    // cudo które pozwala na zapisanie w pamięci telefonu ustawieńzakresu liczb -zapis klucz-wartość
    // aplikacja ma domyślny obiekt przechowywania preferencji- do niego uzyskuje sie dostęp obiektem SharedPrferences,
    //obiekt
    public void storeRange(int newRange) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("range", newRange);
        editor.apply();

    }
}