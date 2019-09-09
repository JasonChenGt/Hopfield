package application;

import javafx.scene.Group;

public class Data extends Group{
	char[] x ; // 文字輸入
	char[] y ; // 文字輸出
	double[] trans ;
	double[] noise ;
	
	public Data(String array) { // 資料設定處理
		x = array.toCharArray() ;
		trans = new double[x.length] ;	
		noise = new double[x.length] ;
	}
	
	public void transform(double noiseP , int TrainOrTest) { // 將資料集轉換
		if (TrainOrTest == 1) { // 若是訓練資料集，則增加雜訊
			for (int i = 0; i < x.length; i++) {
				if (Math.random()*100 < noiseP) {
					if (x[i] == ' ') {
						noise[i] = 1 ;
					}
					else noise[i] = -1 ;
				}
				else {
					if (x[i] == ' ') {
						noise[i] = -1 ;
					}
					else noise[i] = 1 ;
				}
			}
		}
		// 資料集做轉換
		for (int i = 0; i < x.length; i++) {
			if (x[i] == ' ') {
				trans[i] = -1 ;
			}
			else trans[i] = 1 ;
		}
	}
	
	public void finalY(double[] finalNoise) { // 將回想完的結果轉換回文字
		y = new char[x.length] ;
		for (int i = 0; i < x.length; i++) {
			if (finalNoise[i] == -1) {
				y[i] = ' ' ;
			}
			else {
				y[i] = '1' ;
			}
		}
	}
}
