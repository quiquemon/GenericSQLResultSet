package main;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import model.dao.Dao;

public class Main {

	public static void main(String[] args) {
		Dao dao = Dao.createStandardDao();
		printUsers(dao);
		insertTeams(dao);
		insertUserTeams(dao);
		insertPayments(dao);
		resetAutoIncrement(dao);
		printJoin(dao);
	}
	
	private static void printUsers(Dao dao) {
		try {
			dao.connect();
			List<Map<String, Object>> rs = dao.query("SELECT * FROM User");
			rs.forEach(e -> {
				e.forEach((k, v) -> {
					System.out.println(k + ": " + v + " | ");
				});
				System.out.println("");
			});
		} catch (RuntimeException e) {
			System.err.println("Ocurrió un error: " + e.getMessage());
		} finally {
			dao.disconnect();
		}
	}
	
	private static void insertTeams(Dao dao) {
		try {
			dao.connect();
			dao.beginTransaction();
			dao.execute("INSERT INTO Team(name, creationDate, code) VALUES (?, ?, ?)", new Object[] {
				"The Best Team Ever",
				"2016-09-29",
				"uihdugasiguag"
			});
			dao.execute("INSERT INTO Team(name, creationDate, code) VALUES (?, ?, ?)", new Object[] {
				"Rudo's Team",
				"1999-12-12",
				"abcdef0123456789"
			});
			dao.commit();
		} catch (RuntimeException e) {
			dao.rollback();
			System.err.println("Ocurrió un error: " + e.getMessage());
		} finally {
			dao.disconnect();
		}
	}
	
	private static void insertUserTeams(Dao dao) {
		try {
			dao.connect();
			dao.beginTransaction();
			dao.execute("INSERT INTO UserTeam VALUES (?, ?, ?, ?)", new Object[] {
				1, 1, null, null
			});
			dao.execute("INSERT INTO UserTeam VALUES (?, ?, ?, ?)", 2, 1, 32, "My code");
			dao.execute("INSERT INTO UserTeam VALUES (" + 3 + ", " + 2 + ", " + 56 + ", 'My second code')");
			dao.execute("INSERT INTO UserTeam VALUES (?, ?, ?, ?)", new Object[] {
				4, 2, 45, null
			});
			dao.commit();
		} catch (RuntimeException e) {
			dao.rollback();
			System.err.println("Ocurrió un error: " + e.getMessage());
		} finally {
			dao.disconnect();
		}
	}
	
	private static void insertPayments(Dao dao) {
		try {
			dao.connect();
			dao.beginTransaction();
			dao.execute("INSERT INTO Payment (status, transaction, amount, realized, idTeam) VALUES (?, ?, ?, ?, ?)",
				new Object[] { true, "PFCE875667", new BigDecimal(876.981), "2016-09-09", 1 }
			);
			dao.execute("INSERT INTO Payment (status, transaction, amount, realized, idTeam) VALUES (?, ?, ?, ?, ?)",
				new Object[] { false, "PFCE875667", new BigDecimal(9000.2341), "2015-08-08", 2 }
			);
			dao.commit();
		} catch (RuntimeException e) {
			dao.rollback();
			System.err.println("Ocurrió un error: " + e.getMessage());
		} finally {
			dao.disconnect();
		}
	}
	
	private static void resetAutoIncrement(Dao dao) {
		try {
			dao.connect();
			dao.beginTransaction();
			dao.execute("ALTER TABLE Payment AUTO_INCREMENT = 3");
			dao.commit();
		} catch (RuntimeException e) {
			dao.rollback();
			System.err.println("Ocurrió un error: " + e.getMessage());
		} finally {
			dao.disconnect();
		}
	}
	
	private static void printJoin(Dao dao) {
		String sql = "SELECT CONCAT(u.name, ' ', u.lastname) AS User, ut.runnerNumber AS Runner, t.name AS Team,"
			+ " p.transaction AS TransactID, p.amount AS Amount FROM User u, UserTeam ut, Team t, Payment p"
			+ " WHERE u.idUser = ut.idUser AND ut.idTeam = t.idTeam AND t.idTeam = p.idTeam AND t.idTeam = ? LIMIT 1";
		
		try {
			dao.connect();
			List<Map<String, Object>> rs = dao.query(sql, 2);
			rs.forEach(e -> {
				System.out.println("User: " + e.get("User") + " | Runner: " + e.get("Runner") + " | Team: " + e.get("Team")
					+ " | TransactID: " + e.get("TransactID") + " | Amount: " + e.get("Amount"));
				System.out.println("");
			});
		} catch (RuntimeException e) {
			System.err.println("Ocurrió un error: " + e.getMessage());
		} finally {
			dao.disconnect();
		}
	}
}