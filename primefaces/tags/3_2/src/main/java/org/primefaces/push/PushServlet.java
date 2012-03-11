package org.primefaces.push;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public final class PushServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;
    
    private final Map<String,Set<PrimeWebSocket>> connectedClients = new ConcurrentHashMap<String, Set<PrimeWebSocket>>();

    @Override
    public void init() throws ServletException {
        super.init();
        String[] channels = this.getInitParameter("channels").split(",");
        
        for(String channel : channels) {
            this.connectedClients.put(channel, new CopyOnWriteArraySet<PrimeWebSocket>());
        }
    }

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
        String channel = request.getRequestURI().split("/prime-push/")[1];

        return new PrimeWebSocket(channel);
    }

    private class PrimeWebSocket implements WebSocket, WebSocket.OnTextMessage {

        Connection connection;
        
        String channel;
        
        public PrimeWebSocket(String channel) {
            this.channel = channel;
        }

        public void onClose(int closeCode, String message) {
            connectedClients.get(this.channel).remove(this);
        }

        public void onOpen(Connection connection) {
            this.connection = connection;
            connectedClients.get(this.channel).add(this);
        }

        public void onMessage(String message) {   
            try {
                for(PrimeWebSocket ws : connectedClients.get(this.channel)) {
                    ws.connection.sendMessage(message);
                }    
            }catch(IOException e) {
                System.out.println(e);
            }
        }
    }
}