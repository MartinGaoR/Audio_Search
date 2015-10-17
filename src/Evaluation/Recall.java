package Evaluation;

import java.util.ArrayList;

public class Recall {
    private static final int k = 125;
    
    public double result(ArrayList<String> names, String query){
    	String word = query.split("\\.")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0];
    	String result="";
		double ans = 0;
		double count = 0;
		for(int i=0; i<names.size(); i++){
			String temp = names.get(i);
			result = temp.split("\\.")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0];
			if(word.equals(result))
				count++;
		}
		ans = count/k;
		System.out.println(ans);
		return ans;
	}
}
