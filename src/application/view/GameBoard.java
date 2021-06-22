package application.view;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameBoard {
	
	public int size, cap;
	public int [] Board;
	public int score;
	public Block[] blocks;
	public int BlockLen=100;
	public int BlockTerm=10;
	public int len;
	
	public GameBoard(int size) { // reset game board
		this.size=size; this.cap=size*size;
		Board= new int[this.cap];
		blocks=new Block[this.cap];
		len=size*BlockLen+(size+1)*BlockTerm;
		for(int i=0; i<this.size; i++)
			for(int j=0; j<this.size; j++)
				blocks[this.size*i+j] = new Block(i, j, BlockLen, BlockTerm);
	}
	
	public void setState(int[] Board) { // int[] -> UI
		for(int i=0; i<this.cap;i++) {
			this.Board[i]=Board[i];
			blocks[i].setNum(this.Board[i]);
		}
	}
	
	public void showGameBoard(BorderPane root) { // show full game board
		AnchorPane map = new AnchorPane(); map.resize(len, len);
		Rectangle rect = new Rectangle(0,0,len,len); rect.setFill(Color.web("#bbada0"));
		map.getChildren().add(rect);
		showBlocks(map); root.setCenter(map);
	}
	
	public void showBlocks(AnchorPane map) {
		for(int i=0; i<this.cap; i++) blocks[i].showBlock(map);
	}
	
}
