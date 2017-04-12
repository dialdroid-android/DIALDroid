package com.yaogroup.collusion;

import java.io.File;
import java.util.ArrayList;

import com.yaogroup.db.DialDroidSQLConnection;

import edu.psu.cse.siis.ic3.Timers;
import edu.psu.cse.siis.ic3.db.SQLConnection;
import edu.psu.cse.siis.ic3.db.Table;
import edu.psu.cse.siis.ic3.manifest.ManifestPullParser;
import edu.psu.cse.siis.ic3.manifest.SHA256Calculator;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.TestApps.Test;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.tagkit.LineNumberTag;

public class AppAnalysis {

	private static int getIdForUnit(Unit unit, SootMethod method) {
		int id = 0;
		for (Unit currentUnit : method.getActiveBody().getUnits()) {
			if (currentUnit == unit) {
				return id;
			}
			++id;
		}

		return -1;
	}

	private static void getApkList(File apkFileOrDirectory, ArrayList<File> apkList) {

		if (!apkFileOrDirectory.isDirectory()) {

			if (apkFileOrDirectory.getName().endsWith(".apk") || apkFileOrDirectory.getName().endsWith(".APK")) {
				apkList.add(apkFileOrDirectory);
				return;
			}
		}

		File[] folderList = apkFileOrDirectory.listFiles();

		for (File apkFile : folderList) {
			getApkList(apkFile, apkList);

		}

	}

	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("Invalid program usage!! use: computeicc|appanalysis classpath dbname dbhostname [directory] [category]");
			return;
		}

		String dbName = args[2].trim();
		String dbHost = args[3].trim();
		
		Table.setDBHost(dbHost);
		DialDroidSQLConnection.init(dbName, "./cc.properties", null, 3306);
		
		if (args[0].compareToIgnoreCase("computeicc") == 0) {
			
			DialDroidSQLConnection.computeSensitiveChannels();
			return;
		}

		if (args[0].compareToIgnoreCase("appanalysis") != 0) {
			System.out.println("Invalid program usage!! use: computeicc|appanalysis classpath dbname dbhostname [directory] [category]");
			return;
		}

		File apkDirectory = new File(args[4]);
		String appCategory = args[5].trim();
		String classPath = args[1].trim();

		ArrayList<File> apkList = new ArrayList<File>();

		getApkList(apkDirectory, apkList);		
	
		for (File apkFile : apkList) {

			try {

				String appName = apkFile.getName().toLowerCase();
				appName = appName.substring(0, appName.indexOf(".apk"));
				System.out.println(appName);
				
				String shasum = SHA256Calculator.getSHA256(apkFile);

				if (SQLConnection.checkIfAppAnalyzed(shasum)) {
					DialDroidSQLConnection.saveAppCategory(appCategory, apkFile.getAbsolutePath());
					continue;
				}

				Timers.clear();
				Timers.v().analysisTimer.start();

				boolean InfoFlowComputationTimeOut = false;

				edu.psu.cse.siis.ic3.Main.main(new String[] { "-in", apkFile.getAbsolutePath(), "-cp",
						classPath, "-db", "./cc.properties", "-dbname",
						dbName, "-dbhost",dbHost });

				//DialDroidSQLConnection.saveAppCategory(appCategory, apkFile.getAbsolutePath());
				//Timers.v().saveTimeToDb();

				Timers.v().exitPathTimer.start();
				InfoflowResults results = soot.jimple.infoflow.android.TestApps.Test.runAnalysisForResults(
						new String[] { apkFile.getAbsolutePath(), classPath,
								"--aplength", "2", "--timeout", "450" });

				if (Test.InfoFlowComputationTimeOut) {
					InfoFlowComputationTimeOut = true;
					System.out.println(
							"Infoflow computation timeout with Context sensitive path builder. Running sourcesonly..");
					results = soot.jimple.infoflow.android.TestApps.Test.runAnalysisForResults(new String[] {
							apkFile.getAbsolutePath(), classPath,
							"--pathalgo", "SOURCESONLY", "--aplength", "1", "--NOPATHS", "--layoutmode", "none",
							"--aliasflowins", "--noarraysize", "--timeout", "450" });
				}

				DialDroidSQLConnection.insertSourceSinkCount(InfoflowResults.numSources, InfoflowResults.numSinks);
				InfoflowResults.reset();

				Timers.v().exitPathTimer.end();

				if (results != null) {

					for (ResultSinkInfo sink : results.getResults().keySet()) { //
						SootMethod method = results.getInfoflowCFG().getMethodOf(sink.getSink());

						String className = results.getInfoflowCFG().getMethodOf(sink.getSink()).getDeclaringClass()
								.toString();

						int instruction = getIdForUnit(sink.getSink(),
								results.getInfoflowCFG().getMethodOf(sink.getSink()));

						if (sink.getSink().hasTag("LineNumberTag")) {
							instruction = ((LineNumberTag) sink.getSink().getTag("LineNumberTag")).getLineNumber();
						}

						for (ResultSourceInfo source : results.getResults().get(sink)) {

							String leakSource = source.getSource().toString();
							String methodCalling = null;

							try {
								methodCalling = source.getSource().getInvokeExpr().getMethod().getName();
							} catch (Exception e) {

							}

							String leakSink = sink.getSink().toString();
							StringBuffer leakPath = new StringBuffer();

							if (source.getPath() != null) {
								for (Stmt stmt : source.getPath()) {
									leakPath.append(stmt.toString() + ",");
								}
							}

							DialDroidSQLConnection.insertDataLeak(className, method, instruction, sink.getSink(),
									leakSource, leakSink, leakPath.toString(), methodCalling);

						}
					}
				}

				Timers.v().entryPathTimer.start();
				results = soot.jimple.infoflow.android.TestApps.Test.runAnalysisForResults(
						new String[] { apkFile.getAbsolutePath(), classPath,
								"--iccentry", "--aplength", "1", "--timeout", "450" });

				if (Test.InfoFlowComputationTimeOut) {
					InfoFlowComputationTimeOut = true;
					System.out.println(
							"Infoflow computation timeout with Context sensitive path builder. Running sourcesonly..");
					results = soot.jimple.infoflow.android.TestApps.Test.runAnalysisForResults(new String[] {
							apkFile.getAbsolutePath(), classPath,
							"--iccentry", "--pathalgo", "SOURCESONLY", "--aplength", "1", "--nopaths", "--layoutmode",
							"none", "--aliasflowins", "--noarraysize",  "--nostatic", "--timeout",
							"450" });
				}

				Timers.v().entryPathTimer.end();

				if (results != null) {
					for (ResultSinkInfo sink : results.getResults().keySet()) { //

						String method = results.getInfoflowCFG().getMethodOf(sink.getSink()).getSignature();

						String className = results.getInfoflowCFG().getMethodOf(sink.getSink()).getDeclaringClass()
								.toString();

						int instruction = getIdForUnit(sink.getSink(),
								results.getInfoflowCFG().getMethodOf(sink.getSink()));

						if (sink.getSink().hasTag("LineNumberTag")) {
							instruction = ((LineNumberTag) sink.getSink().getTag("LineNumberTag")).getLineNumber();
						}

						for (ResultSourceInfo source : results.getResults().get(sink)) {

							String leakSource = source.getSource().toString();
							String leakSink = sink.getSink().toString();
							StringBuffer leakPath = new StringBuffer();

							if (source.getPath() != null) {
								for (Stmt stmt : source.getPath()) {
									leakPath.append(stmt.toString() + ",");
								}
							}

							DialDroidSQLConnection.insertFromICCDataLeak(className, method.toString(), instruction,
									sink.getSink(), leakSource, leakSink, leakPath.toString());

						}
					}
				}

				if (InfoFlowComputationTimeOut) {
					DialDroidSQLConnection.markAppTimeout();
				}

				Timers.v().analysisTimer.end();
				Timers.v().saveTimeToDb();

				System.out.println("Done:" + appName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
