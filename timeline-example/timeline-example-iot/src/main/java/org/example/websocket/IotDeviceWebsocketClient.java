package org.example.websocket;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@Slf4j
public class IotDeviceWebsocketClient extends WebSocketClient {
    public IotDeviceWebsocketClient(URI serverUri) {
        super(serverUri);
        try {
            this.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public IotDeviceWebsocketClient(String ws) throws URISyntaxException {
        super(new URI(ws));

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        log.info("new message:{}", s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

}
