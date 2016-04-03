package fr.ups.sim.superpianotiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import fr.ups.sim.superpianotiles.events.TileAdapter;
import fr.ups.sim.superpianotiles.events.TileCounter;
import fr.ups.sim.superpianotiles.events.TileEvent;

public class Mode1Activity extends Activity {



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

    class Defilement extends TimerTask
    {
        private PianoTiles game ;
        private TilesView t ;
        private long cycles = 0;
        private long period;

        public Defilement(PianoTiles game, TilesView t, long period)
        {
            this.game = game ;
            this.t = t ;
            this.period = period;
        }

        public void run()
        {
            this.cycles ++;

            if (!this.game.getTiles().isEmpty())
            {

                for (Tiles tile : this.game.getTiles())
                {
                    tile.defile(this.game.getDifficulte());
                    //TODO
                    if (tile.getTop() > 3.99f)
                    {
                        System.err.println("tuile sortie lololololol");
                        this.game.perteVie();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                majVie();
                            }
                        });

                        this.game.removeNextTile();
                        this.game.setNextTile();

                        if (this.game.getVie() == 0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gameOver();
                                }
                            });
                        }
                    }
                }


            }
            if (cycles == period){
                System.err.println("Ajout Tuile dans la liste");
                this.game.newTile();
                senseur.fireNbTileChanged(this.game.getTiles().size());
                this.cycles = 0;
            }
            this.t.setGame(this.game);
            this.t.postInvalidate();
        }
    }

    private PianoTiles game;
    private TilesView tilesView;
    private Timer timer, timer2 ;
    private MediaPlayer music = null;
    private MediaPlayer fail;
    private TileCounter senseur;
    private int debutMilliSec;
    private int debutSec;
    private int debutMin;
    private int finMilliSec;
    private int finSec;
    private int finMin;
    private Calendar c;
    private float meilleurTemps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String mode = "Mode";

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        Log.d("STATE",intent.getStringExtra(mode));
        System.err.println(intent.getStringExtra(mode));


        if (intent.getStringExtra(mode).equals("def")) {
            createGame(Difficulte.MOYEN, R.raw.cloud_atlas, ModeDeJeu.DEFILEMENT);
            System.err.println("DEFILEMENT LOLOLOLOLOLOLOLOLOL");
            Log.d("STATE","DEFILEMENT");
        }
        else if (intent.getStringExtra(mode).equals("stat"))
            createGame(Difficulte.MOYEN, R.raw.cloud_atlas, ModeDeJeu.STATIQUE);
        else
            createGame(Difficulte.MOYEN, R.raw.cloud_atlas, ModeDeJeu.CHRONO);


        game.setIdMusic(R.raw.cloud_atlas);


    }


    public void majVie()
    {
        ((TextView)findViewById(R.id.vies)).setText("Vie(s) : " + this.game.getVie());
    }



    public void createGame(Difficulte difficulte,int musique, ModeDeJeu m){

        setContentView(R.layout.activity_tiles_start);
        if(music != null){
            if(music.isPlaying()){
                music.stop();}}
        music = MediaPlayer.create(this,musique);
        music.start();
        this.game = new PianoTiles(m);
        this.game.setDifficulte(difficulte);
        this.game.setIdMusic(musique);


        //On récupère la view (JFrame en SWING) du jeu
        this.tilesView = (TilesView) findViewById(R.id.view);

        if(!m.equals(ModeDeJeu.DEFILEMENT))
            ((TextView)findViewById(R.id.vies)).setText("");

        //On met en place un listener qui réagira lorsque l'on touchera l'écran tactile
        this.tilesView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchEventHandler(event);

            }
        });


        c = Calendar.getInstance();
        System.err.println(this.c.toString()) ;
        this.debutMilliSec = c.get(Calendar.MILLISECOND) ;
        this.debutSec = c.get(Calendar.SECOND);
        this.debutMin = c.get(Calendar.MINUTE) ;

        long period;

        switch (difficulte.ordinal()) {
            case 0:
                if (this.game.getMode() == ModeDeJeu.STATIQUE) {
                    period = 750;
                }
                else
                {
                    period = 1000 ;
                }
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


        int delay = 0;
        if (this.game.getMode().equals(ModeDeJeu.STATIQUE)) {
            this.timer = new Timer();



            timer.schedule(
                    new MonAction(this.game, this.tilesView),
                    delay,
                    period);

            creerSenseur(20,this.game.getMode());
        }
        else if (this.game.getMode().equals(ModeDeJeu.DEFILEMENT)) {
            this.timer2 = new Timer();


                System.err.println("Dans if");
                timer2.schedule(
                        new Defilement(this.game, this.tilesView, period),
                        delay,
                        1);

            creerSenseur(20,this.game.getMode());

        }
        else {
            this.game.fullScreenTiles();
            this.tilesView.setGame(this.game);

            creerSenseur(0,this.game.getMode());
        }



    }


    //Creer un listener sur une variable
    public void creerSenseur(final int nbTile, final ModeDeJeu mode) {
        //Compteur de tuile et son listener
        this.senseur = new TileCounter();
        senseur.addTemperatureListener(new TileAdapter() {
            @Override
            public void nbTileChanged(TileEvent event) {
                System.err.println("Le nombre de tuile a changé" + event.getNbTiles());

                if (event.getNbTiles() == nbTile) {
                    if (!mode.equals(ModeDeJeu.CHRONO))
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameOver();
                            }
                        });
                    else
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                termine();
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

            if (this.game.getMode().equals(ModeDeJeu.STATIQUE))
                this.timer.cancel();
            else
                this.timer2.cancel();
            //music.stop();
            setContentView(R.layout.settingbis);
            final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
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
                            if(game.getMode() == ModeDeJeu.DEFILEMENT){
                                //si l'utilisateur veut jouer defilement en difficile
                                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Mode1Activity.this);
                                dlgAlert.setMessage("\"Difficile\" is way to harcore for you");
                                dlgAlert.setTitle("App Title");
                                dlgAlert.setPositiveButton("OK", null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                                radioGroup.check(R.id.radioButton2);
                            }else {
                                game.setDifficulte(Difficulte.DIFFICILE);
                                break;
                            }
                    }

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

            final int[] musique = {R.raw.cloud_atlas};
            final ListView playlist = (ListView)findViewById(R.id.listView);
            playlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //music.stop();
                    //MediaPlayer musique = null;

                    switch (((TextView)view).getText().toString()){
                        case("Sextet - Cloud Atlas Soundtrack"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.cloud_atlas);
                            musique[0] = R.raw.cloud_atlas;
                            break;
                        case("Let it Be - Beatles"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.beatles);
                            musique[0] = R.raw.beatles;

                            break;
                        case("Obstacles - Syd Matters"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.obstacles);
                            musique[0] = R.raw.obstacles;
                            break;
                        case("Lean On - Major Lazer ft. DJ Snake"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.lean_on);
                            musique[0] = R.raw.lean_on;
                            break;
                        case("See You Again - Wiz Khalifa"):
                            //musique = MediaPlayer.create(TilesStartActivity.this,R.raw.see_you_again);
                            musique[0] = R.raw.see_you_again;
                            break;
                        default : break;

                    }
                }
            });

            final Button continuer = (Button)findViewById(R.id.button2);

            continuer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createGame(Difficulte.values()[game.getDifficulte()], musique[0], game.getMode());

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

                    if (this.game.getMode() == ModeDeJeu.CHRONO)
                    {
                        senseur.fireNbTileChanged(this.game.getTiles().size());
                    }

                }
                else {
                    gameOver();
                }
        }

        return true;
    }

    public void gameOver() {
        if (this.game.getMode().equals(ModeDeJeu.STATIQUE))
            this.timer.cancel();
        else if (this.game.getMode().equals(ModeDeJeu.DEFILEMENT))
            this.timer2.cancel();

        music.stop();
        fail =  MediaPlayer.create(this, R.raw.crash);
        fail.start();


        setContentView(R.layout.game_over_bis);

        if (!this.game.getMode().equals(ModeDeJeu.CHRONO) )
            ((TextView)findViewById(R.id.textView2)).setText("Your score is " + this.game.getScore());
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(Difficulte.values()[game.getDifficulte()], game.getIdMusic(), game.getMode());
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mode1Activity.this, TilesStartActivity.class);
                startActivity(intent);            }
        });
    }


    public void termine()
    {
        c = Calendar.getInstance();
        Integer tempsSec ;
        Integer tempsMilliSec ;
        String temps ;
        this.finMilliSec = c.get(Calendar.MILLISECOND) ;
        this.finSec = c.get(Calendar.SECOND);
        this.finMin = c.get(Calendar.MINUTE) ;
        if (this.finMin < debutMin)
            this.finMin += 60 ;
        int diffMin = this.finMin - this.debutMin ;
        int diffSec = this.finSec - this.debutSec ;
        int diffMilliSec = this.finMilliSec - this.debutMilliSec ;
        System.err.println(this.debutSec + " et " + this.finSec) ;
        if (diffMilliSec < 0)
        {
            tempsMilliSec = 1000 - this.debutMilliSec + this.finMilliSec ;
            this.finSec-- ;
        }
        else
        {
            tempsMilliSec = this.finMilliSec - this.debutMilliSec ;
        }
        if (diffSec < 0)
        {
            tempsSec = 60 - this.debutSec + (diffMin-1)*60 + this.finSec ;
        }
        else
        {
            tempsSec = this.finSec - this.debutSec ;
        }

        temps = Integer.toString(tempsSec) + "." + Integer.toString(tempsMilliSec) ;

        System.err.println(tempsSec + " et  " + tempsMilliSec) ;



        music.stop();

        setContentView(R.layout.termine);
        ((TextView)findViewById(R.id.temps)).setText("Votre temps : " + temps + "sec");
        if (Float.parseFloat(temps) < this.meilleurTemps || this.meilleurTemps == 0)
            this.meilleurTemps = Float.parseFloat(temps) ;
        ((TextView)findViewById(R.id.meilleurtemps)).setText("Meilleur temps : " + meilleurTemps + "sec");

        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(Difficulte.values()[game.getDifficulte()], game.getIdMusic(), game.getMode());
            }
        });
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mode1Activity.this, TilesStartActivity.class);
                startActivity(intent);
            }
        });



    }


}
