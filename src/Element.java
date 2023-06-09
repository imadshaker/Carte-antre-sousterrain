import java.awt.*;
/**
 * Cette classe représente un programme générant la carte d’un antre sousterrain
 *
 * @author Imad Bouarfa
 * Code permanent: BOUI24039303
 * Courriel: bouarfa.imad@courrier.uqam.ca
 * Cours: INF2120-010
 * @version 2023-02-17
 */
public abstract class Element {
    private Point position;
    private int base;
    private int hauteur;

    public Element(int base, int hauteur){
        this.base = base;
        this.hauteur = hauteur;
    }
    public Element(int x, int y, int base, int hauteur) {
        this.position = new Point(x,y);
        this.base = base;
        this.hauteur = hauteur;
    }


    public Point getPosition() {
        return position;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public int getBase() {
        return base;
    }

    public int getHauteur() {
        return hauteur;
    }

    @Override
    public String toString() {
        return "Element{" +
                "position=" + position +
                ", base=" + base +
                ", hauteur=" + hauteur +
                '}';
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void deplacerDroit(){
        this.position.x++;
    }
    public void deplacerBas(){
        this.position.y++;
    }
    public void deplacerGauche(){
        this.position.x--;
    }
    public void deplacerHaut(){
        this.position.y--;
    }


}
