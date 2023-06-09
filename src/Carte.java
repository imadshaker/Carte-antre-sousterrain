import java.awt.*;
import java.io.*;
import java.util.*;
import java.io.File;
/**
 * Cette classe représente un programme générant la carte d’un antre sousterrain
 *
 * @author Imad Shaker Bouarfa
 * Courriel: bouarfa.imad@courrier.uqam.ca
 * Cours: INF2120-010
 * @version 2023-02-17
 */
public class Carte {
    private String[][] matrice;

    private ArrayList<Mur> mursLibres;

    private ArrayList<Piece> piecesLibres;

    private ArrayList<Piece> piecesSurCarte;

    private int echec;

    public Carte(String fileName) {
        this.echec = 0;
        this.mursLibres = new ArrayList<>();
        this.piecesLibres = new ArrayList<>();
        this.piecesSurCarte = new ArrayList<>();
        this.matrice = remplireMatrice(fileName);
    }

    /**
     * Remplir la carte avec caractères et vérifier le nombre d'echecs
     *
     * @param fileName Prend le fichier text contenant les dimensions des pieces
     *
     * @return
     */
    private String[][] remplireMatrice(String fileName) {
        String[][] m = null;
        try {
            Scanner scanner = new Scanner(new File(fileName));

            m = remplireMatriceAvecCasesPleines(scanner);

            boolean pieceCentralePlacer = placerPieceCentrale(m, scanner);

            if (pieceCentralePlacer){
                lirePiecesLibres(scanner);
                while (echec<100 && piecesLibres.size()>0){
                    placerPieceAuHasard(m);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        return m;

    }

    /**
     * Placer les pieces au hasard dans la map et verifie que les pieces ne se touchent pas
     *
     * @param m Prend en parametre la map remplie avec des cases pleines
     */
    private void placerPieceAuHasard(String[][] m) {
        Piece pieceSurCarte = piecesSurCarte.get(new Random().nextInt(piecesSurCarte.size()));
        Mur mur;
        do{
            mur = pieceSurCarte.getMurs()[new Random().nextInt(pieceSurCarte.getMurs().length)];
        } while (!mursLibres.contains(mur));

        Piece piece = this.piecesLibres.get(new Random().nextInt(piecesLibres.size()));

        Point porteMur = new Point(new Random().nextInt(mur.getBase()), new Random().nextInt(mur.getHauteur()));
        Point portePiece = new Point(new Random().nextInt(piece.getBase()), new Random().nextInt(piece.getHauteur()));

        int xPorte=-1;
        int yPorte=-1;

        if (mur.getBase()>1){
            xPorte = mur.getX() + porteMur.x;
            yPorte = mur.getY();
        } else {
            xPorte = mur.getX();
            yPorte = mur.getY() + porteMur.y;
        }

        int xPiece = xPorte-portePiece.x;
        int yPiece = yPorte-portePiece.y;


        piece.setPosition(new Point(xPiece, yPiece));


        while(piece.enConflit(pieceSurCarte)){
            deplacer(mur,piece,pieceSurCarte);
        }

        //Verifier si la piece à ajouter n'est pas au dessus d'une autre meme apres le deplacement
        boolean bienPlace = pieceEstBienPlace(piece);

        if (!piece.horsCarte(m) && bienPlace){
            piecesSurCarte.add(piece);
            piecesLibres.remove(piece);
            addMursPiece(mur, piece);
            mursLibres.remove(mur);
            updateMatrice(m, xPorte, yPorte, mur, pieceSurCarte);
        } else {
            echec++;
            piece.setPosition(new Point(0,0));
        }


    }

    /**
     * Ajouter les murs aux pieces
     *
     * @param mur   Prend en parametre les dimensions des murs
     * @param piece Prend en parametre les dimensions des pieces
     */
    private void addMursPiece(Mur mur, Piece piece) {
        Mur[] mursDeLaPiece = piece.creerMurs();
        for (Mur m : mursDeLaPiece){
            if(m.getBase()==1 && mur.getBase()==1 && m.getX()!=mur.getX()){
                this.mursLibres.add(m);
            } else if (m.getHauteur()==1 && mur.getHauteur()==1 && m.getY()!=mur.getY()){
                this.mursLibres.add(m);
            } else {
                this.mursLibres.add(m);
            }
        }
    }

    /**
     * Verifie que la piece n'est pas en conflit avec une autre
     *
     * @param piece Prend en parametre les dimensions de la piece
     * @return
     */
    private boolean pieceEstBienPlace(Piece piece) {
        boolean bienPlace = true;
        for (Piece p : piecesSurCarte){
            if(piece.enConflit(p)){
                bienPlace  = false;
            }
        }
        return bienPlace;
    }

    /**
     *Effectue le deplacement des pieces
     *
     * @param mur Prend en parametre les dimensions des murs
     * @param piece Prend en parametre les dimensions des pieces
     * @param p
     */
    private void deplacer(Mur mur, Piece piece, Piece p) {
        if(mur.getY()==p.getY()){
            //mur vertical ==> déplacement à gauche ou à droite
            if(piece.getX()< p.getX()){
                //piece a gauche de p
                piece.deplacerGauche();
            } else {
                piece.deplacerDroit();
            }
        } else {
            //mur horizental ==> déplacement en haut ou en bas
            if (piece.getY()< p.getY()){
                //piece au dessus de p
                piece.deplacerHaut();
            } else {
                //piece au dessous de p
                piece.deplacerBas();
            }
        }
    }

    /**
     * Mettre a jour la map avec des caracteres
     *
     * @param m Prend en parametre la map remplie avec des cases pleines
     * @param xPorte Prend en parametre les positions de la porte
     * @param yPorte Prend en parametre les positions de la porte
     * @param mur Prend en parametre la position des murs
     * @param pieceSurCarte Prend en parametre les pieces sur la carte
     */
    private void updateMatrice(String[][] m, int xPorte, int yPorte, Mur mur, Piece pieceSurCarte) {
        for (Piece piece : piecesSurCarte){
            for (int i=piece.getX(); i<piece.getX()+piece.getBase(); i++){
                for (int j=piece.getY(); j<piece.getY()+piece.getHauteur(); j++){
                    m[i][j] = ".";
                }
            }
        }

        if (mur.getX()==pieceSurCarte.getX()){ //mur horizontal donc porte -
            m[xPorte][yPorte] = "-";
        } else {
            //mur vertical donc porte |
            m[xPorte][yPorte] = "|";
        }

    }

    /**
     * Lecture des pieces non utilisées
     *
     * @param scanner Prend en parametre le fichier text
     */
    private void lirePiecesLibres(Scanner scanner) {
        while (scanner.hasNext()){
            int base = scanner.nextInt();
            int hauteur = scanner.nextInt();
            Piece piece = new Piece(base, hauteur);
            this.piecesLibres.add(piece);
        }
    }

    /**
     * Remplie la carte avec le caractere 'O'
     *
     * @param scanner Prend en parametre le fichier text
     * @return
     */
    private String[][] remplireMatriceAvecCasesPleines(Scanner scanner) {
        String[][] m;
        int base = scanner.nextInt();
        int hauteur = scanner.nextInt();
        m = new String[base][hauteur];
        for (int i=0; i<base; i++){
            for (int j=0; j<hauteur; j++){
                m[i][j] = "O";
            }
        }
        return m;
    }

    /**
     * Placer la piece centrale
     *
     * @param m Prend en parametre la map remplie avec des cases pleines
     * @param scanner Prend en parametre le fichier text
     * @return
     */
    private boolean placerPieceCentrale(String[][] m, Scanner scanner) {
        int basePC = scanner.nextInt();
        int hauteurPC = scanner.nextInt();
        int x = (m.length-basePC)/2;
        int y = (m[0].length-hauteurPC)/2;

        if (basePC>m.length || hauteurPC>m[0].length){
            return false;
        }

        Piece pieceCentrale = new Piece(x, y, basePC, hauteurPC);

        for (int i=pieceCentrale.getX(); i<pieceCentrale.getX()+pieceCentrale.getBase(); i++){
            for (int j=pieceCentrale.getY(); j<pieceCentrale.getY()+pieceCentrale.getHauteur(); j++){
                m[i][j] = ".";
            }
        }
        piecesSurCarte.add(pieceCentrale);

        Mur[] mursPieceCentrale = pieceCentrale.creerMurs();
        for (Mur mur : mursPieceCentrale){
            mursLibres.add(mur);
        }
        return true;
    }

    /**
     * Dessine la carte
     */
    public void dessiner() {
        for (int j = 0; j < matrice[0].length; j++) {
            for (int i = 0; i < matrice.length; i++) {
                System.out.print(matrice[i][j]);
            }
            System.out.println();
        }
    }
}
