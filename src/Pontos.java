import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.image.ImageView;

public class Pontos {
    final byte ESTILO_PADRAO = 0;
    final byte ESTILO_PARARODADA = 1;
    final byte ESTILO_AVANCADOIS = 2;
    final byte ESTILO_VOLTAINICIO = 3;

    byte estilo = ESTILO_PADRAO;

    Point2D ponto;
    Circle circulo;
    final int TAMANHOCIRCULO = 5;
    ImageView imgView;
    Group imgGroup;

    public void atualizaCirculo(){
        // atualiza marca circular na posição do ponto
        circulo = new Circle(ponto.getX() + this.imgView.getX(), ponto.getY() + this.imgView.getY(), TAMANHOCIRCULO);
        this.circulo.setOnMouseClicked (new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                    Click();
            }});
        switch (this.estilo) {
            case ESTILO_PADRAO:
                circulo.setFill(Color.GREEN);
                break;
            case ESTILO_PARARODADA:
                circulo.setFill(Color.LIGHTBLUE);
                break;
            case ESTILO_AVANCADOIS:
                circulo.setFill(Color.LIGHTYELLOW);
                break;
            case ESTILO_VOLTAINICIO:
                circulo.setFill(Color.RED);
                break;
        }
        // insere na tela
        this.imgGroup.getChildren().add(this.circulo);
    }

    public void Click(){
        // Alterna estilo do ponto
        if (this.estilo == ESTILO_VOLTAINICIO) {
            this.estilo = ESTILO_PADRAO;
        } else {
            this.estilo++;
        }
        atualizaCirculo();
    }

    public void setPonto(Point2D novoPonto, ImageView imgView, Group imgGroup){
        this.ponto = novoPonto;
        this.imgView = imgView;
        this.imgGroup = imgGroup;
        atualizaCirculo();
    }
}
