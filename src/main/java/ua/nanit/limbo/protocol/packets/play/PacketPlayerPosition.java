package ua.nanit.limbo.protocol.packets.play;

import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketIn;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;

public class PacketPlayerPosition implements PacketIn {

    private double x;
    private double y;
    private double z;
    private boolean onGround;

    public PacketPlayerPosition() {
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.x = msg.readDouble();
        this.y = msg.readDouble();
        this.z = msg.readDouble();
        this.onGround = msg.readBoolean();
    }

    @Override
    public void handle(ClientConnection conn, LimboServer server) {
        server.getPacketHandler().handle(conn, this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
