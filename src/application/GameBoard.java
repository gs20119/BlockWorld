package application;

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameBoard {
	public int size;
	public int cap;
	public int [] Board;
	public int score;
	public Block[] blocks;
	public int BlockLen=100;
	public int BlockTerm=10;
	public int len;
	
	public GameBoard(int size)
	{
		this.size=size;
		this.cap=size*size;
		Board= new int[this.cap];
		blocks=new Block[this.cap];
		len=size*BlockLen+(size+1)*BlockTerm;
		for(int i=0; i<this.cap; i++)
		{
			blocks[i]=new Block(i/size, i%size, BlockLen, BlockTerm);
		}
	}
	
	public void setState(int [] Board)
	{
		for(int i=0; i<this.cap;i++)
		{
			this.Board[i]=Board[i];
			blocks[i].setNum(this.Board[i]);
		}
	}
	
	public void printGameBoard(BorderPane root)
	{
		AnchorPane map=new AnchorPane();
		map.resize(len, len);
		printBoard(map);
		printBlocks(map);
		
		root.setCenter(map);
	}
	
	public void printBoard(AnchorPane map)
	{
		Rectangle rect=new Rectangle(0,0,len,len);
		rect.setFill(Color.web("#bbada0"));
		map.getChildren().add(rect);
	}
	
	public void printBlocks(AnchorPane map)
	{
		for(int i=0; i<this.cap; i++)
		{
			blocks[i].printBlock(map);
		}
	}
}
