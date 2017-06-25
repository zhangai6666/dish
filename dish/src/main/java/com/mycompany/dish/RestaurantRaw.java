package com.mycompany.dish;

public class RestaurantRaw {
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


