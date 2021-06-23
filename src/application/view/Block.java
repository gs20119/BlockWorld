package application.view;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Block {

	public int x, y, len;
	public int num, term;
	Rectangle rect;
	Text text;
	
	BlockColors bc=BlockColors.LEVEL0;
	TextColors tc=TextColors.LEVEL0;
	
	public Block(int i, int j, int len, int term, AnchorPane map) {
		this.y=i; this.x=j;
		this.len=len; this.term=term;
		rect=new Rectangle( x*(len+term)+term,y*(len+term)+term , len, len);
		text=new Text();
		map.getChildren().add(rect);
		map.getChildren().add(text);
	}

	public void setNum(int t) {
		this.num = (t==0)?0:(int)Math.pow(2,t);
		bc=BlockColors.valueOf("LEVEL"+t);
		tc=TextColors.valueOf("LEVEL"+t);
	}

	public void showBlock() {	
		rect.setFill(bc.getColor());
		if(num==0) text.setText("");
		else text.setText(num+"");
		text.setFill(tc.getColor());   // text color
        text.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        double fontX=text.getLayoutBounds().getWidth();
		double fontY=text.getLayoutBounds().getHeight();
		text.setLayoutX(x*(len+term)+term+len/2-fontX/2);
		text.setLayoutY(y*(len+term)+term+len/2+fontY/2);
	}

}
