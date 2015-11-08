package core.sys.descriptive;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/8/2015
 */
public class Relation {
    Map<Integer, Row> rows = new TreeMap<>();

    public void addRow(int rowid, Row row) {
        this.rows.put(rowid, row);
    }

    public Row removeRow(int rowid) {
        return this.rows.remove(rowid);
    }

    public Row getRow(int rowid) {
        return this.rows.get(rowid);
    }
}
