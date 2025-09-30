/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.Getter;
import lombok.SneakyThrows;
import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.connection.ClientChannelInitializer;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.connection.PacketHandler;
import ua.nanit.limbo.connection.PacketSnapshots;
import ua.nanit.limbo.world.DimensionRegistry;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public final class LimboServer {

    private LimboConfig config;
    private PacketHandler packetHandler;
    private Connections connections;
    private DimensionRegistry dimensionRegistry;
    private ScheduledFuture<?> keepAliveTask;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private CommandManager commandManager;

    @SneakyThrows
    public void start() {
        config = new LimboConfig(Paths.get("./"));
        config.load();

        Log.setLevel(config.getDebugLevel());
        Log.info("Starting server...");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        packetHandler = new PacketHandler(this);
        dimensionRegistry = new DimensionRegistry(this);
        dimensionRegistry.load("minecraft:" + config.getDimensionType().toLowerCase(Locale.ROOT));
        connections = new Connections();

        PacketSnapshots.initPackets(this);

        startBootstrap();

        keepAliveTask = workerGroup.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "NanoLimbo shutdown thread"));

        Log.info("Server started on %s", config.getAddress());

        commandManager = new CommandManager();
        commandManager.registerAll(this);
        commandManager.start();

        System.gc();
    }

    private void startBootstrap() {
        ChannelFactory<? extends ServerChannel> channelFactory;

        if (config.isUseEpoll() && Epoll.isAvailable()) {
            bossGroup = new MultiThreadIoEventLoopGroup(config.getBossGroupSize(), EpollIoHandler.newFactory());
            workerGroup = new MultiThreadIoEventLoopGroup(config.getWorkerGroupSize(), EpollIoHandler.newFactory());
            channelFactory = EpollServerSocketChannel::new;
            Log.debug("Using Epoll transport type");
        } else {
            bossGroup = new MultiThreadIoEventLoopGroup(config.getBossGroupSize(), NioIoHandler.newFactory());
            workerGroup = new MultiThreadIoEventLoopGroup(config.getWorkerGroupSize(), NioIoHandler.newFactory());
            channelFactory = NioServerSocketChannel::new;
            Log.debug("Using Java NIO transport type");
        }

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channelFactory(channelFactory)
                .childHandler(new ClientChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(config.getAddress())
                .bind();
    }

    private void broadcastKeepAlive() {
        connections.getAllConnections().forEach(ClientConnection::sendKeepAlive);
    }

    private void stop() {
        Log.info("Stopping server...");

        if (keepAliveTask != null) {
            keepAliveTask.cancel(true);
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        Log.info("Server stopped, Goodbye!");
    }
}
