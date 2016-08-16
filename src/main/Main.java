package main;

import java.util.Arrays;
import java.util.HashMap;
import tuple.Tuple;

public class Main {

	public static void main(String[] args) {
		Tuple tuple = fromList();
		Tuple tupleFromMap = fromMap();
		
		System.out.println("Size: " + tuple.cardinality());
		System.out.println("Field 'name'? " + tuple.get("name"));
		System.out.println("Field 'name': " + tuple.set("name", "Henry"));
	}
	
	private static Tuple fromList() {
		return new Tuple(Arrays.asList("name", "lastName"));
	}
	
	private static Tuple fromMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("name", "Juanga");
		map.put("lastName", "Jones");
		return new Tuple(map);
	}
}