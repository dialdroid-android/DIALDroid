package com.yaogroup.collusion;

import java.io.File;
import java.sql.SQLException;

import edu.psu.cse.siis.ic3.db.SQLConnection;
import edu.psu.cse.siis.ic3.manifest.ManifestPullParser;

public class AppNameExtractor {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    SQLConnection.init("VirusShare", "./cc.properties", null, 3306);

    File vsDirectory = new File("/home/bosu/AndroidApps/VirusShare/");

    for (File apk : vsDirectory.listFiles()) {
      ManifestPullParser detailedManifest = new ManifestPullParser();
      try {
        detailedManifest.loadManifestFile(apk.getAbsolutePath());

        // try {
        // SQLConnection.insertVirusSharePackageName(detailedManifest.getPackageName(),
        // apk.getName(), detailedManifest.getVersion());
        // } catch (SQLException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

    }

    // System.out.println(detailedManifest.getPackageName());

  }

}
