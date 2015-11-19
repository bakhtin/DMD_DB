package core.descriptive;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         11/8/2015
 */
public class Relation {
    TreeMap<Integer, Row> rows = new TreeMap<>();

    public void addRow(int rowid, Row row) {
        this.rows.put(rowid, row);
    }

    public Row removeRow(int rowid) {
        return this.rows.remove(rowid);
    }

    public Row getRow(int rowid) {
        return this.rows.get(rowid);
    }

    public void forEach(BiConsumer<Integer, Row> b) {
        rows.forEach(b);
    }

    public int size() {
        return rows.size();
    }

    public Set<Map.Entry<Integer, Row>> entrySet() {
        return rows.entrySet();
    }
}
