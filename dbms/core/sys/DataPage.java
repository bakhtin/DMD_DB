package core.sys;

import core.sys.table.Attribute;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 * @date 10/23/2015
 */

public class DataPage extends Page{
    long rowid;
    Attribute[] attrs;

    public DataPage(int n) {
        super(n);
    }
}
