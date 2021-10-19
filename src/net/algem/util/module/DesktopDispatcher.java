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
import java.util.ArrayList;
import java.util.List;
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

    ServerSocket serverSocket;
    int port;
    
    List<ObjectInputStream> ins = new ArrayList<>();
    List<ObjectOutputStream> outs = new ArrayList<>();

    public DesktopDispatcher(int _port) {
        port = _port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            GemLogger.logException(e);
        }
    }
    
    public void start() {
        System.out.println("dispatcher started port:"+port);
        while (true) {
            try {
                Socket client = serverSocket.accept();
                if (client.getInetAddress().isLoopbackAddress())
                    break;
                ThreadDispatcher t = new ThreadDispatcher(client, this);
                t.start();
            } catch (IOException e) {
                GemLogger.logException(e);
            }
        }
        
    }
            
    public static void main(String[] argv) {
        int port = argv.length > 0 ? Integer.parseInt(argv[0]) : DesktopDispatcher.DEFAULT_SOCKET_PORT;

        DesktopDispatcher dispatcher = new DesktopDispatcher(port);
        dispatcher.start();

    }
}
