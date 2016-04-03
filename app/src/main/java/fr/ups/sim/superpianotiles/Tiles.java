package fr.ups.sim.superpianotiles;


/**
 * Created by mathieukostiuk on 22/03/2016.
 */
public class Tiles {
    private String order;
    private float left;
    private float top;
    private float right;
    private float bottom;

    public Tiles(String order, float top, float left) {
        this.order = order;
        this.left = left;
        this.top = top;
        this.right = 4-left;
        this.bottom = 3-top;
    }

    @Override
    public String toString() {
        return this.order;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tiles){
            Tiles compare = (Tiles) o;
            float[] posThis = this.getPos();
            float[] posComp = compare.getPos();
            return ((posThis[1] == posComp[1]) && (posThis[2] == posComp[2]) &&
                    (posThis[0] == posComp[0]) && (posThis[3] == posComp[3]));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (19);
    }



    public float[] getPos() {
        return new float[]{this.left, this.top, this.right, this.bottom};
    }

    public float getBottom()
    {
        return this.bottom ;
    }



    public void defile(int difficulte)
    {
        switch(difficulte) {
            case 0 :
                this.top = this.top +0.0075f ;
                break ;
            case 1 :
                this.top = this.top + 0.01f;
                break ;
            case 2 :
                this.top = this.top + 0.013f;
                break ;
            default :
                this.top = this.top + 0.0075f ;
                break ;
        }
        this.bottom = 3 - this.top ;
    }
}
