/*
 * @(#)DesktopDispatcher.java	2.15.11 09/10/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import net.algem.util.GemLogger;

/**
 * Service used to sending and receiving events through the local network.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.11
 */
public class DesktopDispatcher {

  static final int DEFAULT_SOCKET_PORT = 5433;

  Vector<ObjectInputStream> ins = new Vector<ObjectInputStream>();
  Vector<ObjectOutputStream> outs = new Vector<ObjectOutputStream>();

  public static void main(String[] argv) {
    ServerSocket serverSocket;

    try {
      serverSocket = new ServerSocket(DEFAULT_SOCKET_PORT);
    } catch (IOException e) {
      GemLogger.logException(e);
      return;
    }

    DesktopDispatcher dispatcher = new DesktopDispatcher();
    System.out.println("dispatcher started");
    while (true) {
      try {
        Socket client = serverSocket.accept();
        ThreadDispatcher t = new ThreadDispatcher(client, dispatcher);
        t.start();
      } catch (IOException e) {
        GemLogger.logException(e);
      }
    }
  }
  }
