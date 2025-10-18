package com.finops.batch;

import com.finops.util.DateUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class Databackup extends DailySummaryReport{

    public void backup(){

        String backupDirectory = "D:\\MYSQL-DBBACKUP\\";

        String timeStamp = DateUtil.getSystemDate()+"_"+System.currentTimeMillis();
        String backupFileName = String.format("%s_%s.sql", database, timeStamp);

        //backupFileName = "shik.sql";

        String command = String.format("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump -u %s -p%s %s > %s/%s",
                userName, newword, database, backupDirectory, backupFileName);

        try {
            Process process = Runtime.getRuntime().exec(command);

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Database backup successful.");
            } else {
                System.err.println("Error backing up database.");
            }

        } catch (InterruptedException | IOException e) {
            System.err.println("Error executing backup command: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Databackup db = new Databackup();
        db.setup();
        db.backup();

    }
}
