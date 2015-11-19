package core;

import core.exceptions.RecordStatus;
import core.exceptions.SQLError;
import core.managers.DBManager;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBTest {
    public static void main(String[] args) throws SQLError, IOException, RecordStatus {
        File q = new File("baza.db");
        q.delete();

        DBManager DB = new DBManager("baza.db");

        Scanner file = new Scanner(new File("D:\\Git\\dmd_project.sql\\2.txt"));
        String line = file.nextLine();


        //DB.cursor.execute(line);
    }
}
