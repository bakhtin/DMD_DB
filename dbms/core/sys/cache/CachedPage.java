package core.sys.cache;

import core.sys.Page;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/24/2015
 */
class CachedPage implements Comparable<Integer> {
    Integer used = 0;
    Page page;

    CachedPage(Page p) {
        page = p;
    }

    public void use() {
        used++;
    }

    public Integer getUsed() {
        return used;
    }

    public Integer getNumber() {
        return page.getNumber();
    }


    @Override
    public int compareTo(Integer o) {
        return used.compareTo(o);
    }

    public Page getPage() {
        return page;
    }
}
