package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int col = 7;
	private static final int rows = 6;
	private static final int dia = 80;
	private static final String col1 = "#24303E";
	private static final String col2 = "#4CAA88";
	private static String one = "Player One";
	private static String two = "Player Two";
	private boolean isone = true;

	private Disc[][] insertedDiscArray=new Disc[rows][col];
	@FXML
	public GridPane grid;
	@FXML
	public TextField text1;
	@FXML
	public TextField text2;
	@FXML
	public Pane discpane;
	@FXML
	public Label label;
	@FXML
	public Button setnames;
	private boolean isAllowedToInsert=true;

	public void createPlayground() {
		Shape rec = structuralGrid();
		grid.add(rec, 0, 1);
		List<Rectangle> rectangleList = click();
		for (Rectangle rectangle : rectangleList) {
			grid.add(rectangle, 0, 1);
		}
	}

	private Shape structuralGrid() {
		Shape rec = new Rectangle((col + 1) * dia, (rows + 1) * dia);
		for (int row = 0; row < rows; row++) {
			for (int cols = 0; cols < col; cols++) {

				Circle circle = new Circle();
				circle.setRadius(dia / 2);
				circle.setCenterX(dia / 2);
				circle.setCenterY(dia / 2);
				circle.setSmooth(true);

				circle.setTranslateX(cols * (dia + 5) + dia / 4);
				circle.setTranslateY(row * (dia + 5) + dia / 4);

				rec = Shape.subtract(rec, circle);
			}
		}
		rec.setFill(Color.WHITE);
		return rec;
	}

	private List<Rectangle> click() {
		List<Rectangle> reclist = new ArrayList<>();
		for (int i = 0; i < col; i++) {
			Rectangle rectangle = new Rectangle(dia, (rows + 1) * dia);
			rectangle.setFill(Color.TRANSPARENT);

			rectangle.setTranslateX(i * (dia + 5) + dia / 4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = i;
			rectangle.setOnMouseClicked(event ->{
					if(isAllowedToInsert){
				isAllowedToInsert=false;
					insertDisc(new Disc(isone), column);
					}
					});
			reclist.add(rectangle);
		}
		return reclist;
	}

	private void insertDisc(Disc disc, int column) {

		int row=rows-1;
		while(row>=0){
			if(getDiscIfPresent(row,column)==null)
				break;
			row--;
		}
		if(row<0){
			return;
		}
		insertedDiscArray[row][column]=disc;
		discpane.getChildren().add(disc);
		disc.setTranslateX(column*(dia+5) + dia/4);
		int currentRow=row;
		TranslateTransition t=new TranslateTransition(Duration.seconds(0.5),disc);
		t.setToY(row*(dia+5) + dia/4);
		t.setOnFinished(event -> {
			isAllowedToInsert=true;
			if(gameEnded(currentRow,column)){
               gameOver();
               return;
			}
			isone=!isone;
			label.setText(isone? one: two);
				}
		);
		t.play();

	}

	private boolean gameEnded(int currentRow, int column) {
		List<Point2D>verticalPoints=IntStream.rangeClosed(currentRow -3,currentRow+3)
				.mapToObj(r-> new Point2D(r,column))
				.collect(Collectors.toList());
		List<Point2D> horizontalPoints=IntStream.rangeClosed(column -3,column+3)
				.mapToObj(col-> new Point2D(currentRow,col))
				.collect(Collectors.toList());
		Point2D startPoint= new Point2D(currentRow-3, column+3);
		List<Point2D> diagonalPoints=IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint.add(i,-i))
				.collect(Collectors.toList());
		Point2D startPoint1= new Point2D(currentRow-3, column-3);
		List<Point2D> diagonal2Points=IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint1.add(i,i))
				.collect(Collectors.toList());


		boolean isEnded=checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonalPoints) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
			if (disc != null && disc.isPlayerOneMove == isone) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
					chain = 0;
				}
			}
			return false;
	}

	private Disc getDiscIfPresent(int row,int column){
		if(row>=rows || row<0 || column>=col || column<0)
			return null;
		return insertedDiscArray[row][column];
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(dia / 2);
			setFill(isPlayerOneMove ? Color.valueOf(col1) : Color.valueOf(col2));
			setCenterX(dia / 2);
			setCenterY(dia / 2);
		}
	}

	private void gameOver() {
		String winner=isone? one:two;
		System.out.println("Winner is:" + " "+ winner);
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect four");
		alert.setHeaderText("The Winner is" + " " + winner);
		alert.setContentText("Want to Play Again?");
		ButtonType y=new ButtonType("yes");
		ButtonType n=new ButtonType("No");
		alert.getButtonTypes().setAll(y,n);
        Platform.runLater(() -> {
	        Optional<ButtonType> btn=alert.showAndWait();
	        if(btn.isPresent() && btn.get()==y){
		        resetgame();
	        }else{
		        Platform.exit();
		        System.exit(0);
	        }

        });
	}

	public void resetgame() {
		discpane.getChildren().clear();
		for (int row=0;row<insertedDiscArray.length;row++){
			for(int col=0;col<insertedDiscArray[row].length;col++){
				insertedDiscArray[row][col]=null;
			}
		}
		isone=true;
		label.setText(one);
		createPlayground();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setnames.setOnAction(event -> {
			String input1=text1.getText();
			one=input1;
			String input2=text2.getText();
			two=input2;
		});
	}
}



