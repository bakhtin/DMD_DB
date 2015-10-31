package core.sys.tabledescriptor;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class Table {
    String tbl_name;

    int attrN;
    Attribute[] attributes;

    long recordsTotal;
    int rootpage;

}
