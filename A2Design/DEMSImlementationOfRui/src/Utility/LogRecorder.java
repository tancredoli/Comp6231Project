package Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:33 PM 2019-07-06
 * @Modified by:
 */
public class LogRecorder {
    private File serverDir;
    private SimpleDateFormat format;
    private File outputFile;

    public LogRecorder(int type) throws IOException {
        this.serverDir = new File(System.getProperty("user.dir"));
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String filename = new String();
        if (type == 1) {
            filename = serverDir.toString() + "\\src\\Log\\Server" +
                    "\\MTLLOG.txt";

        } else if (type == 2) {
            filename = serverDir.toString() + "\\src\\Log\\Server" +
                    "\\OTWLOG.txt";

        } else if (type == 3) {
            filename = serverDir.toString() + "\\src\\Log\\Server" +
                    "\\TORLOG.txt";

        } else if (type == 4) {
            filename = serverDir.toString() + "\\src\\Log\\Client" +
                    "\\Client.txt";

        }
        this.outputFile = new File(filename);
        if (!this.outputFile.exists()) {
            if (!this.outputFile.getParentFile().exists())
                this.outputFile.getParentFile().mkdirs();
            this.outputFile.createNewFile();

        }
    }

    public String paraConstructor(
            String managerID, String customerID,
            int eventType, String eventID,
            int bookingCapacity) {

        String para = "";
        if (!managerID.isEmpty())
            para += " managerID: " + managerID;
        if (!customerID.isEmpty())
            para += " customerID: " + customerID;
        if (eventType == 1)
            para += " eventType: Conference";
        else if (eventType == 2)
            para += " eventType: Seminars";
        else if (eventType == 3)
            para += " eventType: Trade Shows";
        if (!eventID.isEmpty())
            para += " eventID: " + eventID;
        if (bookingCapacity != 0)
            para += " bookingCapacity: " + bookingCapacity;

        return para;

    }

    public synchronized void writeLog(
            String parameter, String action, boolean comp,
            String response) throws IOException {
        String date = format.format(new Date());
        String actionExecu;
        if (comp)
            actionExecu = "Completed";
        else
            actionExecu = "Failed";
        FileWriter fw = new FileWriter(outputFile.getAbsolutePath(), true);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("Date: " + date + " | Parameter: " + parameter + " | " +
                "Action:" +
                " " + action);
        pw.println("Action execution: " + actionExecu + " | Response: " + response);
        pw.close();
    }
}
