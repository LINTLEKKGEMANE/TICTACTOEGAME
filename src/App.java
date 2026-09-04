import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {

    // =========================================================
    // GAME STATE
    // =========================================================

    private char currentPlayer = 'X';

    private final char[][] board = new char[3][3];

    private boolean gameOver = false;
    private int movesCount = 0;

    private int player1Score = 0;
    private int player2Score = 0;
    private int draws = 0;

    private int currentLevel = 1;

    private boolean isPlayer2Turn = false;
    private boolean isPaused = false;
    private boolean isDraw = false;

    // =========================================================
    // MAIN UI
    // =========================================================

    private Stage primaryStage;
    private BorderPane root;

    private Label statusLabel;
    private Label scoreLabel;
    private Label levelLabel;

    private GridPane gridPane;

    private Button[][] cellButtons =
            new Button[3][3];

    private Button pauseButton;

    // =========================================================
    // MAIN MENU RESPONSIVE COMPONENTS
    // =========================================================

    private boolean mainMenuActive = false;

    private VBox mainMenuBox;

    private Text mainMenuTitle;

    private Rectangle mainMenuLogoBackground;

    private Text mainMenuLogoText;

    private Button mainMenuPlayButton;
    private Button mainMenuInstructionsButton;
    private Button mainMenuSettingsButton;
    private Button mainMenuExitButton;

    // =========================================================
    // BACKGROUND
    // =========================================================

    private Image mainMenuBackgroundImage;

    // =========================================================
    // SOUND
    // =========================================================

    private AudioClip clickSound;
    private AudioClip winSound;

    private MediaPlayer musicPlayer;

    private boolean soundOn = true;
    private boolean musicOn = true;

    private double volumeLevel = 0.7;

    private String difficulty = "Normal";

    // =========================================================
    // RANDOM
    // =========================================================

    private final Random random =
            new Random();

    // =========================================================
    // START
    // =========================================================

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        primaryStage.setTitle(
                "Tic Tac Toe vs Player 2 - Interactive Multimedia"
        );

        // ---------------------------------------------------------
        // ROOT
        // ---------------------------------------------------------

        root = new BorderPane();

        root.setPadding(
                new Insets(20)
        );

        setDefaultBackground();

        // ---------------------------------------------------------
        // LOAD SOUNDS
        // ---------------------------------------------------------

        loadSounds();

        // ---------------------------------------------------------
        // MAIN MENU
        // ---------------------------------------------------------

        showMainMenu();

        // ---------------------------------------------------------
        // SCENE
        // ---------------------------------------------------------

        Scene scene =
                new Scene(
                        root,
                        900,
                        700
                );

        primaryStage.setScene(scene);

        // ---------------------------------------------------------
        // MINIMUM WINDOW SIZE
        // ---------------------------------------------------------

        primaryStage.setMinWidth(320);
        primaryStage.setMinHeight(480);

        // ---------------------------------------------------------
        // RESPONSIVE WINDOW LISTENERS
        // ---------------------------------------------------------

        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> {

                    adjustForScreenSize(
                            newValue.doubleValue(),
                            scene.getHeight()
                    );

                    updateMainMenuResponsive();
                }
        );

        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> {

                    adjustForScreenSize(
                            scene.getWidth(),
                            newValue.doubleValue()
                    );

                    updateMainMenuResponsive();
                }
        );

        // ---------------------------------------------------------
        // SHOW WINDOW
        // ---------------------------------------------------------

        primaryStage.show();

        // Update after window becomes visible
        updateMainMenuResponsive();

        // ---------------------------------------------------------
        // MUSIC
        // ---------------------------------------------------------

        startMusic();
    }

    // =========================================================
    // DEFAULT BACKGROUND
    // =========================================================

    private void setDefaultBackground() {

        root.setBackground(null);

        root.setStyle(
                "-fx-background-color: " +
                "linear-gradient(" +
                "to bottom, " +
                "#1a1a2e, " +
                "#16213e, " +
                "#0f3460" +
                ");"
        );
    }

    // =========================================================
    // MAIN MENU BACKGROUND
    // =========================================================

    private void setMainMenuBackground() {

        root.setBackground(null);

        root.setStyle("");

        File backgroundFile =
                new File(
                        "images/background.jpg"
                );

        if (!backgroundFile.exists()) {

            System.out.println(
                    "ERROR: images/background.jpg not found."
            );

            setDefaultBackground();

            return;
        }

        try {

            /*
             * Load the image once.
             */

            mainMenuBackgroundImage =
                    new Image(
                            backgroundFile
                                    .toURI()
                                    .toString()
                    );

            if (
                    mainMenuBackgroundImage.isError()
            ) {

                System.out.println(
                        "ERROR: Could not load background image."
                );

                setDefaultBackground();

                return;
            }

            /*
             * 100% width and 100% height.
             *
             * preserveRatio = true
             *
             * cover = true
             *
             * This makes the image fill the complete
             * window while maintaining its proportions.
             *
             * If the window has a different shape,
             * the extra part of the image is cropped.
             */

            BackgroundSize backgroundSize =
                    new BackgroundSize(
                            100,
                            100,
                            true,
                            true,
                            false,
                            true
                    );

            BackgroundImage background =
                    new BackgroundImage(
                            mainMenuBackgroundImage,

                            BackgroundRepeat.NO_REPEAT,

                            BackgroundRepeat.NO_REPEAT,

                            BackgroundPosition.CENTER,

                            backgroundSize
                    );

            root.setBackground(
                    new Background(background)
            );

            System.out.println(
                    "SUCCESS: Responsive main menu background applied."
            );

        } catch (Exception e) {

            System.out.println(
                    "ERROR loading background: "
                            + e.getMessage()
            );

            setDefaultBackground();
        }
    }

    // =========================================================
    // MAIN MENU
    // =========================================================

    private void showMainMenu() {

        mainMenuActive = true;

        // Apply responsive background
        setMainMenuBackground();

        // Clear other areas
        root.setTop(null);
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);

        // ---------------------------------------------------------
        // MENU BOX
        // ---------------------------------------------------------

        mainMenuBox =
                new VBox();

        mainMenuBox.setAlignment(
                Pos.CENTER
        );

        mainMenuBox.setPadding(
                new Insets(30)
        );

        // ---------------------------------------------------------
        // TITLE
        // ---------------------------------------------------------

        mainMenuTitle =
                new Text(
                        "🎮 TIC TAC TOE 🎮"
                );

        mainMenuTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        48
                )
        );

        mainMenuTitle.setFill(
                Color.GOLD
        );

        mainMenuTitle.setEffect(
                new DropShadow(
                        20,
                        Color.BLACK
                )
        );

        // ---------------------------------------------------------
        // TITLE ANIMATION
        // ---------------------------------------------------------

        RotateTransition titleRotate =
                new RotateTransition(
                        javafx.util.Duration.seconds(3),
                        mainMenuTitle
                );

        titleRotate.setFromAngle(-2);

        titleRotate.setToAngle(2);

        titleRotate.setAutoReverse(true);

        titleRotate.setCycleCount(
                RotateTransition.INDEFINITE
        );

        titleRotate.play();

        // ---------------------------------------------------------
        // LOGO
        // ---------------------------------------------------------

        StackPane logoPane =
                new StackPane();

        mainMenuLogoBackground =
                new Rectangle(
                        200,
                        100
                );

        mainMenuLogoBackground.setArcWidth(25);

        mainMenuLogoBackground.setArcHeight(25);

        mainMenuLogoBackground.setFill(
                Color.rgb(
                        0,
                        0,
                        0,
                        0.55
                )
        );

        mainMenuLogoBackground.setStroke(
                Color.GOLD
        );

        mainMenuLogoBackground.setStrokeWidth(3);

        mainMenuLogoText =
                new Text(
                        "❌ vs ⭕"
                );

        mainMenuLogoText.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        40
                )
        );

        mainMenuLogoText.setFill(
                Color.GOLD
        );

        logoPane.getChildren().addAll(
                mainMenuLogoBackground,
                mainMenuLogoText
        );

        // ---------------------------------------------------------
        // LOGO ANIMATION
        // ---------------------------------------------------------

        ScaleTransition logoPulse =
                new ScaleTransition(
                        javafx.util.Duration.seconds(1.2),
                        logoPane
                );

        logoPulse.setFromX(1.0);
        logoPulse.setFromY(1.0);

        logoPulse.setToX(1.08);
        logoPulse.setToY(1.08);

        logoPulse.setAutoReverse(true);

        logoPulse.setCycleCount(
                ScaleTransition.INDEFINITE
        );

        logoPulse.play();

        // ---------------------------------------------------------
        // BUTTONS
        // ---------------------------------------------------------

        mainMenuPlayButton =
                createStyledButton(
                        "▶ PLAY GAME",
                        "#27ae60"
                );

        mainMenuInstructionsButton =
                createStyledButton(
                        "📖 INSTRUCTIONS",
                        "#2980b9"
                );

        mainMenuSettingsButton =
                createStyledButton(
                        "⚙ SETTINGS",
                        "#8e44ad"
                );

        mainMenuExitButton =
                createStyledButton(
                        "❌ EXIT",
                        "#c0392b"
                );

        // ---------------------------------------------------------
        // BUTTON ACTIONS
        // ---------------------------------------------------------

        mainMenuPlayButton.setOnAction(
                event -> {

                    playClickSound();

                    showGameScreen();
                }
        );

        mainMenuInstructionsButton.setOnAction(
                event -> {

                    playClickSound();

                    showInstructionsScreen();
                }
        );

        mainMenuSettingsButton.setOnAction(
                event -> {

                    playClickSound();

                    showSettingsScreen();
                }
        );

        mainMenuExitButton.setOnAction(
                event -> {

                    playClickSound();

                    primaryStage.close();
                }
        );

        // ---------------------------------------------------------
        // ADD COMPONENTS
        // ---------------------------------------------------------

        mainMenuBox.getChildren().addAll(
                mainMenuTitle,
                logoPane,
                mainMenuPlayButton,
                mainMenuInstructionsButton,
                mainMenuSettingsButton,
                mainMenuExitButton
        );

        root.setCenter(
                mainMenuBox
        );

        updateMainMenuResponsive();
    }

    // =========================================================
    // RESPONSIVE MAIN MENU
    // =========================================================

    private void updateMainMenuResponsive() {

        if (
                !mainMenuActive ||
                mainMenuBox == null ||
                root == null
        ) {

            return;
        }

        double width =
                root.getWidth();

        double height =
                root.getHeight();

        // ---------------------------------------------------------
        // FALLBACK SIZE
        // ---------------------------------------------------------

        if (width <= 0) {

            width = 900;
        }

        if (height <= 0) {

            height = 700;
        }

        /*
         * Use the smallest dimension.
         *
         * This prevents the menu from becoming too large
         * when the window is very wide or very narrow.
         */

        double base =
                Math.min(
                        width,
                        height
                );

        // ---------------------------------------------------------
        // TITLE SIZE
        // ---------------------------------------------------------

        double titleSize =
                clamp(
                        base * 0.075,
                        24,
                        52
                );

        // ---------------------------------------------------------
        // LOGO SIZE
        // ---------------------------------------------------------

        double logoWidth =
                clamp(
                        base * 0.30,
                        120,
                        220
                );

        double logoHeight =
                clamp(
                        base * 0.14,
                        60,
                        110
                );

        double logoTextSize =
                clamp(
                        base * 0.055,
                        22,
                        40
                );

        // ---------------------------------------------------------
        // BUTTON SIZE
        // ---------------------------------------------------------

        double buttonFontSize =
                clamp(
                        base * 0.026,
                        11,
                        18
                );

        double buttonVerticalPadding =
                clamp(
                        base * 0.015,
                        6,
                        12
                );

        double buttonHorizontalPadding =
                clamp(
                        base * 0.035,
                        14,
                        30
                );

        double buttonWidth =
                clamp(
                        width * 0.45,
                        190,
                        360
                );

        // ---------------------------------------------------------
        // SPACING
        // ---------------------------------------------------------

        double spacing =
                clamp(
                        base * 0.025,
                        6,
                        20
                );

        double menuPadding =
                clamp(
                        base * 0.035,
                        10,
                        30
                );

        // ---------------------------------------------------------
        // APPLY MENU SPACING
        // ---------------------------------------------------------

        mainMenuBox.setSpacing(
                spacing
        );

        mainMenuBox.setPadding(
                new Insets(
                        menuPadding
                )
        );

        // ---------------------------------------------------------
        // APPLY TITLE SIZE
        // ---------------------------------------------------------

        mainMenuTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        titleSize
                )
        );

        // ---------------------------------------------------------
        // APPLY LOGO SIZE
        // ---------------------------------------------------------

        mainMenuLogoBackground.setWidth(
                logoWidth
        );

        mainMenuLogoBackground.setHeight(
                logoHeight
        );

        mainMenuLogoText.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        logoTextSize
                )
        );

        // ---------------------------------------------------------
        // APPLY BUTTON SIZES
        // ---------------------------------------------------------

        Button[] buttons = {

                mainMenuPlayButton,

                mainMenuInstructionsButton,

                mainMenuSettingsButton,

                mainMenuExitButton
        };

        for (Button button : buttons) {

            button.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            buttonFontSize
                    )
            );

            button.setPadding(
                    new Insets(
                            buttonVerticalPadding,
                            buttonHorizontalPadding,
                            buttonVerticalPadding,
                            buttonHorizontalPadding
                    )
            );

            button.setPrefWidth(
                    buttonWidth
            );

            button.setMaxWidth(
                    buttonWidth
            );
        }
    }

    // =========================================================
    // CLAMP
    // =========================================================

    private double clamp(
            double value,
            double minimum,
            double maximum
    ) {

        return Math.max(
                minimum,
                Math.min(
                        maximum,
                        value
                )
        );
    }

    // =========================================================
    // GENERAL RESPONSIVE SCREEN SIZE
    // =========================================================

    private void adjustForScreenSize(
            double width,
            double height
    ) {

        double buttonSize;

        double paddingSize;

        if (
                width < 600 ||
                height < 500
        ) {

            paddingSize = 8;

            buttonSize = 60;

        } else if (
                width < 900 ||
                height < 700
        ) {

            paddingSize = 12;

            buttonSize = 80;

        } else {

            paddingSize = 16;

            buttonSize = 100;
        }

        root.setPadding(
                new Insets(
                        paddingSize
                )
        );

        // ---------------------------------------------------------
        // GAME GRID
        // ---------------------------------------------------------

        if (gridPane != null) {

            for (
                    int row = 0;
                    row < 3;
                    row++
            ) {

                for (
                        int col = 0;
                        col < 3;
                        col++
                ) {

                    if (
                            cellButtons[row][col]
                                    != null
                    ) {

                        cellButtons[row][col]
                                .setPrefSize(
                                        buttonSize,
                                        buttonSize
                                );
                    }
                }
            }
        }

        // ---------------------------------------------------------
        // MAIN MENU
        // ---------------------------------------------------------

        if (mainMenuActive) {

            updateMainMenuResponsive();
        }
    }

    // =========================================================
    // GAME SCREEN
    // =========================================================

    private void showGameScreen() {

        mainMenuActive = false;

        setDefaultBackground();

        gameOver = false;

        isPaused = false;

        isDraw = false;

        movesCount = 0;

        currentPlayer = 'X';

        isPlayer2Turn = false;

        initializeBoard();

        // ---------------------------------------------------------
        // TOP BAR
        // ---------------------------------------------------------

        HBox topBar =
                new HBox(20);

        topBar.setAlignment(
                Pos.CENTER
        );

        topBar.setPadding(
                new Insets(10)
        );

        scoreLabel =
                new Label();

        scoreLabel.setTextFill(
                Color.WHITE
        );

        scoreLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        statusLabel =
                new Label(
                        "Player 1 (X) Turn"
                );

        statusLabel.setTextFill(
                Color.GOLD
        );

        statusLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        levelLabel =
                new Label(
                        "Level: " + currentLevel
                );

        levelLabel.setTextFill(
                Color.WHITE
        );

        levelLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        pauseButton =
                createStyledButton(
                        "⏸ PAUSE",
                        "#f39c12"
                );

        pauseButton.setOnAction(
                event ->
                        togglePause()
        );

        updateScore();

        topBar.getChildren().addAll(
                scoreLabel,
                statusLabel,
                levelLabel,
                pauseButton
        );

        root.setTop(
                topBar
        );

        // ---------------------------------------------------------
        // GAME GRID
        // ---------------------------------------------------------

        gridPane =
                new GridPane();

        gridPane.setAlignment(
                Pos.CENTER
        );

        gridPane.setHgap(8);

        gridPane.setVgap(8);

        for (
                int row = 0;
                row < 3;
                row++
        ) {

            for (
                    int col = 0;
                    col < 3;
                    col++
            ) {

                Button cell =
                        new Button();

                cell.setPrefSize(
                        100,
                        100
                );

                cell.setMinSize(
                        60,
                        60
                );

                cell.setMaxSize(
                        150,
                        150
                );

                cell.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.12);" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
                );

                final int r = row;

                final int c = col;

                cell.setOnAction(
                        event ->
                                handleCellClick(
                                        r,
                                        c
                                )
                );

                cell.setOnMouseEntered(
                        event -> {

                            if (
                                    board[r][c] == ' ' &&
                                    !gameOver &&
                                    !isPaused
                            ) {

                                cell.setStyle(
                                        "-fx-background-color: rgba(255,215,0,0.25);" +
                                        "-fx-border-color: #FFD700;" +
                                        "-fx-border-width: 3;" +
                                        "-fx-background-radius: 10;" +
                                        "-fx-border-radius: 10;"
                                );
                            }
                        }
                );

                cell.setOnMouseExited(
                        event -> {

                            if (
                                    board[r][c] == ' '
                            ) {

                                cell.setStyle(
                                        "-fx-background-color: rgba(255,255,255,0.12);" +
                                        "-fx-border-color: #ffffff;" +
                                        "-fx-border-width: 2;" +
                                        "-fx-background-radius: 10;" +
                                        "-fx-border-radius: 10;"
                                );
                            }
                        }
                );

                cellButtons[row][col] =
                        cell;

                gridPane.add(
                        cell,
                        col,
                        row
                );
            }
        }

        // ---------------------------------------------------------
        // BOTTOM BAR
        // ---------------------------------------------------------

        HBox bottomBar =
                new HBox(15);

        bottomBar.setAlignment(
                Pos.CENTER
        );

        bottomBar.setPadding(
                new Insets(15)
        );

        Button resetButton =
                createStyledButton(
                        "🔄 RESET",
                        "#3498db"
                );

        resetButton.setOnAction(
                event -> {

                    playClickSound();

                    resetGame();
                }
        );

        Button menuButton =
                createStyledButton(
                        "🏠 MAIN MENU",
                        "#7f8c8d"
                );

        menuButton.setOnAction(
                event -> {

                    playClickSound();

                    showMainMenu();
                }
        );

        Label hint =
                new Label(
                        "Press R to Reset | P to Pause"
                );

        hint.setTextFill(
                Color.LIGHTGRAY
        );

        bottomBar.getChildren().addAll(
                resetButton,
                menuButton,
                hint
        );

        root.setCenter(
                new StackPane(
                        gridPane
                )
        );

        root.setBottom(
                bottomBar
        );

        root.setLeft(null);

        root.setRight(null);

        // ---------------------------------------------------------
        // KEYBOARD CONTROLS
        // ---------------------------------------------------------

        if (
                primaryStage.getScene()
                        != null
        ) {

            primaryStage.getScene()
                    .setOnKeyPressed(
                            event -> {

                                if (
                                        event.getCode()
                                                == KeyCode.R
                                ) {

                                    resetGame();

                                } else if (
                                        event.getCode()
                                                == KeyCode.P
                                ) {

                                    togglePause();
                                }
                            }
                    );
        }
    }

    // =========================================================
    // PAUSE
    // =========================================================

    private void togglePause() {

        if (gameOver) {

            return;
        }

        playClickSound();

        isPaused = !isPaused;

        if (isPaused) {

            statusLabel.setText(
                    "⏸ GAME PAUSED"
            );

            pauseButton.setText(
                    "▶ RESUME"
            );

        } else {

            statusLabel.setText(
                    "Player "
                            + (
                            currentPlayer == 'X'
                                    ? "1 (X)"
                                    : "2 (O)"
                    )
                            + " Turn"
            );

            pauseButton.setText(
                    "⏸ PAUSE"
            );
        }
    }

    // =========================================================
    // CELL CLICK
    // =========================================================

    private void handleCellClick(
            int row,
            int col
    ) {

        if (
                gameOver ||
                isPaused ||
                board[row][col] != ' '
        ) {

            return;
        }

        if (currentPlayer != 'X') {

            return;
        }

        playClickSound();

        makeMove(
                row,
                col,
                'X'
        );

        if (!gameOver) {

            isPlayer2Turn = true;

            statusLabel.setText(
                    "Player 2 (O) Thinking..."
            );

            PauseTransition pause =
                    new PauseTransition(
                            javafx.util.Duration
                                    .millis(500)
                    );

            pause.setOnFinished(
                    event ->
                            player2Move()
            );

            pause.play();
        }
    }

    // =========================================================
    // MAKE MOVE
    // =========================================================

    private void makeMove(
            int row,
            int col,
            char player
    ) {

        board[row][col] =
                player;

        movesCount++;

        Button cell =
                cellButtons[row][col];

        cell.setText(
                String.valueOf(player)
        );

        cell.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        42
                )
        );

        if (player == 'X') {

            cell.setTextFill(
                    Color.LIMEGREEN
            );

        } else {

            cell.setTextFill(
                    Color.ORANGERED
            );
        }

        cell.setStyle(
                "-fx-background-color: rgba(255,255,255,0.18);" +
                "-fx-border-color: "
                        + (
                        player == 'X'
                                ? "#32CD32"
                                : "#FF4500"
                )
                        + ";" +
                "-fx-border-width: 3;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );

        // ---------------------------------------------------------
        // WIN
        // ---------------------------------------------------------

        if (
                checkWin(player)
        ) {

            gameOver = true;

            if (player == 'X') {

                player1Score++;

            } else {

                player2Score++;
            }

            playWinSound();

            highlightWinningCells(
                    player
            );

            updateScore();

            PauseTransition pause =
                    new PauseTransition(
                            javafx.util.Duration
                                    .seconds(1)
                    );

            boolean player1Won =
                    player == 'X';

            pause.setOnFinished(
                    event ->
                            showGameOverScreen(
                                    player1Won
                            )
            );

            pause.play();

            return;
        }

        // ---------------------------------------------------------
        // DRAW
        // ---------------------------------------------------------

        if (
                isBoardFull()
        ) {

            gameOver = true;

            isDraw = true;

            draws++;

            updateScore();

            PauseTransition pause =
                    new PauseTransition(
                            javafx.util.Duration
                                    .seconds(1)
                    );

            pause.setOnFinished(
                    event ->
                            showGameOverScreen(
                                    false
                            )
            );

            pause.play();

            return;
        }

        // ---------------------------------------------------------
        // CHANGE PLAYER
        // ---------------------------------------------------------

        if (player == 'X') {

            currentPlayer = 'O';

        } else {

            currentPlayer = 'X';
        }

        statusLabel.setText(
                "Player "
                        + (
                        currentPlayer == 'X'
                                ? "1 (X)"
                                : "2 (O)"
                )
                        + " Turn"
        );
    }

    // =========================================================
    // PLAYER 2 MOVE
    // =========================================================

    private void player2Move() {

        if (
                gameOver ||
                isPaused
        ) {

            return;
        }

        int[] move =
                getBestMove();

        if (move != null) {

            makeMove(
                    move[0],
                    move[1],
                    'O'
            );
        }

        isPlayer2Turn = false;
    }

    // =========================================================
    // GET BEST MOVE
    // =========================================================

    private int[] getBestMove() {

        // ---------------------------------------------------------
        // EASY
        // ---------------------------------------------------------

        if (
                difficulty.equals(
                        "Easy"
                )
        ) {

            return getRandomMove();
        }

        // ---------------------------------------------------------
        // HARD
        // ---------------------------------------------------------

        if (
                difficulty.equals(
                        "Hard"
                )
        ) {

            return getMinimaxMove();
        }

        // ---------------------------------------------------------
        // NORMAL
        // ---------------------------------------------------------

        if (
                random.nextInt(100) < 65
        ) {

            return getMinimaxMove();

        } else {

            return getRandomMove();
        }
    }

    // =========================================================
    // RANDOM MOVE
    // =========================================================

    private int[] getRandomMove() {

        List<int[]> available =
                new ArrayList<>();

        for (
                int row = 0;
                row < 3;
                row++
        ) {

            for (
                    int col = 0;
                    col < 3;
                    col++
            ) {

                if (
                        board[row][col] == ' '
                ) {

                    available.add(
                            new int[]{
                                    row,
                                    col
                            }
                    );
                }
            }
        }

        if (
                available.isEmpty()
        ) {

            return null;
        }

        return available.get(
                random.nextInt(
                        available.size()
                )
        );
    }

    // =========================================================
    // MINIMAX MOVE
    // =========================================================

    private int[] getMinimaxMove() {

        int bestScore =
                Integer.MIN_VALUE;

        int[] bestMove = null;

        for (
                int row = 0;
                row < 3;
                row++
        ) {

            for (
                    int col = 0;
                    col < 3;
                    col++
            ) {

                if (
                        board[row][col] == ' '
                ) {

                    board[row][col] =
                            'O';

                    int score =
                            minimax(
                                    board,
                                    0,
                                    false
                            );

                    board[row][col] =
                            ' ';

                    if (
                            score > bestScore
                    ) {

                        bestScore =
                                score;

                        bestMove =
                                new int[]{
                                        row,
                                        col
                                };
                    }
                }
            }
        }

        return bestMove;
    }

    // =========================================================
    // MINIMAX
    // =========================================================

    private int minimax(
            char[][] state,
            int depth,
            boolean maximizing
    ) {

        if (
                checkWinOnBoard(
                        state,
                        'O'
                )
        ) {

            return 10 - depth;
        }

        if (
                checkWinOnBoard(
                        state,
                        'X'
                )
        ) {

            return depth - 10;
        }

        if (
                isBoardFullOnBoard(
                        state
                )
        ) {

            return 0;
        }

        if (maximizing) {

            int bestScore =
                    Integer.MIN_VALUE;

            for (
                    int row = 0;
                    row < 3;
                    row++
            ) {

                for (
                        int col = 0;
                        col < 3;
                        col++
                ) {

                    if (
                            state[row][col]
                                    == ' '
                    ) {

                        state[row][col] =
                                'O';

                        int score =
                                minimax(
                                        state,
                                        depth + 1,
                                        false
                                );

                        state[row][col] =
                                ' ';

                        bestScore =
                                Math.max(
                                        bestScore,
                                        score
                                );
                    }
                }
            }

            return bestScore;

        } else {

            int bestScore =
                    Integer.MAX_VALUE;

            for (
                    int row = 0;
                    row < 3;
                    row++
            ) {

                for (
                        int col = 0;
                        col < 3;
                        col++
                ) {

                    if (
                            state[row][col]
                                    == ' '
                    ) {

                        state[row][col] =
                                'X';

                        int score =
                                minimax(
                                        state,
                                        depth + 1,
                                        true
                                );

                        state[row][col] =
                                ' ';

                        bestScore =
                                Math.min(
                                        bestScore,
                                        score
                                );
                    }
                }
            }

            return bestScore;
        }
    }

    // =========================================================
    // CHECK WIN
    // =========================================================

    private boolean checkWin(
            char player
    ) {

        return checkWinOnBoard(
                board,
                player
        );
    }

    // =========================================================
    // CHECK WIN ON BOARD
    // =========================================================

    private boolean checkWinOnBoard(
            char[][] state,
            char player
    ) {

        // Rows
        for (
                int row = 0;
                row < 3;
                row++
        ) {

            if (
                    state[row][0] == player &&
                    state[row][1] == player &&
                    state[row][2] == player
            ) {

                return true;
            }
        }

        // Columns
        for (
                int col = 0;
                col < 3;
                col++
        ) {

            if (
                    state[0][col] == player &&
                    state[1][col] == player &&
                    state[2][col] == player
            ) {

                return true;
            }
        }

        // Main diagonal
        if (
                state[0][0] == player &&
                state[1][1] == player &&
                state[2][2] == player
        ) {

            return true;
        }

        // Other diagonal
        if (
                state[0][2] == player &&
                state[1][1] == player &&
                state[2][0] == player
        ) {

            return true;
        }

        return false;
    }

    // =========================================================
    // CHECK BOARD FULL
    // =========================================================

    private boolean isBoardFull() {

        return isBoardFullOnBoard(
                board
        );
    }

    private boolean isBoardFullOnBoard(
            char[][] state
    ) {

        for (
                int row = 0;
                row < 3;
                row++
        ) {

            for (
                    int col = 0;
                    col < 3;
                    col++
            ) {

                if (
                        state[row][col] == ' '
                ) {

                    return false;
                }
            }
        }

        return true;
    }

    // =========================================================
    // HIGHLIGHT WINNING CELLS
    // =========================================================

    private void highlightWinningCells(
            char player
    ) {

        String winningStyle =
                "-fx-background-color: rgba(255,215,0,0.45);" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 4;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;";

        for (
                int row = 0;
                row < 3;
                row++
        ) {

            if (
                    board[row][0] == player &&
                    board[row][1] == player &&
                    board[row][2] == player
            ) {

                cellButtons[row][0]
                        .setStyle(
                                winningStyle
                        );

                cellButtons[row][1]
                        .setStyle(
                                winningStyle
                        );

                cellButtons[row][2]
                        .setStyle(
                                winningStyle
                        );
            }
        }

        for (
                int col = 0;
                col < 3;
                col++
        ) {

            if (
                    board[0][col] == player &&
                    board[1][col] == player &&
                    board[2][col] == player
            ) {

                cellButtons[0][col]
                        .setStyle(
                                winningStyle
                        );

                cellButtons[1][col]
                        .setStyle(
                                winningStyle
                        );

                cellButtons[2][col]
                        .setStyle(
                                winningStyle
                        );
            }
        }

        if (
                board[0][0] == player &&
                board[1][1] == player &&
                board[2][2] == player
        ) {

            cellButtons[0][0]
                    .setStyle(
                            winningStyle
                    );

            cellButtons[1][1]
                    .setStyle(
                            winningStyle
                    );

            cellButtons[2][2]
                    .setStyle(
                            winningStyle
                    );
        }

        if (
                board[0][2] == player &&
                board[1][1] == player &&
                board[2][0] == player
        ) {

            cellButtons[0][2]
                    .setStyle(
                            winningStyle
                    );

            cellButtons[1][1]
                    .setStyle(
                            winningStyle
                    );

            cellButtons[2][0]
                    .setStyle(
                            winningStyle
                    );
        }
    }

    // =========================================================
    // UPDATE SCORE
    // =========================================================

    private void updateScore() {

        if (
                scoreLabel != null
        ) {

            scoreLabel.setText(
                    "Player 1: "
                            + player1Score
                            + "   |   Player 2: "
                            + player2Score
                            + "   |   Draws: "
                            + draws
            );
        }

        if (
                levelLabel != null
        ) {

            levelLabel.setText(
                    "Level: "
                            + currentLevel
            );
        }
    }

    // =========================================================
    // RESET GAME
    // =========================================================

    private void resetGame() {

        playClickSound();

        gameOver = false;

        isPaused = false;

        isDraw = false;

        movesCount = 0;

        currentPlayer = 'X';

        isPlayer2Turn = false;

        initializeBoard();

        showGameScreen();
    }

    // =========================================================
    // INITIALIZE BOARD
    // =========================================================

    private void initializeBoard() {

        for (
                int row = 0;
                row < 3;
                row++
        ) {

            for (
                    int col = 0;
                    col < 3;
                    col++
            ) {

                board[row][col] =
                        ' ';

                cellButtons[row][col] =
                        null;
            }
        }
    }

    // =========================================================
    // GAME OVER SCREEN
    // =========================================================

    private void showGameOverScreen(
            boolean player1Won
    ) {

        mainMenuActive = false;

        setDefaultBackground();

        VBox gameOverBox =
                new VBox(20);

        gameOverBox.setAlignment(
                Pos.CENTER
        );

        gameOverBox.setPadding(
                new Insets(30)
        );

        Text title =
                new Text(
                        "GAME OVER"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        44
                )
        );

        title.setFill(
                Color.GOLD
        );

        title.setEffect(
                new DropShadow(
                        20,
                        Color.BLACK
                )
        );

        Text result;

        if (isDraw) {

            result =
                    new Text(
                            "🤝 IT'S A DRAW!"
                    );

        } else if (player1Won) {

            result =
                    new Text(
                            "🏆 PLAYER 1 WINS!"
                    );

        } else {

            result =
                    new Text(
                            "🤖 PLAYER 2 WINS!"
                    );
        }

        result.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        30
                )
        );

        result.setFill(
                player1Won
                        ? Color.LIMEGREEN
                        : Color.ORANGERED
        );

        Label finalScore =
                new Label(
                        "Player 1: "
                                + player1Score
                                + "     Player 2: "
                                + player2Score
                                + "     Draws: "
                                + draws
                );

        finalScore.setTextFill(
                Color.WHITE
        );

        finalScore.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        Label level =
                new Label(
                        "Level Reached: "
                                + currentLevel
                );

        level.setTextFill(
                Color.GOLD
        );

        level.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        Button playAgain =
                createStyledButton(
                        "🔄 PLAY AGAIN",
                        "#27ae60"
                );

        playAgain.setOnAction(
                event -> {

                    playClickSound();

                    showGameScreen();
                }
        );

        Button mainMenu =
                createStyledButton(
                        "🏠 MAIN MENU",
                        "#3498db"
                );

        mainMenu.setOnAction(
                event -> {

                    playClickSound();

                    showMainMenu();
                }
        );

        gameOverBox.getChildren().addAll(
                title,
                result,
                finalScore,
                level,
                playAgain,
                mainMenu
        );

        root.setTop(null);
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);

        root.setCenter(
                gameOverBox
        );
    }

    // =========================================================
    // INSTRUCTIONS SCREEN
    // =========================================================

    private void showInstructionsScreen() {

        mainMenuActive = false;

        setDefaultBackground();

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(30)
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        Text title =
                new Text(
                        "📖 HOW TO PLAY"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        40
                )
        );

        title.setFill(
                Color.GOLD
        );

        Label descriptionTitle =
                createSectionTitle(
                        "Game Description"
                );

        Label description =
                createInfoLabel(
                        "Tic Tac Toe is a two-player strategy game. "
                                + "Player 1 uses X while Player 2 uses O. "
                                + "The goal is to place three symbols in a "
                                + "horizontal, vertical, or diagonal line."
                );

        Label objectiveTitle =
                createSectionTitle(
                        "Objectives"
                );

        Label objectives =
                createInfoLabel(
                        "• Get three X symbols in a row.\n"
                                + "• Block Player 2 from winning.\n"
                                + "• Score more wins than your opponent.\n"
                                + "• Progress through the game levels."
                );

        Label controlsTitle =
                createSectionTitle(
                        "Controls"
                );

        Label controls =
                createInfoLabel(
                        "🖱 Mouse: Click an empty square to play.\n"
                                + "⌨ R: Reset the game.\n"
                                + "⌨ P: Pause or resume the game."
                );

        Label rulesTitle =
                createSectionTitle(
                        "Rules"
                );

        Label rules =
                createInfoLabel(
                        "1. Player 1 starts with X.\n"
                                + "2. Player 2 uses O.\n"
                                + "3. Players take turns.\n"
                                + "4. A player wins by getting three symbols "
                                + "in a row.\n"
                                + "5. If all nine spaces are filled without "
                                + "a winner, the game is a draw."
                );

        Label difficultyTitle =
                createSectionTitle(
                        "Difficulty Levels"
                );

        Label difficultyInfo =
                createInfoLabel(
                        "Easy: Player 2 makes random moves.\n"
                                + "Normal: Player 2 combines strategic and "
                                + "random moves.\n"
                                + "Hard: Player 2 uses an advanced strategy."
                );

        Button backButton =
                createStyledButton(
                        "⬅ BACK TO MAIN MENU",
                        "#3498db"
                );

        backButton.setOnAction(
                event -> {

                    playClickSound();

                    showMainMenu();
                }
        );

        content.getChildren().addAll(
                title,
                descriptionTitle,
                description,
                objectiveTitle,
                objectives,
                controlsTitle,
                controls,
                rulesTitle,
                rules,
                difficultyTitle,
                difficultyInfo,
                backButton
        );

        ScrollPane scrollPane =
                new ScrollPane(
                        content
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background: transparent;" +
                "-fx-background-color: transparent;"
        );

        root.setTop(null);
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);

        root.setCenter(
                scrollPane
        );
    }

    // =========================================================
    // SETTINGS SCREEN
    // =========================================================

    private void showSettingsScreen() {

        mainMenuActive = false;

        setDefaultBackground();

        VBox settingsBox =
                new VBox(20);

        settingsBox.setAlignment(
                Pos.CENTER
        );

        settingsBox.setPadding(
                new Insets(30)
        );

        Text title =
                new Text(
                        "⚙ SETTINGS"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        40
                )
        );

        title.setFill(
                Color.GOLD
        );

        // ---------------------------------------------------------
        // SOUND
        // ---------------------------------------------------------

        Button soundButton =
                new Button();

        updateSoundButtonStyle(
                soundButton
        );

        soundButton.setOnAction(
                event -> {

                    soundOn = !soundOn;

                    updateSoundButtonStyle(
                            soundButton
                    );

                    playClickSound();
                }
        );

        // ---------------------------------------------------------
        // MUSIC
        // ---------------------------------------------------------

        Button musicButton =
                new Button();

        updateMusicButtonStyle(
                musicButton
        );

        musicButton.setOnAction(
                event -> {

                    musicOn = !musicOn;

                    if (musicOn) {

                        startMusic();

                    } else {

                        stopMusic();
                    }

                    updateMusicButtonStyle(
                            musicButton
                    );
                }
        );

        // ---------------------------------------------------------
        // DIFFICULTY
        // ---------------------------------------------------------

        Label difficultyLabel =
                new Label(
                        "Difficulty:"
                );

        difficultyLabel.setTextFill(
                Color.WHITE
        );

        difficultyLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        ComboBox<String> difficultyBox =
                new ComboBox<>();

        difficultyBox.getItems().addAll(
                "Easy",
                "Normal",
                "Hard"
        );

        difficultyBox.setValue(
                difficulty
        );

        difficultyBox.setOnAction(
                event -> {

                    difficulty =
                            difficultyBox.getValue();

                    playClickSound();
                }
        );

        HBox difficultyRow =
                new HBox(15);

        difficultyRow.setAlignment(
                Pos.CENTER
        );

        difficultyRow.getChildren().addAll(
                difficultyLabel,
                difficultyBox
        );

        // ---------------------------------------------------------
        // VOLUME
        // ---------------------------------------------------------

        Label volumeLabel =
                new Label(
                        "Volume:"
                );

        volumeLabel.setTextFill(
                Color.WHITE
        );

        volumeLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        Slider volumeSlider =
                new Slider(
                        0,
                        1,
                        volumeLevel
                );

        volumeSlider.setPrefWidth(
                250
        );

        volumeSlider.valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            volumeLevel =
                                    newValue.doubleValue();

                            if (
                                    musicPlayer
                                            != null
                            ) {

                                musicPlayer.setVolume(
                                        volumeLevel
                                );
                            }
                        }
                );

        HBox volumeRow =
                new HBox(15);

        volumeRow.setAlignment(
                Pos.CENTER
        );

        volumeRow.getChildren().addAll(
                volumeLabel,
                volumeSlider
        );

        // ---------------------------------------------------------
        // BACK
        // ---------------------------------------------------------

        Button backButton =
                createStyledButton(
                        "⬅ BACK",
                        "#3498db"
                );

        backButton.setOnAction(
                event -> {

                    playClickSound();

                    showMainMenu();
                }
        );

        settingsBox.getChildren().addAll(
                title,
                soundButton,
                musicButton,
                difficultyRow,
                volumeRow,
                backButton
        );

        root.setTop(null);
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);

        root.setCenter(
                settingsBox
        );
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private Label createSectionTitle(
            String text
    ) {

        Label label =
                new Label(text);

        label.setTextFill(
                Color.GOLD
        );

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        22
                )
        );

        return label;
    }

    // =========================================================
    // INFO LABEL
    // =========================================================

    private Label createInfoLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setTextFill(
                Color.WHITE
        );

        label.setFont(
                Font.font(
                        "Arial",
                        16
                )
        );

        label.setWrapText(true);

        label.setMaxWidth(
                800
        );

        return label;
    }

    // =========================================================
    // SOUND BUTTON STYLE
    // =========================================================

    private void updateSoundButtonStyle(
            Button button
    ) {

        if (soundOn) {

            button.setText(
                    "🔊 SOUND: ON"
            );

            button.setStyle(
                    "-fx-background-color: #27ae60;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 30 12 30;"
            );

        } else {

            button.setText(
                    "🔇 SOUND: OFF"
            );

            button.setStyle(
                    "-fx-background-color: #7f8c8d;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 30 12 30;"
            );
        }
    }

    // =========================================================
    // MUSIC BUTTON STYLE
    // =========================================================

    private void updateMusicButtonStyle(
            Button button
    ) {

        if (musicOn) {

            button.setText(
                    "🎵 MUSIC: ON"
            );

            button.setStyle(
                    "-fx-background-color: #27ae60;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 30 12 30;"
            );

        } else {

            button.setText(
                    "🔇 MUSIC: OFF"
            );

            button.setStyle(
                    "-fx-background-color: #7f8c8d;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 12 30 12 30;"
            );
        }
    }

    // =========================================================
    // CREATE STYLED BUTTON
    // =========================================================

    private Button createStyledButton(
            String text,
            String color
    ) {

        Button button =
                new Button(text);

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        button.setTextFill(
                Color.WHITE
        );

        button.setPadding(
                new Insets(
                        12,
                        30,
                        12,
                        30
                )
        );

        button.setMinWidth(
                180
        );

        applyStyledButtonStyle(
                button,
                color,
                false
        );

        button.setOnMouseEntered(
                event ->
                        applyStyledButtonStyle(
                                button,
                                color,
                                true
                        )
        );

        button.setOnMouseExited(
                event ->
                        applyStyledButtonStyle(
                                button,
                                color,
                                false
                        )
        );

        return button;
    }

    // =========================================================
    // BUTTON STYLE
    // =========================================================

    private void applyStyledButtonStyle(
            Button button,
            String color,
            boolean hover
    ) {

        String effect;

        if (hover) {

            effect =
                    "dropshadow(" +
                    "gaussian, " +
                    "rgba(255,255,255,0.45), " +
                    "15, 0, 0, 5" +
                    ")";

        } else {

            effect =
                    "dropshadow(" +
                    "gaussian, " +
                    "rgba(0,0,0,0.45), " +
                    "8, 0, 0, 3" +
                    ")";
        }

        button.setStyle(
                "-fx-background-color: "
                        + color
                        + ";" +

                "-fx-text-fill: white;" +

                "-fx-font-weight: bold;" +

                "-fx-background-radius: 12;" +

                "-fx-border-radius: 12;" +

                "-fx-effect: "
                        + effect
                        + ";" +

                "-fx-cursor: hand;"
        );
    }

    // =========================================================
    // LOAD SOUNDS
    // =========================================================

    private void loadSounds() {

        try {

            // -----------------------------------------------------
            // CLICK SOUND
            // -----------------------------------------------------

            File clickFile =
                    new File(
                            "sounds/click.mp3"
                    );

            if (
                    clickFile.exists()
            ) {

                clickSound =
                        new AudioClip(
                                clickFile
                                        .toURI()
                                        .toString()
                        );

                clickSound.setVolume(
                        volumeLevel
                );

                System.out.println(
                        "SUCCESS: click.mp3 loaded."
                );

            } else {

                System.out.println(
                        "ERROR: sounds/click.mp3 not found."
                );
            }

            // -----------------------------------------------------
            // MUSIC
            // -----------------------------------------------------

            File musicFile =
                    new File(
                            "sounds/music.mp3"
                    );

            if (
                    musicFile.exists()
            ) {

                Media media =
                        new Media(
                                musicFile
                                        .toURI()
                                        .toString()
                        );

                musicPlayer =
                        new MediaPlayer(
                                media
                        );

                musicPlayer.setCycleCount(
                        MediaPlayer.INDEFINITE
                );

                musicPlayer.setVolume(
                        volumeLevel
                );

                System.out.println(
                        "SUCCESS: music.mp3 loaded."
                );

            } else {

                System.out.println(
                        "ERROR: sounds/music.mp3 not found."
                );
            }

            // Use click sound as win sound
            winSound =
                    clickSound;

        } catch (Exception e) {

            System.out.println(
                    "ERROR loading sounds: "
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // CLICK SOUND
    // =========================================================

    private void playClickSound() {

        if (
                soundOn &&
                clickSound != null
        ) {

            clickSound.setVolume(
                    volumeLevel
            );

            clickSound.play();
        }
    }

    // =========================================================
    // WIN SOUND
    // =========================================================

    private void playWinSound() {

        if (
                soundOn &&
                winSound != null
        ) {

            winSound.setVolume(
                    volumeLevel
            );

            winSound.play();
        }
    }

    // =========================================================
    // START MUSIC
    // =========================================================

    private void startMusic() {

        if (
                musicOn &&
                musicPlayer != null
        ) {

            musicPlayer.setVolume(
                    volumeLevel
            );

            musicPlayer.play();
        }
    }

    // =========================================================
    // STOP MUSIC
    // =========================================================

    private void stopMusic() {

        if (
                musicPlayer != null
        ) {

            musicPlayer.stop();
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}