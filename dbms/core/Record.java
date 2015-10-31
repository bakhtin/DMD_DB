package core;

import java.nio.ByteBuffer;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 *         10/27/2015
 */
class Record {
    /** HEADER **/
    /**
     *  1 - tuple
     *  2 - internal b+tree node
     *  3 - leaf b+tree node
     */
    byte type;

    /**
     * Unique record id
     */
    long rowid;

    /**
     * Suppose we are trying to add a record with size = 100 to page with free space 50.
     * Page1: first 50 bytes: forward overflow: pointer to page 2
     * Page2: second 50 bytes: backward overflow: pointer to page 1
     * 0 means there is no overflow
     */
    int backward_overflow = 0;
    int forward_overfow   = 0;

    /** BODY **/
    /**
     * Real length of the record
     */
    int record_length;
    ByteBuffer payload;
}
