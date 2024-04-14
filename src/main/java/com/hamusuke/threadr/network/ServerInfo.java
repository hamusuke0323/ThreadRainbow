package com.hamusuke.threadr.network;

import com.hamusuke.threadr.util.Util;

import java.util.Objects;

public class ServerInfo {
    public String address;
    public int port;
    public transient int protocolVersion;
    public transient int ping;
    public transient Status status = Status.NONE;

    public ServerInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public String toString() {
        return switch (this.status) {
            case NONE ->
                    Util.toHTML(String.format("%s:%d\n%s", this.address, this.port, "更新ボタンを押すことでこのサーバーの情報を取得できます"));
            case CONNECTING -> Util.toHTML(String.format("%s:%d\n%s", this.address, this.port, "応答確認中"));
            case MISMATCH_PROTOCOL_VERSION ->
                    Util.toHTML(String.format("%s:%d (%dms)\n%s", this.address, this.port, this.ping, "接続できません。プロトコルのバージョンが違います"));
            case OK -> Util.toHTML(String.format("%s:%d (%dms)\nOK", this.address, this.port, this.ping));
            case FAILED -> Util.toHTML(String.format("%s:%d\n%s", this.address, this.port, "サーバーに接続できません"));
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return port == that.port && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    public enum Status {
        NONE,
        CONNECTING,
        MISMATCH_PROTOCOL_VERSION,
        OK,
        FAILED
    }
}
