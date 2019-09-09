package application;

import java.util.ArrayList;

import javafx.scene.Group;

public class Hopfield extends Group{
	double[][] W ;
	double[] theta ;
	int thinkTimes = 0 ;
	int thinkNoiseTimes = 0 ;
	public Hopfield(ArrayList<Data> listOfTrainingData , int length) {
		W = new double[length][length] ;
		theta = new double[length] ;
////////////////////////////////////////////////// 網路學習 ////////////////////////////////////////////////////
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				for (int k = 0; k < listOfTrainingData.size() ; k++) {
					W[i][j] += listOfTrainingData.get(k).trans[j] * listOfTrainingData.get(k).trans[i];
				}
				if (i == j) {
					W[i][j] -= listOfTrainingData.size();
				}
				W[i][j] /= length ;
			}
		}
	}
	
	public void HopfieldThink(Data testing , int length , int NoiseOrTest) {
		boolean stop = false ;
		double[] oldnoise = new double[length];
		double[] oldtrans = new double[length];
//////////////////////////////////////////////////// 網路回想 ////////////////////////////////////////////////////
		while (!stop) {
			if (NoiseOrTest == 1) { // 若是訓練資料集加雜訊，則使用noise的陣列做回想
				for (int i = 0; i < length; i++) {
					oldnoise[i] = testing.noise[i] ;
				}
				for (int i = 0; i < length; i++) {
					double temp = 0 ;
					for (int j = 0; j < length; j++) {
						temp += W[i][j]*testing.noise[j] ;
					}
					if (temp-theta[i] >= 0) {
						testing.noise[i] = 1 ;
					}
					else if (temp-theta[i] < 0) {
						testing.noise[i] = -1 ;
					}
				}
				for (int i = 0; i < length; i++) {
					if (testing.noise[i] == oldnoise[i]) {
						stop = true ;
					}
					else {
						stop = false ;
						break ;
					}
				}
				thinkNoiseTimes++ ;
			}
			else { // 若是測試資料集，則使用trans的陣列做回想
				for (int i = 0; i < length; i++) {
					oldtrans[i] = testing.trans[i] ;
				}
				for (int i = 0; i < length; i++) {
					double temp = 0 ;
					for (int j = 0; j < length; j++) {
						temp += W[i][j]*testing.trans[j] ;
					}
					if (temp-theta[i] >= 0) {
						testing.trans[i] = 1 ;
					}
					else if (temp-theta[i] < 0) {
						testing.trans[i] = -1 ;
					}
				}
				for (int i = 0; i < length; i++) {
					if (testing.trans[i] == oldtrans[i]) {
						stop = true ;
					}
					else {
						stop = false ;
						break ;
					}
				}
				thinkTimes++ ;
			}
		}
		if (NoiseOrTest == 1) {
			testing.finalY(testing.noise);
		}
		else {
			testing.finalY(testing.trans);
		}
	}
}
