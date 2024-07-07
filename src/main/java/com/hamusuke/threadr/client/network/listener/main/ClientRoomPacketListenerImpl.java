package com.hamusuke.threadr.client.network.listener.main;

import com.hamusuke.threadr.client.ThreadRainbowClient;
import com.hamusuke.threadr.client.gui.component.panel.dialog.CenteredMessagePanel;
import com.hamusuke.threadr.client.gui.component.panel.main.room.RoomPanel;
import com.hamusuke.threadr.client.network.listener.main.play.ClientPlayPacketListenerImpl;
import com.hamusuke.threadr.network.channel.Connection;
import com.hamusuke.threadr.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.threadr.network.protocol.packet.clientbound.common.ChangeHostNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.GameModeSyncNotify;
import com.hamusuke.threadr.network.protocol.packet.clientbound.room.StartGameNotify;

public class ClientRoomPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientRoomPacketListener {
    public ClientRoomPacketListenerImpl(ThreadRainbowClient client, Connection connection) {
        super(client, client.curRoom, connection);
        this.clientSpider = client.clientSpider;
    }

    @Override
    public void handleChangeHost(ChangeHostNotify packet) {
        super.handleChangeHost(packet);
        this.client.setPanel(this.client.getPanel());
    }

    @Override
    public void handleStartGame(StartGameNotify packet) {
        this.client.setPanel(new CenteredMessagePanel("ゲームを開始しています..."));
        var listener = ClientPlayPacketListenerImpl.newListenerByGameMode(packet.mode(), this.client, this.connection);
        this.connection.setListener(listener);
        this.connection.setProtocol(packet.nextProtocol());
    }

    @Override
    public void handleGameModeSync(GameModeSyncNotify packet) {
        if (this.client.getPanel() instanceof RoomPanel roomPanel) {
            roomPanel.setSelectedItem(packet.gameMode());
        }
    }
}
