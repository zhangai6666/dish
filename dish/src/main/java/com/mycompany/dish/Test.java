package dish;

import java.io.IOException;
import java.util.Arrays;

import com.google.gson.Gson;

public class Test {
	
	public h00 parse (String jsonString) {
    	Gson gson = new Gson();
    	return gson.fromJson(jsonString, h00.class);
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
