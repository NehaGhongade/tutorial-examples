package com.example.ejb;

import javax.ejb.Stateless;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;

@Stateless
public class LoginServiceBean implements LoginService {

  @Resource(lookup = "jdbc/DB2DataSource")
  private DataSource dataSource;

  @Override
  public boolean authenticate(String username, String password) {
    try (Connection conn = dataSource.getConnection()) {
      String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        stmt.setString(2, password);
        try (ResultSet rs = stmt.executeQuery()) {
          return rs.next() && rs.getInt(1) > 0;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }
}
