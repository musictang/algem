/*
 * @(#)DesktopDispatcher.java	2.6.a 25/09/12
 *
 * Copyright (c) 1999-2003 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.util.module;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Service for sending and receiving events through the network.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class DesktopDispatcher {

  Vector<ObjectInputStream> ins = new Vector<ObjectInputStream>();
  Vector<ObjectOutputStream> outs = new Vector<ObjectOutputStream>();

  public static void main(String[] argv) {
    ServerSocket sock;

    try {
      sock = new ServerSocket(5433);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    DesktopDispatcher dispatcher = new DesktopDispatcher();
    System.out.println("dispatcher started");
    while (true) {
      try {
        Socket client = sock.accept();
        ThreadDispatcher t = new ThreadDispatcher(client, dispatcher);
        t.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
