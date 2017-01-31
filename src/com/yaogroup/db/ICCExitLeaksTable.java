package com.yaogroup.db;

import java.sql.SQLException;
import edu.psu.cse.siis.ic3.db.Table;

public class ICCExitLeaksTable extends Table {
  private static final String INSERT = "INSERT INTO ICCExitLeaks "
      + "(exit_point_id, leak_source, leak_sink, leak_path,method) VALUES (?, ?, ?, ?,?)";

  /*
   * private static final String FIND =
   * "SELECT id FROM ExitPoints WHERE class_id = ? AND method = ? AND instruction = ? " +
   * "AND exit_kind = ?";
   */

  public int insert(int exitPoint, String leakSource, String leakSink, String leakPath,
      String method) throws SQLException {
    // int id = find(classId, method, instruction, exit_kind);
    // if (id != NOT_FOUND) {
    // return id;
    // }
    if (insertStatement == null || insertStatement.isClosed()) {
      insertStatement = getConnection().prepareStatement(INSERT);
    }
    insertStatement.setInt(1, exitPoint);
    if (leakSource.length() > 512) {
      leakSource = leakSource.substring(0, 512);
    }
    insertStatement.setString(2, leakSource);
    insertStatement.setString(3, leakSink);
    insertStatement.setString(4, leakPath);
    insertStatement.setString(5, method);

    if (insertStatement.executeUpdate() == 0) {
      return NOT_FOUND;
    }
    return findAutoIncrement();
  }

  /*
   * public int find(int classId, String method, int instruction, String exit_kind) throws
   * SQLException { if (findStatement == null || findStatement.isClosed()) { findStatement =
   * getConnection().prepareStatement(FIND); } findStatement.setInt(1, classId);
   * findStatement.setString(2, method); findStatement.setInt(3, instruction);
   * findStatement.setString(4, exit_kind); return processIntFindQuery(findStatement); }
   */
}
