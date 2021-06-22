package application.view;

import javafx.scene.paint.Color;

public enum BlockColors {
	
	LEVEL0(Color.web("#cdc1b4")),
	LEVEL1(Color.web("#eee4da")), 
	LEVEL2(Color.web("#ede0c8")),
	LEVEL3(Color.web("#f2b179")),
	LEVEL4(Color.web("#f59563")),
	LEVEL5(Color.web("#f67c5f")),
	LEVEL6(Color.web("#f65e3b")),
	LEVEL7(Color.web("#edcf72")),
	LEVEL8(Color.web("#edcc61")),
	LEVEL9(Color.web("#edc850")),
	LEVEL10(Color.web("#edc53f")),
	LEVEL11(Color.web("#edc22e")),
	LEVEL12(Color.web("#b885ac")),
	LEVEL13(Color.web("#af6da9")),
	LEVEL14(Color.web("#ab61a7")),
	LEVEL15(Color.web("#a755a6")),
	LEVEL16(Color.web("#3c3a32")),
	LEVEL17(Color.web("#3c3a32")),
	LEVEL18(Color.web("#3c3a32"));
	
	Color color;
	
	BlockColors(Color color){ this.color=color; }
	Color getColor(){ return this.color; }
}
