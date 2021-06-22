package application.view;

import javafx.scene.paint.Color;

public enum TextColors {
	
	LEVEL0(Color.web("#776e65")),
	LEVEL1(Color.web("#776e65")), 
	LEVEL2(Color.web("#776e65")),
	LEVEL3(Color.web("#f9f6f2")),
	LEVEL4(Color.web("#f9f6f2")),
	LEVEL5(Color.web("#f9f6f2")),
	LEVEL6(Color.web("#f9f6f2")),
	LEVEL7(Color.web("#f9f6f2")),
	LEVEL8(Color.web("#f9f6f2")),
	LEVEL9(Color.web("#f9f6f2")),
	LEVEL10(Color.web("#f9f6f2")),
	LEVEL11(Color.web("#f9f6f2")),
	LEVEL12(Color.web("#f9f6f2")),
	LEVEL13(Color.web("#f9f6f2")),
	LEVEL14(Color.web("#f9f6f2")),
	LEVEL15(Color.web("#f9f6f2")),
	LEVEL16(Color.web("#f9f6f2")),
	LEVEL17(Color.web("#f9f6f2")),
	LEVEL18(Color.web("#f9f6f2"));
	
	Color color;
	
	TextColors(Color color){ this.color=color; }
	Color getColor() { return this.color; }
}
