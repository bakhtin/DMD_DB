package core.parse;

import core.sys.Page;
import core.sys.Pager;
import org.junit.Test;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class TestParse {
    @Test
    public void testParse() throws Exception {
        Pager p = new Pager("huidb");
        Page q = p.readPage(5);
    }
}
