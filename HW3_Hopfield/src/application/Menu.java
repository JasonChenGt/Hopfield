package application;

import java.io.* ;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Menu extends Scene {
	Button loadTrainingFileB = (Button)lookup("#loadTrainingFile");
	Button TrainingB = (Button)lookup("#Training") ;
	Button loadTestingFileB = (Button)lookup("#loadTestingFile");
	Button TestingB = (Button)lookup("#Testing") ;
	Text TrainingPath = (Text)lookup("#TrainingPath") ;
	Text TestingPath = (Text)lookup("#TestingPath") ;
	Text thinkTimesT = (Text)lookup("#thinkTimes") ;
	Text testingRecRateT = (Text)lookup("#testingRecRate") ;
	Text thinkNoiseTimesT = (Text)lookup("#thinkNoiseTimes") ;
	Text NoiseRecRateT = (Text)lookup("#NoiseRecRate") ;
	TextField noiseT = (TextField)lookup("#noise") ;
	TextArea trainTA = (TextArea)lookup("#trainTA") ;
	TextArea NoiseTA = (TextArea)lookup("#NoiseTA") ;
	TextArea testTA = (TextArea)lookup("#testTA") ;
	
	ArrayList<Data> listOfTrainingData ;
	ArrayList<Data> listOfTestingData ;
	
	File TrainingFile ;
	File TestingFile ;
	
	Hopfield hopfield ;
	
	int row , column;
	double noiseP ;
	double[] TcorrectA ;
	double[] NcorrectA ;
	
	public Menu(Main m,Parent root) {
		super(root);
		initial() ;
		loadTrainingFileB.setOnMouseClicked(new EventHandler<MouseEvent>() { // 按鈕：載入訓練資料集
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(extFilter);
	            fileChooser.setTitle("Choose a txt file");
	            TrainingFile = fileChooser.showOpenDialog(m.stage);
            	listOfTrainingData = new ArrayList<>();	
                if (TrainingFile != null) {
                    loadFile(TrainingFile , 1) ;
                }
			}
		});
		
		loadTestingFileB.setOnMouseClicked(new EventHandler<MouseEvent>() { // 按鈕：載入測試資料集
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
	            fileChooser.getExtensionFilters().add(extFilter);
	            fileChooser.setTitle("Choose a txt file");
	            TestingFile = fileChooser.showOpenDialog(m.stage);
            	listOfTestingData = new ArrayList<>();
                if (TestingFile != null) {
                    loadFile(TestingFile , 2) ;
                }
			}
		});
		
		TrainingB.setOnMouseClicked(new EventHandler<MouseEvent>() { // 按鈕：將訓練資料集使用Hopfield訓練，並將加了雜訊的訓練資料集做轉換
			@Override
			public void handle(MouseEvent event) {
				if (TrainingFile != null) {
					for (int i = 0; i < listOfTrainingData.size(); i++) {
						listOfTrainingData.get(i).transform(noiseP , 1);
					}
					
					printText(listOfTrainingData , 1);
					hopfield = new Hopfield(listOfTrainingData, listOfTrainingData.get(0).x.length) ;
				}
			}
		});	
		
		TestingB.setOnMouseClicked(new EventHandler<MouseEvent>() { // 按扭：將測試資料集做轉換，並將加了雜訊的訓練資料集和測試資料集做回想
			@Override
			public void handle(MouseEvent event) {
				if (TestingFile != null) {
					for (int i = 0; i < listOfTestingData.size(); i++) {
						listOfTestingData.get(i).transform(noiseP , 2);
					}
					hopfield.thinkTimes = 0 ;
					hopfield.thinkNoiseTimes = 0 ;
					think();
				}
			}
		});
		
		noiseT.textProperty().addListener(new ChangeListener<String>() { // 輸入欄位：雜訊量，0% ~ 100%，不在範圍內會顯示紅字
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("([1-9]?)([0-9])(([.][0-9]*)?)") || newValue.matches("100([.][0]*)?")) {
					noiseT.setStyle("-fx-text-inner-color: black;"); 
	    	    	noiseP = Double.parseDouble(noiseT.getText());
	    	    }
		    	else if (newValue.isEmpty()) {
		    		noiseP = 0 ;
		    	}
		    	else {
		    		noiseT.setStyle("-fx-text-inner-color: red;"); 
		        }
		    }
		});
	}

	private void initial() { // 初始化
		row = 0 ;
		column = 0 ;
    	noiseP = 0 ;
	}
	

	private void think() { // 執行Hopfield回想
		DecimalFormat df = new DecimalFormat("######0.00");
		double Tcorrect = 0 ;
		double Ncorrect = 0 ;
		TcorrectA = new double[listOfTestingData.size()] ;
		NcorrectA = new double[listOfTrainingData.size()] ;
		
		for (int i = 0; i < listOfTrainingData.size() ; i++) { // 將加了雜訊的訓練資料集做回想
			hopfield.HopfieldThink(listOfTrainingData.get(i) , listOfTrainingData.get(0).x.length , 1) ;
			for (int j = 0; j < listOfTrainingData.get(0).x.length ; j++) {
				if (listOfTrainingData.get(i).y[j] == listOfTrainingData.get(i).x[j]) {
					NcorrectA[i]++ ;
					Ncorrect++ ;
				}
			}
		}
		for (int i = 0; i < listOfTestingData.size() ; i++) { // 將測試資料集做回想
			hopfield.HopfieldThink(listOfTestingData.get(i) , listOfTestingData.get(0).x.length , 2) ;
			for (int j = 0; j < listOfTestingData.get(0).x.length ; j++) {
				if (listOfTestingData.get(i).y[j] == listOfTrainingData.get(i).x[j]) {
					TcorrectA[i]++ ;
					Tcorrect++ ;
				}
			}
		}
		
		// 顯示資訊
		printText(listOfTrainingData , 2);
		printText(listOfTestingData , 3);
		thinkNoiseTimesT.setText(""+hopfield.thinkNoiseTimes) ;
		thinkTimesT.setText(""+hopfield.thinkTimes) ;
		Tcorrect = 100*Tcorrect/(listOfTestingData.size()*listOfTestingData.get(0).x.length);
		testingRecRateT.setText("" + df.format(Tcorrect) + " %");
		Ncorrect = 100*Ncorrect/(listOfTrainingData.size()*listOfTrainingData.get(0).x.length);
		NoiseRecRateT.setText("" + df.format(Ncorrect) + " %");
	}
	
	private void printText(ArrayList<Data> listOfData , int TrainOrNoiseOrTest) { // 顯示圖形和各自的正確率
		String text = "" ;
		for (int i = 0; i < listOfData.size() ; i++) {
			switch (TrainOrNoiseOrTest) {
			case 1:
				text += ("Case" + (i+1) + "\n") ;
				break;
			case 2:
				text += ("Case" + (i+1) + " 正確率：" + 100*NcorrectA[i]/listOfTrainingData.get(0).x.length + "%\n") ;
				break;
			case 3:
				text += ("Case" + (i+1) + " 正確率：" + 100*TcorrectA[i]/listOfTestingData.get(0).x.length + "%\n") ;
				break;
			}
			
			int count = 0 ;
			for (int j = 0; j < row ; j++) {
				for (int k = 0; k < column ; k++) {
					if (TrainOrNoiseOrTest == 1) { // 若是訓練，看Ｘ
						if (listOfData.get(i).x[count++] == ' ') {
							text += "　" ;
						}
						else {
							text += "１" ;
						}
					}
					else { // 若是回想後，看Y
						if (listOfData.get(i).y[count++] == ' ') {
							text += "　" ;
						}
						else {
							text += "１" ;
						}
					}
				}
				text += "\n" ;
			}
			text += "\n" ;
		}
		switch (TrainOrNoiseOrTest) {
			case 1:
				trainTA.setText(text);
				break;
			case 2:
				NoiseTA.setText(text);
				break;
			case 3:
				testTA.setText(text);
				break;
		}
	}
	
	
	
	private void loadFile(File file , int TrainOrTest) { // 載入資料集
		BufferedReader bufferedReader = null ;
		Data data ;
		if (TrainOrTest == 1) { // 載入訓練資料集
			TrainingPath.setText(file.getPath());
			try {
				bufferedReader = new BufferedReader(new FileReader(file));
	            String text , array="";
	            while ((text = bufferedReader.readLine()) != null) {
	            	if (text.isEmpty()) {
	            		data = new Data(array) ;
		            	listOfTrainingData.add(data) ;
		            	array = "";
		            	row = 0 ;
					}
	            	else {
	            		array += text ;
	            		column = text.length() ;
	            		row++ ;
	            	}
	            }
	            data = new Data(array) ;
            	listOfTrainingData.add(data) ;
			}catch (Exception e) {
				// TODO: handle exception
	        } finally {
	            try {
	                bufferedReader.close();
	            } catch (Exception e) {}
	        }
		}
		else { // 載入測試資料集
			TestingPath.setText(file.getPath());
			try {
				bufferedReader = new BufferedReader(new FileReader(file));
	            String text , array="";
	            while ((text = bufferedReader.readLine()) != null) {
	            	if (text.isEmpty()) {
	            		data = new Data(array) ;
		            	listOfTestingData.add(data) ;
		            	array = "";
		            	row = 0 ;
					}
	            	else {
	            		array += text ;
	            		column = text.length() ;
	            		row++ ;
	            	}
	            }
	            data = new Data(array) ;
            	listOfTestingData.add(data) ;
			}catch (Exception e) {
				// TODO: handle exception
	        } finally {
	            try {
	                bufferedReader.close();
	            } catch (Exception e) {}
	        }
		}
	}
}