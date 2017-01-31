package com.yaogroup.collusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.android.data.ICCExitPointSourceSink;
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser;

public class PermissionAnalysis {

  private static PermissionMethodParser pmp;

  public static ArrayList<String> getPermissionList(String methodName) {

    ArrayList<String> permissions = new ArrayList<String>();

    if (methodName.contains("android.view.View") && methodName.contains("findViewById(int)")) {
      permissions.add("SENSITIVE_UI_DATA");
      return permissions;
    }

    try {

      if (pmp == null) {
        pmp = PermissionMethodParser.fromStringList(ICCExitPointSourceSink.getList());
      }

      Set<soot.jimple.infoflow.source.data.SourceSinkDefinition> sourceList = pmp.getSources();

      for (soot.jimple.infoflow.source.data.SourceSinkDefinition source : sourceList) {

        // System.out.println(source.toString());

        AndroidMethod method = (AndroidMethod) source.getMethod();

        if (methodName.contains(method.getMethodName())
            && methodName.contains(method.getClassName())
            && methodName.contains(method.getReturnType())) {
          for (String permision : method.getPermissions()) {
            permissions.add(permision);
          }
          break;
        }

      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
    }

    return permissions;
  }

  public static void main(String args[]) {
    String[] strs = {
        "$r3 = virtualinvoke $r2.<android.app.ActivityManager: java.util.List getRunningTasks(int)>(2147483647)",
        "$r5 = virtualinvoke $r4.<android.content.pm.PackageManager: java.util.List getInstalledPackages(int)>(0)",
        "r5 = virtualinvoke $r4.<android.os.Handler: android.os.Message obtainMessage(int,java.lang.Object)>(i0, r2)",
        "$d0 = virtualinvoke $r7.<android.location.Location: double getLatitude()>()",
        "$r6 = virtualinvoke $r5.<java.util.Locale: java.lang.String getCountry()>()",
        "$r1 = virtualinvoke $r0.<com.illuminati.peoplesminister.Login: android.view.View findViewById(int)>(2131034226)",
        "$r5 = virtualinvoke $r4.<android.telephony.TelephonyManager: java.lang.String getSubscriberId()>()" };

    for (String str : strs) {
      System.out.println(getPermissionList(str));
    }
  }

}
