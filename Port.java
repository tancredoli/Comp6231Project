package Utility;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public enum Port {
    PORT_NUM;
    public static Logger LOGGER = null;
    public static FileHandler FH = null;
    public static String dir = "RM_Side_Log";
    public static String Serverdir = "Server_Side_Log";

    public final static String rmHost1 = "132.205.46.178";
    public final static String rmHost2 = "132.205.46.179";
    public final static String rmHost3 = "132.205.46.180";
    public final static String rmHost4 = "132.205.46.181";

    public final int rmPort1 = 6666;
    public final int rmPort2 = 6666;
    public final int rmPort3 = 6666;
    public final int rmPort4 = 6666;

    public final int rmPort1_crash = 1111;
    public final int rmPort2_crash = 1111;
    public final int rmPort3_crash = 1111;
    public final int rmPort4_crash = 1111;

    public static final String FRONTEND_IP = rmHost4;
    public static final String SEQUENCER_IP = rmHost1;

    public static final int FRONTEND_PORT = 5000;
    public static final int SEQUENCER_PORT = 5001;

}
