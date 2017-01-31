package com.yaogroup.db;

import java.sql.SQLException;
import edu.psu.cse.siis.ic3.db.Table;

public class AppCategoryTable extends Table {
  private static final String INSERT =
      "INSERT INTO AppCategories (AppID, CategoryID, path) VALUES (?, ?,?)";
  private static final String FIND =
      "SELECT id FROM AppCategories WHERE AppID = ? AND CategoryID = ?";

  public int insert(int appId, int category, String path) throws SQLException {
    if (insertStatement == null || insertStatement.isClosed()) {
      insertStatement = getConnection().prepareStatement(INSERT);
    }

    insertStatement.setInt(1, appId);
    insertStatement.setInt(2, category);
    insertStatement.setString(3, path);

    if (insertStatement.executeUpdate() == 0) {
      return NOT_FOUND;
    }

    return findAutoIncrement();
  }

  public int find(int appId, int category) throws SQLException {
    findStatement = getConnection().prepareStatement(FIND);
    findStatement.setInt(1, appId);
    findStatement.setInt(2, category);
    return processIntFindQuery(findStatement);
  }

}
