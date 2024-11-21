import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {
    private final Stage stage;

    public Game(Stage stage) {
        this.stage = stage;
        stageMenu();
    }

    ArrayList<Pontos> listaPontos = new ArrayList<>();
    int p1Pos = -1;
    ImageView img = null;
    UDPComm commIn = new UDPComm(1020);
    UDPComm commOut = new UDPComm("localhost",1030);

    public String imgNameExt;
    public static String imgName;

    public Player p1;
    public Player p2;
    public Player p3;
    public Player p4;


    public boolean setPlayerNames() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Nomes dos Jogadores");
        dialog.setHeaderText("Informe os nomes dos jogadores:");

        // Configurando os botões do diálogo
        ButtonType okButtonType = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Criando os campos de texto para os jogadores
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField player1Field = new TextField();
        player1Field.setPromptText("Jogador 1");
        TextField player2Field = new TextField();
        player2Field.setPromptText("Jogador 2");
        player2Field.setText("COM-2");
        TextField player3Field = new TextField();
        player3Field.setPromptText("Jogador 3");
        player3Field.setText("COM-3");
        TextField player4Field = new TextField();
        player4Field.setPromptText("Jogador 4");
        player4Field.setText("COM-4");

        grid.add(new Label("Jogador 1:"), 0, 0);
        grid.add(player1Field, 1, 0);
        grid.add(new Label("Jogador 2:"), 0, 1);
        grid.add(player2Field, 1, 1);
        grid.add(new Label("Jogador 3:"), 0, 2);
        grid.add(player3Field, 1, 2);
        grid.add(new Label("Jogador 4:"), 0, 3);
        grid.add(player4Field, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Converter o resultado do diálogo
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return List.of(
                    player1Field.getText().trim(),
                    player2Field.getText().trim(),
                    player3Field.getText().trim(),
                    player4Field.getText().trim()
                );
            }
            return null;
        });

        Optional<List<String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            List<String> playerNames = result.get();
            if (validatePlayerNames(playerNames)) {
                // Associar os nomes aos jogadores
                p1 = new Player(playerNames.get(0), 1);
                p2 = new Player(playerNames.get(1), 2);
                p3 = new Player(playerNames.get(2), 3);
                p4 = new Player(playerNames.get(3), 4);
                return true;
            } else {
                System.out.println("Os nomes fornecidos são inválidos. Tente novamente.");
                return false;
            }
        }

        System.out.println("A configuração dos jogadores foi cancelada.");
        return false;
    }

    private boolean validatePlayerNames(List<String> playerNames) {
        for (String name : playerNames) {
            if (name == null || name.trim().isEmpty()) {
                gameAlert("Todos os jogadores devem ter um nome válido.");
                return false;
            }
        }
        return true;
    }

    public void stageMenu() {
        // Criando o título
        Text labelTitulo = new Text("DYNAMIC TABLETOP");
        labelTitulo.setFont(new Font("Roboto", 32));
        labelTitulo.setStyle("-fx-font-weight: bold;");

        // Criando credits
        Text labelCredits = new Text("Por: Lucas G. Bonato");
        labelCredits.setFont(new Font("Roboto", 12));
        labelCredits.setStyle("-fx-font-weight: bold;");

        // Criando version
        Text labelVersion = new Text("Versão: 1.0");
        labelVersion.setFont(new Font("Roboto", 12));

        // Criando cabeçalho
        VBox headerBox = new VBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        headerBox.getChildren().addAll(labelTitulo);

        // Criando rodapé
        VBox footerBox = new VBox();
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(20));
        footerBox.setSpacing(5);

        footerBox.getChildren().addAll(labelCredits, labelVersion);

        // centralizando botões e título
        VBox optionsBox = new VBox(15);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(20));
        optionsBox.setSpacing(5);

        // botões do menu
        // online
        Button btnPlayOnline = new Button("Jogar Online");
        btnPlayOnline.setPrefWidth(200);
        btnPlayOnline.setOnAction(e -> {
            gameAlert("Este modo de jogo ainda não está disponível!");
        });

        // offline
        Button btnPlayOffline = new Button("Jogar Offline");
        btnPlayOffline.setPrefWidth(200);
        btnPlayOffline.setOnAction(e -> {
            if (setPlayerNames()) {
                stageGame();
            }
        });

        // quit
        Button btnQuit = new Button("Sair");
        btnQuit.setPrefWidth(200);
        btnQuit.setOnAction(e -> stage.close());


        // Adicionando título e botões ao menu
        optionsBox.getChildren().addAll(btnPlayOnline, btnPlayOffline, btnQuit);

        // Layout principal do menu
        BorderPane menuLayout = new BorderPane();
        menuLayout.setTop(headerBox);
        menuLayout.setCenter(optionsBox);
        menuLayout.setBottom(footerBox);

        // Cena do menu
        Scene menuScene = new Scene(menuLayout, 600, 300);
        stage.setTitle("D-TABLETOP - Menu");
        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.show();
    }

    public void stageGame() {
        // imageview geral
        ImageView imageView = new ImageView();

        // grupo de imagens
        Group imgGrupo = new Group();

        // grupo posição top
        HBox labelTop = new HBox();
        labelTop.setAlignment(Pos.CENTER);
        labelTop.setSpacing(10);

        Text iGreen = new Text("Ponto normal");
        iGreen.setFont(new Font("Arial",14));
        Circle cGreen = new Circle(1,1,6);
        cGreen.setFill(Color.GREEN);

        Text iBlue = new Text("Para uma rodada");
        iBlue.setFont(new Font("Arial",14));
        Circle cBlue = new Circle(1,1,6);
        cBlue.setFill(Color.BLUE);

        Text iYellow = new Text("Avança duas casas");
        iYellow.setFont(new Font("Arial",14));
        Circle cYellow = new Circle(1,1,6);
        cYellow.setFill(Color.YELLOW);

        Text iRed = new Text("Volta ao início");
        iRed.setFont(new Font("Arial",14));
        Circle cRed = new Circle(1,1,6);
        cRed.setFill(Color.RED);

        Button btUndoLista = new Button("Desfazer");
        btUndoLista.setPrefWidth(80);
        btUndoLista.setDisable(true);
        EventHandler<ActionEvent> onbtUndoListaClick = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    undoArray(listaPontos, imgGrupo);
                } catch (Exception exp){
                    // erro IO
                }
            }
        };
        btUndoLista.setOnAction(onbtUndoListaClick);

        labelTop.getChildren().addAll(
            cGreen, iGreen, cBlue,
            iBlue, cYellow, iYellow,
            cRed, iRed, btUndoLista
        );

        // information label
        Text labelInfo = new Text("Carregue um tabuleiro e comece a jogar!");
        labelInfo.setFont(new Font("Arial", 16));

        // information label
        Text labelPlayer1 = new Text("Jogador 1: " + p1.getNome());
        labelPlayer1.setFont(new Font("Arial", 16));

        Button btSalvaLista = new Button("Salva pontos");
        btSalvaLista.setPrefWidth(200);
        btSalvaLista.setDisable(true);
        EventHandler<ActionEvent> onbtSalvaListaClick = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    exportar(listaPontos);
                } catch (Exception exp){
                    // erro IO
                    System.out.println(exp.getMessage());
                }
            }
        };
        btSalvaLista.setOnAction(onbtSalvaListaClick);

        Button btLimpaLista = new Button("Limpa pontos");
        btLimpaLista.setPrefWidth(200);
        btLimpaLista.setDisable(true);
        EventHandler<ActionEvent> onbtLimpaListaClick = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    limpaArray(listaPontos, imgGrupo);
                } catch (Exception exp){
                    // erro IO
                }
            }
        };
        btLimpaLista.setOnAction(onbtLimpaListaClick);

        Button btImportaLista = new Button("Importar pontos");
        btImportaLista.setPrefWidth(200);
        btImportaLista.setDisable(true);
        EventHandler<ActionEvent> onbtImportaListaClick = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    limpaArray(listaPontos, imgGrupo);
                    listaPontos.retainAll(importar(imageView, imgGrupo, stage));
                } catch (Exception exp){
                    // erro IO
                }
            }
        };
        btImportaLista.setOnAction(onbtImportaListaClick);

        // cria botões e eventos
        Button btDadoPin = new Button("Jogar Dado");
        btDadoPin.setPrefWidth(200);
        btDadoPin.setDisable(true);
        EventHandler<ActionEvent> onbtDadoPinClick = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (p1Pos == -1) {
                    btImportaLista.setDisable(true);
                    btLimpaLista.setDisable(true);
                    btSalvaLista.setDisable(true);
                    try {
                        Image pin = new Image(new FileInputStream("misc\\pin_1.png"));
                        img = new ImageView(pin);
                        img.setFitHeight(35);
                        img.setPreserveRatio(true);
                    } catch (IOException excep){
                        System.out.println(excep.getMessage());
                    }
                    imgGrupo.getChildren().add(img);
                    p1Pos++;
                } else {
                    // Sorteio do valor do dado
                    int diceValue = jogaDados();
                    String diceImagePath = "misc/dice/dice_" + diceValue + ".png";



                    try {
                        // Carrega a imagem do dado
                        Image diceImage = new Image(new FileInputStream(diceImagePath));
                        ImageView diceImgView = new ImageView(diceImage);
                        diceImgView.setFitHeight(50);  // Ajuste o tamanho conforme necessário
                        diceImgView.setPreserveRatio(true);

                        // Cria um painel (como um Label) para exibir a imagem do dado no stage
                        StackPane dicePane = new StackPane();
                        dicePane.getChildren().add(diceImgView);
                        imgGrupo.getChildren().add(dicePane); // Adiciona a imagem do dado ao grupo

                    } catch (IOException excep) {
                        System.out.println("Erro ao carregar a imagem do dado: " + excep.getMessage());
                    }

                    labelInfo.setText("Valor do dado: " + diceValue);

                    // Movimenta o pin conforme o valor sorteado
                    p1Pos += diceValue;
                    if (p1Pos >= listaPontos.size()) {
                        p1Pos = 0;
                        gameAlert("Parábens, você venceu!");
                    }
                }
                switch (listaPontos.get(p1Pos).estilo) {
                    // ponto azul
                    case 1:
                        // para uma rodada
                        // off-line nao precisa parar uma rodada
                        labelInfo.setText("O jogador caiu na casa azul e não jogará por uma rodada!");
                        break;
                    // ponto amarelo
                    case 2:
                        // avança duas casas
                        p1Pos += 2;
                        labelInfo.setText("O jogador caiu na casa amarela e avançou mais duas casas!");
                        break;
                    // ponto vermelho
                    case 3:
                        // volta ao inicio
                        p1Pos = 0;
                        labelInfo.setText("O jogador caiu na casa vermelha e voltou ao início!");
                        break;
                }

                img.setTranslateX(listaPontos.get(p1Pos).ponto.getX() - 20);
                img.setTranslateY(listaPontos.get(p1Pos).ponto.getY() - 20);
            }
        };
        btDadoPin.setOnAction(onbtDadoPinClick);

        Button btCarregaImagem = new Button("Carrega imagem");
        btCarregaImagem.setPrefWidth(200);
        btCarregaImagem.setDisable(false);
        EventHandler<ActionEvent> onbtCarregaImagemClick = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    Image image = carregaImagem();
                    if (image != null) {
                        imageView.setImage(image);
                        btImportaLista.setDisable(false);
                        btLimpaLista.setDisable(false);
                        btSalvaLista.setDisable(false);
                        btDadoPin.setDisable(false);
                        btUndoLista.setDisable(false);

                        importar(imageView, imgGrupo, stage);
                        if (p1Pos >= 0) {
                            // pin existe
                            imgGrupo.getChildren().removeLast();
                            p1Pos = -1;
                        }
                        limpaArray(listaPontos, imgGrupo);
                    }
                } catch (Exception exp) {
                    // erro IO
                    System.out.println(exp.getMessage());
                }
            }
        };
        btCarregaImagem.setOnAction(onbtCarregaImagemClick);

        imgGrupo.getChildren().add(imageView);



        VBox centerBox = new VBox();
        centerBox.setSpacing(10);
        centerBox.getChildren().addAll(labelPlayer1, imgGrupo, labelInfo);

        centerBox.setAlignment(Pos.CENTER);

        // BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(labelTop);
        BorderPane.setMargin(labelTop, new Insets(10));
        BorderPane.setAlignment(labelTop, Pos.CENTER);
        borderPane.setCenter(centerBox);

        // Setting the image view
        // setting the fit height and width of the image view
        imageView.setFitHeight(500);
        imageView.setFitWidth(500);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Pontos estePonto = new Pontos();
            estePonto.setPonto(new Point2D(event.getX() - imageView.getX(),event.getY() - imageView.getY()), imageView, imgGrupo);
            // Atualiza no array
            listaPontos.add(estePonto);
        });

        // Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        // grupo de botões
        HBox grupo = new HBox();
        grupo.setMinHeight(50);
        grupo.setAlignment(Pos.CENTER_RIGHT);
        grupo.setSpacing(20);
        grupo.getChildren().addAll(btCarregaImagem,btLimpaLista, btSalvaLista, btImportaLista, btDadoPin);
        BorderPane.setMargin(grupo, new Insets(12));
        borderPane.setBottom(grupo);

        Scene scene = new Scene(borderPane, 800, 650);
        stage.setTitle("D-TABLETOP - Offline");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public int jogaDados() {
        // Joga os dados para ver quantas casas anda
        // envia pacote para receber valor do dado
        byte[] ping = {'*'};
        commOut.setMsg(ping);
        commOut.sendMsg();
        // Aguarda resposta
        commIn.receiveMsg();
        System.out.println("Recebido: "+  commIn.msgByte[0]);
        return (int) commIn.msgByte[0];
    }

    public String getImgName(File imgPath) {
        String[] imgNameExtList;

        imgNameExt = imgPath.getName();
        System.out.println(imgNameExt);
        imgNameExtList = imgNameExt.split("\\.");

        return imgNameExtList[0];
    }

    public Image carregaImagem() {
        // Abrir documento
        FileChooser abrirDialog = new FileChooser();
        File fileAbrir = abrirDialog.showOpenDialog(stage);
        if (fileAbrir != null) {
            try {
                imgName = getImgName(fileAbrir);
                return new Image(new FileInputStream(fileAbrir.getPath()));
            } catch (Exception error) {
                // Tratamento da erro
                System.out.println(error.getMessage());
                return null;
            }
        } else return null;
    }

    public static void exportar(ArrayList<Pontos> listaPontos) throws IOException {
        try {
            FileWriter arq = new FileWriter("imports\\points\\" + imgName + ".txt");
            PrintWriter gravarArq = new PrintWriter(arq);
            int i;
            for (i = 0; i < listaPontos.size(); i++) {
                gravarArq.println(
                        listaPontos.get(i).ponto.getX() + "," +
                                listaPontos.get(i).ponto.getY() + "," +
                                listaPontos.get(i).estilo
                );
            }
            gravarArq.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<Pontos> importar(ImageView imgView, Group imgGroup, Stage stage) {
        ArrayList<Pontos> listaPontos = new ArrayList<>();
        double x, y;
        String[] split;
        File fileAbrir;

        // Tenta localizar o arquivo inicial
        fileAbrir = new File("imports\\points\\" + imgName + ".txt");

        if (!fileAbrir.exists()) {
            // Alerta para o usuário se o arquivo não for encontrado
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Arquivo não encontrado");
            alert.setHeaderText("O tabuleiro selecionado não possui trilhas salvas.");
            alert.setContentText("Deseja procurar as trilhas manualmente ou cancelar?");

            ButtonType btnProcurar = new ButtonType("Procurar");
            ButtonType btnCancelar = new ButtonType("Cancelar");

            alert.getButtonTypes().setAll(btnProcurar, btnCancelar);

            // Exibe o alerta e aguarda a resposta do usuário
            ButtonType resultado = alert.showAndWait().orElse(btnCancelar);

            if (resultado == btnProcurar) {
                // Usuário decide procurar manualmente
                FileChooser abrirDialog = new FileChooser();
                fileAbrir = abrirDialog.showOpenDialog(stage);

                // Verifica se o usuário selecionou algum arquivo
                if (fileAbrir == null) {
                    System.out.println("Nenhum arquivo foi selecionado.");
                    return null;
                }
            } else {
                // Usuário cancelou a operação
                System.out.println("Operação cancelada.");
                return null;
            }
        }

        // Lê o arquivo
        try (BufferedReader lerArq = new BufferedReader(new FileReader(fileAbrir))) {
            String linha = lerArq.readLine(); // Lê a primeira linha
            while (linha != null) {
                Pontos novoPonto = new Pontos();

                split = linha.split(",");
                x = Double.parseDouble(split[0]);
                y = Double.parseDouble(split[1]);

                novoPonto.estilo = Byte.parseByte(split[2]);
                novoPonto.setPonto(new Point2D(x, y), imgView, imgGroup);

                listaPontos.add(novoPonto); // Adiciona o objeto ao array
                linha = lerArq.readLine(); // Lê a próxima linha
            }
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }

        return listaPontos;
    }

    public void limpaArray(ArrayList<Pontos> listaPontos, Group imgGrupo){
        int n = imgGrupo.getChildren().size();
        for(int i = 1; i < n; i++){
            imgGrupo.getChildren().removeLast();
        }
        listaPontos.clear();
    }

    public void undoArray(ArrayList<Pontos> listaPontos, Group imgGrupo){
        if (!listaPontos.isEmpty()) {
            imgGrupo.getChildren().removeLast();
            listaPontos.removeLast();
        }
    }

    public void gameAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
}
