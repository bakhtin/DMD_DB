package core;

import core.exceptions.RecordStatus;
import core.exceptions.SQLError;
import core.managers.DBManager;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/4/2015
 */
public class DBTest {
    public static void main(String[] args) throws SQLError, Exception, RecordStatus {
        //Scanner file = new Scanner(new File("D:\\Git\\dmd_project.sql\\2.txt"));

        String query = "SELECT p.id, title, issn, isbn, doi, pubdate, pages, volume, abstract, url, pub_number, i_name.name, i_type.type, aff.name, pshr.name\n" +
                "FROM publication p, issue_name AS i_name, issue_type AS i_type, affiliation AS aff, publisher AS pshr\n" +
                "WHERE \n" +
                "p.issue_name_id=i_name.id\n" +
                "and p.issue_type_id=i_type.id\n" +
                "and p.affiliation_id=aff.id\n" +
                "and p.id = 1050\n" +
                "and p.publisher_id=pshr.id";

        query = "select id from publication where title=\"new_title\";";

        String response = DBManager.processQuery(query);

        System.out.println(response);
        //DB.cursor.execute(line);
    }
}
