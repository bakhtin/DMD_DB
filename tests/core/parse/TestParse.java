package core.parse;

import core.sys.Page;
import core.sys.PageManager;
import org.junit.Test;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
public class TestParse {
    @Test
    public void testParse() throws Exception {
        PageManager p = new PageManager("huidb");
        Page q = p.readPage(5);
    }
}
