package user;

import javax.xml.transform.Result;
import java.sql.*;

public class UserDaoSqlite implements UserDao {
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new Error(e);
		}
	}
	
	protected Connection conn;

	public UserDaoSqlite( String userFilePath ) throws SQLException {
		String jdbcUrl = "jdbc:sqlite:"+userFilePath;
		this.conn = null;
		try{
			conn = DriverManager.getConnection(jdbcUrl);
			System.out.println("Connection réussite");
		}catch (RuntimeException e){
			throw new RuntimeException("Erreur de connexion à la base de données SQLite");
		}
	}
	
	@Override
	public void add(User user, String password) {
		String sql = "INSERT INTO users (firstname, lastname, email, password) VALUES (?,?,?,?)";
		try (PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setString(1, user.getFirstname());
			pst.setString(2, user.getLastname());
			pst.setString(3, user.getEmail());
			pst.setString(4, user.getPassword());
			boolean success = pst.execute();
			if (!success){
				throw new SQLException();
			}
		}catch (SQLException e){
			System.out.println("erreur add user");
		}
	}
	
	@Override
	public void update(User user, String password) {
		String sql = "UPDATE users SET firstname=?, lastname=?, email=?, password=? WHERE id=?";
		try (PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setString(1, user.getFirstname());
			pst.setString(2, user.getLastname());
			pst.setString(3, user.getEmail());
			pst.setString(4, password);
			pst.setLong(5, user.getId());
			pst.executeUpdate();
		}catch (SQLException e){
			System.out.println("erreur update user");
		}
	}
	
	@Override
	public User find(long id) {
		// TODO : get user data by its ID and map to User object
		String sql = "SELECT * FROM users WHERE id=?";
		User user = null;
		try (PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setLong(1, id);
			ResultSet rs = pst.executeQuery();
			Long rsId = rs.getLong("id");
			String firstname = rs.getString("firstname");
			String lastname = rs.getString("lastname");
			String email = rs.getString("email");
			String pwd = rs.getString("password");
			user = new User(rsId, firstname, lastname, email, pwd);
		}catch (SQLException e){
			System.out.println("erreur findById user");
		}
		return user;
	}
	
	@Override
	public User findByEmail(String email) {
		// TODO : get user data by its ID and map to User object
		String sql = "SELECT * FROM users WHERE email=?";
		User user = null;
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setString(1, email);
			ResultSet rs = pst.executeQuery();
			Long rsId = rs.getLong("id");
			String firstname = rs.getString("firstname");
			String lastname = rs.getString("lastname");
			String rsEmail = rs.getString("email");
			String pwd = rs.getString("password");
			user = new User(rsId, firstname, lastname, email, pwd);
		}catch (SQLException e){
			System.out.println("erreur findByEmail user");
		}
		return user;
	}
	
	@Override
	public long checkPassword(String email, String password) {
		// TODO : get user id that match, return -1 if none
		String sql = "SELECT id FROM users WHERE email=? AND password=?";
		long result = -1;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, email);
			pst.setString(2, password);
			ResultSet rs = pst.executeQuery();
			if (rs != null){
				result = rs.getLong("id");
			}
		}catch (SQLException e){
			System.out.println("erreur findByEmail user");
		}
		return result;
	}
	
	@Override
	public void delete(long id) {
		// TODO : delete a user that match this ID
		String sql = "DELETE FROM users WHERE id=?";
		try{
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, id);
			int rs = pst.executeUpdate();
			if (rs < 1 ){
				throw new SQLException();
			}
		}catch(SQLException e){
			System.out.println("Erreur delete user");
		}
	}
	
	@Override
	public long exists(String email) {
		// TODO : check if user with that mail exists
		long result = -1;
		String sql = "SELECT * FROM users WHERE email=?";
		try (PreparedStatement pst = conn.prepareStatement(sql)){
			pst.setString(1, email);
			ResultSet rs = pst.executeQuery();
			if (rs != null){
				result = rs.getLong("id");
			}
		}catch (SQLException e){
			System.out.println("erreur exists user");
		}
		return result;
	}
	
	

}
