/*
 * @(#)ThreadDispatcher.java	2.7.d 24/01/13
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import net.algem.util.event.GemRemoteEvent;

/**
 * Thread for listening events sent through the network.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.d
 * @since 1.0a 15/06/2003
 */
public class ThreadDispatcher
        extends Thread {

  private DesktopDispatcher dispatcher;
  private ObjectOutputStream out;
  private ObjectInputStream in;

  public ThreadDispatcher(Socket s, DesktopDispatcher d)
          throws IOException {
    dispatcher = d;
    out = new ObjectOutputStream(s.getOutputStream());
    in = new ObjectInputStream(s.getInputStream());

    synchronized (dispatcher) {
      dispatcher.ins.addElement(in);
      dispatcher.outs.addElement(out);
    }
    System.out.println("Connexion client:" + s);
  }

  @Override
  public void run() {
    for (;;) {
      GemRemoteEvent evt = null;
      try {
        System.out.println("Attente evt");
        evt = (GemRemoteEvent) in.readObject();
      } catch (Exception e) {
        evt = null;
      }
      if (evt == null) {
        System.out.println("fermeture flux");
        synchronized (dispatcher) {
          int idx = dispatcher.ins.indexOf(in);
          dispatcher.ins.removeElementAt(idx);
          dispatcher.outs.removeElementAt(idx);
        }
        try {
          in.close();
        } catch (Exception ignore) {
        }
        try {
          out.close();
        } catch (Exception ignore) {
        }
        return;
      } // fin if (evt == null) {

      System.out.println("evt:" + evt);
      synchronized (dispatcher) {
        for (int i = 0; i < dispatcher.outs.size(); i++) {
          ObjectOutputStream os = (ObjectOutputStream) dispatcher.outs.elementAt(i);
          if (os.equals(out)) {
            continue;
          }
          //System.out.println("forward evt "+i);
          try {
            os.writeObject(evt);
          } catch (Exception ignore) {
          }
        }
      }
    } // fin for
  }
}
