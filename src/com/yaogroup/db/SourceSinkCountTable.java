package com.yaogroup.db;
import edu.psu.cse.siis.ic3.db.Table;

import java.sql.SQLException;

public class SourceSinkCountTable extends Table {
  private static final String INSERT =
      "INSERT INTO SourceSinkCount (`AppID`, `num_sources`, `num_sinks`) VALUES (?, ?, ?);";
  private static final String FIND = "SELECT id FROM SourceSinkCount WHERE AppID = ?";
  private static final String DELETE = "DELETE FROM SourceSinkCount WHERE AppID = ?";

  public int insert(int app, int numSource, int numSink) throws SQLException {
    delete(app);

    insertStatement = getConnection().prepareStatement(INSERT);

    insertStatement.setInt(1, app);
    insertStatement.setInt(2, numSource);
    insertStatement.setInt(3, numSink);

    if (insertStatement.executeUpdate() == 0) {
      return NOT_FOUND;
    }

    return findAutoIncrement();
  }

  public void delete(int app) throws SQLException {
    findStatement = getConnection().prepareStatement(DELETE);
    findStatement.setInt(1, app);

    findStatement.executeUpdate();
    findStatement.close();

  }
}
