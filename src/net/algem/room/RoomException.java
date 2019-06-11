/*
 * @(#)RoomException.java	2.6.j 13/12/12
 *
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
 *
 */

package net.algem.room;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.j
 * @since 2.6.j 13/12/12
 */
public class RoomException extends Exception {

    /**
     * Creates a new instance of <code>RoomException</code> without detail message.
     */
    public RoomException() {
    }


    /**
     * Constructs an instance of <code>RoomException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RoomException(String msg) {
        super(msg);
    }
}
