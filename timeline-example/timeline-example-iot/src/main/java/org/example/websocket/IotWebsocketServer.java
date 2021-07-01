package org.example.websocket;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DeviceDto;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */

@Slf4j
@Component
public class IotWebsocketServer extends WebSocketServer {
    @Resource
    private DeviceSessionManager deviceSessionManager;

    public IotWebsocketServer(int port) {
        super(new InetSocketAddress(port));
        log.info("websocket server listener :{}", port);
    }

    public IotWebsocketServer() {
        this(8889);
        start();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String path = webSocket.getResourceDescriptor();
        String sn = path.replace("/", "");
        if (deviceSessionManager.hasOnline(sn)) {
            log.warn("new websocket reject.because sn has online.sn:{}", sn);
            webSocket.close();
            return;
        }
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setSn(sn);
        deviceSessionManager.onlineDevice(deviceDto);
        log.info("new websocket client:{}", webSocket.getRemoteSocketAddress().getHostName());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String path = webSocket.getResourceDescriptor();
        String sn = path.replace("/", "");
        log.info("websocket client closed:{}", webSocket.getRemoteSocketAddress().getHostName());
        deviceSessionManager.offlineDevice(sn);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        log.info("websocket client[{}] message:{}", webSocket.getRemoteSocketAddress().getHostName(), s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        String path = webSocket.getResourceDescriptor();
        String sn = path.replace("/", "");
        log.info("websocket client error:{}", webSocket.getRemoteSocketAddress().getHostName());
        deviceSessionManager.offlineDevice(sn);


    }

    @Override
    public void onStart() {

    }
}
