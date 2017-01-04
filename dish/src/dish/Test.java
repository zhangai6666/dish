package dish;

import java.io.IOException;
import java.util.Arrays;

import com.google.gson.Gson;

public class Test {
	
	public h00 parse (String jsonString) {
    	Gson gson = new Gson();
    	return gson.fromJson(jsonString, h00.class);
    }
	
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		String file = "test.txt";
		JsonParser parser = new JsonParser();
		StringBuilder jsonString = new StringBuilder();
		parser.readFromFile(file, jsonString);
		System.out.println(jsonString);
		Test test = new Test();
		h00 tmp = test.parse(jsonString.toString());
		System.out.println(Arrays.toString(tmp.main[0].sA));
		System.out.println(Arrays.toString(tmp.main[0].iA));
		System.out.println(Arrays.toString(tmp.main[0].sC));
	}

}

class h00 {
	h01[] main;
}

class h01 {
	String[] sA;
	int[] iA;
	String[] sC;
}
