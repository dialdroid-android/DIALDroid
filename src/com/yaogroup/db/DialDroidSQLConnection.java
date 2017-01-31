package com.yaogroup.db;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


import com.yaogroup.collusion.PermissionAnalysis;


import edu.psu.cse.siis.ic3.db.*;
import soot.SootMethod;
import soot.Unit;

public class DialDroidSQLConnection extends SQLConnection {
  
  protected static ICCExitLeaksTable dataLeaksTable = new ICCExitLeaksTable();
  protected static EntryPointTable entryPointTable = new EntryPointTable();
  protected static ICCEntryLeaksTable fromICCEntryDataLeaksTable =
      new ICCEntryLeaksTable();
 
  protected static AppTimeoutTable appTimeoutTable = new AppTimeoutTable();
  protected static PermissionLeakTable permissionLeakTable = new PermissionLeakTable();
  protected static SourceSinkCountTable sourceSinkCountTable = new SourceSinkCountTable();

  
  
  protected static int insertExitPoint(String className, String method, int instruction,
      String exit_kind, Integer missingIntentFilters, Unit unit) throws SQLException {
    int classId = insertClass(className);
    return exitPointTable.insert(classId, method, instruction, exit_kind, missingIntentFilters,
        unit.toString());
  }

  public static void insertDataLeak(String className, SootMethod method, int instruction, Unit unit,
      String source, String sink, String path, String methodCalling) throws SQLException {

    String exit_kind = "a";

    if (sink.contains("startService") || sink.contains("bindService")) {
      exit_kind = "s";
    } else if (sink.contains("sendBroadcast") || sink.contains("sendOrderedBroadcast")
        || sink.contains("sendStickyBroadcast") || sink.contains("sendStickyOrderedBroadcast")) {
      exit_kind = "r";
    } else if (sink.contains("ContentResolver")||sink.contains("android.database")) {
      exit_kind = "p";
    }

    int exitPointID =
        insertExitPoint(className, method.getSignature(), instruction, exit_kind, 0, unit);

    
      ArrayList<String> permissions = PermissionAnalysis.getPermissionList(source);
      
      int leakID = dataLeaksTable.insert(exitPointID, source, sink, path, methodCalling);
      
      for (String permission : permissions) { int permissionId =
      permissionStringTable.insert(permission); permissionLeakTable.insert(leakID, permissionId); }
     

  }

  public static void insertFromICCDataLeak(String className, String method, int instruction,
      Unit unit, String source, String sink, String path) throws SQLException {
    int classId = insertClass(className);
    int entryPointID = entryPointTable.insert(classId, method, instruction, source);
    fromICCEntryDataLeaksTable.insert(entryPointID, source, sink, path);

  }



  

  public static void markAppTimeout() throws SQLException {
    appTimeoutTable.insert(appId);
  }

  public static void insertSourceSinkCount(int numSources, int numSinks) throws SQLException {
    sourceSinkCountTable.insert(appId, numSources, numSinks);
  }

	public static void computeSensitiveChannels() {
	
		Connection connection=Table.getConnection();
		String query="{CALL calculatedataleak()}";
		try {
			CallableStatement stmt=connection.prepareCall(query);
			stmt.executeQuery();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	
	}


}
