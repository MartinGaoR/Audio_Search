package Search;

import Feature.*;
import SignalProcess.WaveIO;
import Distance.*;
import Tool.SortHashMapByValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by workshop on 9/18/2015.
 */
public class SearchDemo {
    /**
     * Please replace the 'trainPath' with the specific path of train set in your PC.
     */
    protected final static String trainPath = "/Users/svarshney/Desktop/Semester 1/cs2108/Assignments/Assignment 2/data/input/train";
    private double[] coefficients;
    int choice = 0;
    public SearchDemo(double a,double b,double c,double d, int num){
		this.coefficients = new double[4];
		this.coefficients[0] = a;
		this.coefficients[1] = b;
		this.coefficients[2] = c;
		this.coefficients[3] = d;
		choice = num;
	}

    /***
     * Get the feature of train set via the specific feature extraction method, and write it into offline file for efficiency;
     * Please modify this function, select or combine the methods (in the Package named 'Feature') to extract feature, such as Zero-Crossing, Energy, Magnitude-
     * Spectrum and MFCC by yourself.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    public HashMap<String,double[]> trainFeatureList(){
        File trainFolder = new File(trainPath);
        File[] trainList = trainFolder.listFiles();

        HashMap<String, double[]> MSfeatureList = new HashMap<>();
        HashMap<String, double[]> ENfeatureList = new HashMap<>();
        HashMap<String, double[]> ZCfeatureList = new HashMap<>();
        HashMap<String, double[]> MFfeatureList = new HashMap<>();
        try {

            FileWriter fw1 = new FileWriter("data/feature/allFeatureMS.txt");
            FileWriter fw2 = new FileWriter("data/feature/allFeatureEN.txt");
            FileWriter fw3 = new FileWriter("data/feature/allFeatureZC.txt");
            FileWriter fw4 = new FileWriter("data/feature/allFeatureMF.txt");
            for (int i = 0; i < trainList.length; i++) {
                WaveIO waveIO = new WaveIO();
                short[] signal = waveIO.readWave(trainList[i].getAbsolutePath());
                System.out.println(trainList[i].getAbsolutePath());

                /**
                 * Example of extracting feature via MagnitudeSpectrum, modify it by yourself.
                 */
                MagnitudeSpectrum ms = new MagnitudeSpectrum();
                double[] msFeature = ms.getFeature(signal);
                
                Energy en = new Energy();
                FFTProcess fft = new FFTProcess();
                MFCC mf = new MFCC();
                ZeroCrossing zc = new ZeroCrossing();
                double[] enFeature = en.getFeature(signal);
                mf.process(signal);
                double[] mfMean = mf.getMeanFeature();
                double[] zcFeature = zc.getFeature(signal);
                /**
                 * Write the extracted feature into offline file;
                 */
                MSfeatureList.put(trainList[i].getName(), msFeature);
                ENfeatureList.put(trainList[i].getName(), enFeature);
                ZCfeatureList.put(trainList[i].getName(), zcFeature);
                MFfeatureList.put(trainList[i].getName(), mfMean);
                String line1 = trainList[i].getName() + "\t";
                String line2 = trainList[i].getName() + "\t";
                String line3 = trainList[i].getName() + "\t";
                String line4 = trainList[i].getName() + "\t";
                for (double f: msFeature){
                    line1 += f + "\t";
                }
                for (double f: enFeature){
                    line2 += f + "\t";
                }
                for (double f: zcFeature){
                    line3 += f + "\t";
                }
                for (double f: mfMean){
                    line4 += f + "\t";
                }
                fw1.append(line1+"\n");
                fw2.append(line2+"\n");
                fw3.append(line3+"\n");
                fw4.append(line4+"\n");
                System.out.println("@=========@" + i);
            }
            fw1.close();
            fw2.close();
            fw3.close();
            fw4.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {


        }catch (Exception e){
            e.printStackTrace();
        }


        return MSfeatureList;
    }

    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */  
    // need 4 more coefficient and 1 index 
    public ArrayList<String> resultList(String query){
        //HERE IS THE DISTANCE INDEX
    	int index = 0;
    	
    	WaveIO waveIO = new WaveIO();
        
        short[] inputSignal = waveIO.readWave(query);
        MagnitudeSpectrum ms = new MagnitudeSpectrum();
        Energy en = new Energy();
        FFTProcess fft = new FFTProcess();
        MFCC mf = new MFCC();
        ZeroCrossing zc = new ZeroCrossing();
        double[] msFeature = ms.getFeature(inputSignal);
        double[] enFeature = en.getFeature(inputSignal);
        mf.process(inputSignal);
        double[] mfMean = mf.getMeanFeature();
        double[] zcFeature = zc.getFeature(inputSignal);
        //System.out.println(msFeature.length);
        //System.out.println(mfMean.length);
        HashMap<String, Double> simList = new HashMap<String, Double>();

        /**
         * Example of calculating the distance via Cosine Similarity, modify it by yourself please.
         */
        Cosine cosine = new Cosine();
        CityBlock cb = new CityBlock();
        Euclidean eu = new Euclidean();
        /**
         * Load the offline file of features (the result of function 'trainFeatureList()'), modify it by yourself please;
         */
        HashMap<String, double[]> trainFeatureListMS = readFeature("data/feature/allFeatureMS.txt");
        HashMap<String, double[]> trainFeatureListEN = readFeature("data/feature/allFeatureEN.txt");
        HashMap<String, double[]> trainFeatureListZC = readFeature("data/feature/allFeatureZC.txt");
        HashMap<String, double[]> trainFeatureListMF = readFeature("data/feature/allFeatureMF.txt");
        //System.out.println(trainFeatureList.size() + "=====");
        index = choice;
        for (Map.Entry f: trainFeatureListMS.entrySet()){
        	//cosine.getDistance(mfMean, (double[]) f.getValue());
        	double ans, ans1, ans2, ans3, ans4;
        	String key = (String)f.getKey();
        	switch(index)
        	{
        		case 0://COSINE
        		{	
        			ans1 = coefficients[0] * cosine.getDistance(msFeature, (double[]) f.getValue());
        			ans2 = coefficients[1] * cosine.getDistance(enFeature, trainFeatureListEN.get(key));
        			ans3 = coefficients[2] * cosine.getDistance(mfMean, trainFeatureListMF.get(key));
        			ans4 = coefficients[3] * cosine.getDistance(zcFeature, trainFeatureListZC.get(key));
        			ans = ans1 + ans2 + ans3 + ans4;
        			simList.put(key, ans);
        			break;
        		}
        		case 1://CITY BLOCK
        		{
        			ans1 = coefficients[0] * cb.getDistance(msFeature, (double[]) f.getValue());
        			ans2 = coefficients[1] * cb.getDistance(enFeature, trainFeatureListEN.get(key));
        			ans3 = coefficients[2] * cb.getDistance(mfMean, trainFeatureListMF.get(key));
        			ans4 = coefficients[3] * cb.getDistance(zcFeature, trainFeatureListZC.get(key));
        			ans = ans1 + ans2 + ans3 + ans4;
        			simList.put(key, ans);
        			break;
        		}
        		case 2://EUCLIDEAN
        		{
        			ans1 = coefficients[0] * eu.getDistance(msFeature, (double[]) f.getValue());
        			ans2 = coefficients[1] * eu.getDistance(enFeature, trainFeatureListEN.get(key));
        			ans3 = coefficients[2] * eu.getDistance(mfMean, trainFeatureListMF.get(key));
        			ans4 = coefficients[3] * eu.getDistance(zcFeature, trainFeatureListZC.get(key));
        			ans = ans1 + ans2 + ans3 + ans4;
        			simList.put(key, ans);
        			break;
        		}
        	}
        }

        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(simList);

        String out = query + ":";
        for(int j = 0; j < result.size(); j++){
            out += "\t" + result.get(j);
        }

        System.out.println(out);
        return result;
    }

    /**
     * Load the offline file of features (the result of function 'trainFeatureList()');
     * @param featurePath the path of offline file including the features of training set.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    private HashMap<String, double[]> readFeature(String featurePath){
        HashMap<String, double[]> fList = new HashMap<>();
        try{
            FileReader fr = new FileReader(featurePath);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){

                String[] split = line.trim().split("\t");
                if (split.length < 2)
                    continue;
                double[] fs = new double[split.length - 1];
                for (int i = 1; i < split.length; i ++){
                    fs[i-1] = Double.valueOf(split[i]);
                }

                fList.put(split[0], fs);

                line = br.readLine();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return fList;
    }

    /*public static void main(String[] args){
        SearchDemo searchDemo = new SearchDemo();
        //Example of searching, selecting 'bus2.wav' as query;
        searchDemo.resultList("data/input/test/bus2.wav");
    }*/
}
