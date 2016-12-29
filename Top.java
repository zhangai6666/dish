package dish;

import com.google.gson.Gson;

public class Top {
	Response response;
}

class Response {
	Group[] groups;
}

class Group {
	Item[] items;
}

class Item {
	Restaurant venue;
}


