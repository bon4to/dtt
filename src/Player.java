public class Player {
    private String nome;
    private int id;
    private int pos;
    private boolean idle;

    private int turnCounter;

    public Player(String nome, int id) {
        this.nome = nome;
        this.id = id;
        this.pos = -1;
        this.idle = false;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void toggleIdle(boolean idle) {
        this.idle = idle;
    }

    public boolean isIdle() {
        return idle;
    }

    public boolean checkTurn() {
        turnCounter++;
        if (turnCounter % 4 == 0) {
            return true;
        }
        return false;
    }
}
