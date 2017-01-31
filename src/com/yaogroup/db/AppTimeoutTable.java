package com.yaogroup.db;

import java.sql.SQLException;
import edu.psu.cse.siis.ic3.db.TwoIntTable;

public class AppTimeoutTable extends TwoIntTable {

  AppTimeoutTable() {
    super("AppTimeout", "AppID", "Timeout");
    // TODO Auto-generated constructor stub
  }

  public int insert(int appId) throws SQLException {
    return super.insert(appId, 1);
  }

  public int forceInsert(int appId) throws SQLException {
    return super.forceInsert(appId, 1);
  }

  public int find(int appId) throws SQLException {
    return super.find(appId, 1);
  }

}
