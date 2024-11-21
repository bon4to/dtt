public class Player {
    private String nome;
    private int id;
    private int pos;

    public Player(String nome, int id) {
        this.nome = nome;
        this.id = id;
        this.pos = 0;
    }

    public String getNome() {
        return nome;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
