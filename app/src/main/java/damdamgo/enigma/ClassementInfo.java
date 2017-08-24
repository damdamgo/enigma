package damdamgo.enigma;



/**
 * Created by Poste on 14/02/2016.
 */
public class ClassementInfo {

    private String pseudo;
    private String score;

    public ClassementInfo(String pseudo,String score){
        this.pseudo = pseudo;
        this.score = score;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getScore() {
        return score;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String toString(){
        return pseudo+" || "+score;
    }
}
