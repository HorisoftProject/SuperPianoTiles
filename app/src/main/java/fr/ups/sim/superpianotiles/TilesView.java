package fr.ups.sim.superpianotiles;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;



/**
 * Custom view that displays tiles
 */
public class TilesView extends View {

    private Random rand = new Random() ;
    private int tileColor = Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    private int textColor = Color.WHITE;
    private Drawable mExampleDrawable;
    private float textSize = 40;
    Paint pText = new Paint();
    Paint pTile = new Paint();
    private PianoTiles game = new PianoTiles(ModeDeJeu.DEFILEMENT);



    public TilesView(Context context) {
        super(context);
        init(null, 0);
    }

    public TilesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TilesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TilesView, defStyle, 0);


        if (a.hasValue(R.styleable.TilesView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.TilesView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        pText.setTextSize(textSize);
        pText.setColor(textColor);
        pTile.setColor(tileColor);

        float left,top,right,bottom;

        canvas.drawLine(0,0,1,getHeight(),pTile);
        canvas.drawLine(getWidth()/5,0,getWidth()/5+1,getHeight(),pTile);
        canvas.drawLine(2*getWidth()/5,0,2*getWidth()/5+1,getHeight(),pTile);
        canvas.drawLine(3*getWidth()/5,0,3*getWidth()/5+1,getHeight(),pTile);
        canvas.drawLine(4*getWidth()/5,0,4*getWidth()/5+1,getHeight(),pTile);
        canvas.drawLine(getWidth()-1,0,getWidth(),getHeight(),pTile);



        //System.err.println(this.game.getTiles().isEmpty());

        if (!this.game.getTiles().isEmpty())
            for (Tiles t : this.game.getTiles()) {
                //System.err.println("On dessine une tuile");
                float[] tab = t.getPos();
                left = getWidth()*tab[0]/5;
                top = getBottom()* tab[1]/4;
                right = getWidth()-getWidth() *tab[2]/5;
                bottom = getBottom() - getBottom()*tab[3]/4;
                addTile(t.toString(), new RectF(left, top, right, bottom), canvas);

            }

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }

    public void addTile(String order, RectF rect, Canvas canvas){
        canvas.drawRoundRect(rect, 2, 2, pTile);
        canvas.drawText(order, rect.centerX(), rect.centerY(),pText);
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    public void setGame(PianoTiles game) {
        this.game = game;
    }



    public boolean isBottom(Tiles tile) {

        return (getBottom() - getBottom()*tile.getPos()[3]/4 == 0);
    }
}
