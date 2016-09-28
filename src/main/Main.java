package main;

public class Main {

	public static void main(String[] args) {
		String sql = "cisdubccb r = ? and x = ? and v = ? and d <> ? and ii != ?";
		long n = sql.chars().filter(e -> e == '?').count();
		System.out.println("Number: " + n);
	}
}