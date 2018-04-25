package utils.campus;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Logger {

	private static PrintStream logger;
	private static long millis;

	private static int logLevel;

	public static boolean log;
	
	
	
	public static final int DEBUG = 1;
	public static final int VERBOSE_LOG = 2;
	public static final int LOG = 3;

	static {
		log = true;
		logLevel = 0;
		logger = System.err;
		millis = System.currentTimeMillis();
	}

	public static void setLogger(String logFileName) {
		try {
			logger = new PrintStream(logFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static long getUpTimeInMillis() {
		return System.currentTimeMillis() - millis;
	}

	private static void printRunningTime() {
		logger.printf("[%.3f]\t" , (((float) getUpTimeInMillis()) / 1000.0f));
	}

	// Given the verbose level,
	public static void doLog(String toLog, int verboseLevel) {
		if (log && verboseLevel > logLevel) {
			printRunningTime();
			logger.println(toLog);
		}
	}

	public static void log(String toLog) {
		doLog(toLog, LOG);
	}
	
	public static void verboseLog(String toLog) {
		doLog(toLog, VERBOSE_LOG);
	}
	
	public static void debug(String toLog) {
		doLog(toLog, DEBUG);
	}
}
