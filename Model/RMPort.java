package Model;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public enum RMPort {
	RM_PORT;
	public static Logger LOGGER = null;
	public static FileHandler FH = null;
	public static String dir = "RM_Side_Log/";
	
	public final String rmHost1 = "132.205.46.182";
	public final String rmHost2 = "132.205.46.183";
	public final String rmHost3 = "132.205.46.184";
	public final String rmHost4 = "132.205.46.185";
	
	public final int rmPort1= 6666;
	public final int rmPort2= 6666;
	public final int rmPort3= 6666;
	public final int rmPort4= 6666;
	
	public final int rmPort1_crash = 1111;
	public final int rmPort2_crash = 1111;
	public final int rmPort3_crash = 1111;
	public final int rmPort4_crash = 1111;

}
