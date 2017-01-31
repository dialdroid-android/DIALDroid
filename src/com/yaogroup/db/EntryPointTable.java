package com.yaogroup.db;
import edu.psu.cse.siis.ic3.db.Table;

import java.sql.SQLException;

public class EntryPointTable extends Table {
  private static final String INSERT =
      "INSERT INTO EntryPoints " + "(class_id, method, instruction, statement) VALUES (?, ?, ?, ?)";

  private static final String FIND =
      "SELECT id FROM EntryPoints WHERE class_id = ? AND method = ? AND instruction = ? ";

  public int insert(int classId, String method, int instruction, String unit) throws SQLException {
    int id = find(classId, method, instruction);
    if (id != NOT_FOUND) {
      return id;
    }
    if (insertStatement == null || insertStatement.isClosed()) {
      insertStatement = getConnection().prepareStatement(INSERT);
    }
    insertStatement.setInt(1, classId);
    if (method.length() > 512) {
      method = method.substring(0, 512);
    }
    insertStatement.setString(2, method);
    insertStatement.setInt(3, instruction);
    insertStatement.setString(4, unit);

    if (insertStatement.executeUpdate() == 0) {
      return NOT_FOUND;
    }
    return findAutoIncrement();
  }

  public int find(int classId, String method, int instruction) throws SQLException {
    if (findStatement == null || findStatement.isClosed()) {
      findStatement = getConnection().prepareStatement(FIND);
    }
    findStatement.setInt(1, classId);
    findStatement.setString(2, method);
    findStatement.setInt(3, instruction);

    return processIntFindQuery(findStatement);
  }
}
