package fr.ups.sim.superpianotiles;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import fr.ups.sim.superpianotiles.events.TileAdapter;
import fr.ups.sim.superpianotiles.events.TileCounter;
import fr.ups.sim.superpianotiles.events.TileEvent;

public class TilesStartActivity extends Activity {


    class MonAction extends TimerTask {
        private PianoTiles game;
        private TilesView t;

        public MonAction(PianoTiles game, TilesView t){
            this.game = game;
            this.t = t;
        }

        public void run() {
            System.err.println("Ajout Tuile dans la liste");
            this.game.newTile();
            senseur.fireNbTileChanged(this.game.getTiles().size());
            this.t.setGame(this.game);
            this.t.postInvalidate();
        }


    }

    private PianoTiles game;
    private TilesView tilesView;
    private Timer t;
    private MediaPlayer music = null;
    private MediaPlayer fail;
    private TileCounter senseur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        createMenu(Difficulte.MOYEN, R.raw.cloud_atlas);
        game.setIdMusic(R.raw.cloud_atlas);
        

    }




    public void createMenu(final Difficulte difficulte, final int musique) {

        setContentView(R.layout.menu_start);

        this.game = new PianoTiles();
        this.game.setDifficulte(difficulte);
        this.game.setIdMusic(musique);

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(difficulte,musique);
            }
        });
    }

    public void createGame(Difficulte difficulte,int musique){

        setContentView(R.layout.activity_tiles_start);
        if(music != null){
            if(music.isPlaying()){
                music.stop();}}
        music = MediaPlayer.create(this,musique);
        music.start();
        this.game = new PianoTiles();
        this.game.setDifficulte(difficulte);
        this.game.setIdMusic(musique);


        //On récupère la view (JFrame en SWING) du jeu
        this.tilesView = (TilesView) findViewById(R.id.view);


        //On met en place un listener qui réagira lorsque l'on touchera l'écran tactile
        this.tilesView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEventHandler(event);

            }
        });



        this.t = new Timer() ;
        int delay = 0 ;
        long period;

        switch (difficulte.ordinal()) {
            case 0:
                period = 750;
                break;
            case 1:
                period = 500;
                break;
            case 2:
                period = 300;
                break;
            default:
                period = 500;

        }

        t.schedule(
                new MonAction(this.game, this.tilesView),
                delay,
                period) ;


        //Compteur de tuile et son listener
        this.senseur = new TileCounter();
        senseur.addTemperatureListener(new TileAdapter() {
            @Override
            public void nbTileChanged(TileEvent event) {
                System.err.println("Le nombre de tuile a changé" + event.getNbTiles());

                if (event.getNbTiles() == 20) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameOver();
                        }
                    });
                }
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tiles_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // ICI - A compléter pour déclencher l'ouverture de l'écran de paramétrage

            t.cancel();
            //music.stop();
            setContentView(R.layout.settingbis);
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
            radioGroup.check(R.id.radioButton2);

            //Pré sélectionne le radioButton selon la difficulté courante
            switch (game.getDifficulte()){
                case 0:
                    radioGroup.check(R.id.radioButton);
                    break;
                case 1:
                    radioGroup.check(R.id.radioButton2);
                    break;
                case 2:
                    radioGroup.check(R.id.radioButton3);
                    break;
                default:
                    radioGroup.check(R.id.radioButton2);

            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // checkedId is the RadioButton selected

                    switch (checkedId) {
                        case R.id.radioButton:

                            // Facile
                            game.setDifficulte(Difficulte.FACILE);

                            break;
                        case R.id.radioButton2:

                            // Moyen
                            game.setDifficulte(Difficulte.MOYEN);

                            break;
                        case R.id.radioButton3:
                            //Difficile
                            game.setDifficulte(Difficulte.DIFFICILE);
                            break;
                    }

                        createGame(Difficulte.values()[game.getDifficulte()], game.getIdMusic());
                }
            });

            final ImageButton sound = (ImageButton) findViewById(R.id.sound);
            sound.setTag("music");
            sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sound.getTag() == "music") {
                        music.pause();
                        sound.setImageResource(R.drawable.mute);
                        sound.setTag("mute");
                    } else {
                        music.start();
                        sound.setImageResource(R.drawable.music);
                        sound.setTag("music");
                    }
                }
            });


            final ListView playlist = (ListView)findViewById(R.id.listView);
            playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //music.stop();
                    //MediaPlayer musique = null;
                    int musique = 0;
                    switch (((TextView)view).getText().toString()){
                        case("Sextet - Cloud Atlas Soundtrack"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.cloud_atlas);
                            musique = R.raw.cloud_atlas;
                            break;
                        case("Let it Be - Beatles"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.beatles);
                            musique = R.raw.beatles;

                            break;
                        case("Obstacles - Syd Matters"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.obstacles);
                            musique = R.raw.obstacles;
                            break;
                        case("Lean On - Major Lazer ft. DJ Snake"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.lean_on);
                            musique = R.raw.lean_on;
                            break;
                        case("See You Again - Wiz Khalifa"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.see_you_again);
                            musique = R.raw.see_you_again;
                            break;
                        default : break;

                    }
                        createGame(Difficulte.values()[game.getDifficulte()], musique);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Handler utilisé lorsque le joueur touche l'écran
     */
    private boolean onTouchEventHandler (MotionEvent evt){
        Log.i("TilesView", "Touch event handled");

        switch (evt.getAction()) {
            case MotionEvent.ACTION_DOWN:


                if (this.game.isCorrectTileTouched(evt.getX(), evt.getY(), this.tilesView.getBottom(),
                            this.tilesView.getWidth()))
                {

                    this.tilesView.setGame(this.game);
                    this.game.incrementeScore();
                    this.tilesView.invalidate();
                 }
                else {
                    gameOver();
                }
        }

        return true;
    }

    public void gameOver() {
        this.t.cancel();

        music.stop();
        fail =  MediaPlayer.create(this,R.raw.crash);
        fail.start();


        setContentView(R.layout.game_over_bis);
        ((TextView)findViewById(R.id.textView2)).setText("Your score is " + this.game.getScore());
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createGame(Difficulte.values()[game.getDifficulte()], game.getIdMusic());
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenu(Difficulte.values()[game.getDifficulte()], game.getIdMusic());
            }
        });
    }

}
