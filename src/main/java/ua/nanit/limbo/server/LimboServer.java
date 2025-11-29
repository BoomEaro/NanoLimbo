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
import lombok.NonNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ua.nanit.limbo.configuration.LimboConfiguration;
import ua.nanit.limbo.configuration.data.Netty;
import ua.nanit.limbo.configuration.serializers.ByteArraySerializer;
import ua.nanit.limbo.configuration.serializers.InetSocketAddressSerializer;
import ua.nanit.limbo.configuration.serializers.NbtMessageSerializer;
import ua.nanit.limbo.connection.ClientChannelInitializer;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.connection.PacketHandler;
import ua.nanit.limbo.connection.PacketSnapshots;
import ua.nanit.limbo.protocol.NbtMessage;
import ua.nanit.limbo.world.DimensionRegistry;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public final class LimboServer {

    private LimboConfiguration configuration;
    private PacketHandler packetHandler;
    private Connections connections;
    private DimensionRegistry dimensionRegistry;
    private ScheduledFuture<?> keepAliveTask;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private CommandManager commandManager;

    public void start() throws Exception {
        this.configuration = loadConfiguration();

        Log.setLevel(this.configuration.getDebugLevel());
        Log.info("Starting server...");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        this.packetHandler = new PacketHandler(this);
        this.dimensionRegistry = new DimensionRegistry(this);
        this.dimensionRegistry.load("minecraft:" + this.configuration.getDimensionType().name().toLowerCase(Locale.ROOT));
        this.connections = new Connections();

        PacketSnapshots.initPackets(this);

        startBootstrap();

        this.keepAliveTask = workerGroup.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "NanoLimbo shutdown thread"));

        Log.info("Server started on %s", configuration.getBind());

        this.commandManager = new CommandManager();
        this.commandManager.registerAll(this);
        this.commandManager.start();

        System.gc();
    }

    private void startBootstrap() {
        ChannelFactory<? extends ServerChannel> channelFactory;

        Netty netty = this.configuration.getNetty();
        if (netty.isUseEpoll() && Epoll.isAvailable()) {
            this.bossGroup = new MultiThreadIoEventLoopGroup(netty.getThreads().getBossGroup(), EpollIoHandler.newFactory());
            this.workerGroup = new MultiThreadIoEventLoopGroup(netty.getThreads().getWorkerGroup(), EpollIoHandler.newFactory());
            channelFactory = EpollServerSocketChannel::new;
            Log.debug("Using Epoll transport type");
        } else {
            this.bossGroup = new MultiThreadIoEventLoopGroup(netty.getThreads().getBossGroup(), NioIoHandler.newFactory());
            this.workerGroup = new MultiThreadIoEventLoopGroup(netty.getThreads().getWorkerGroup(), NioIoHandler.newFactory());
            channelFactory = NioServerSocketChannel::new;
            Log.debug("Using Java NIO transport type");
        }

        new ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channelFactory(channelFactory)
                .childHandler(new ClientChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(this.configuration.getBind())
                .bind();
    }

    private void broadcastKeepAlive() {
        this.connections.getAllConnections().forEach(ClientConnection::sendKeepAlive);
    }

    @NonNull
    private LimboConfiguration loadConfiguration() throws ConfigurateException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Paths.get("").toAbsolutePath().resolve("settings.yml"))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .defaultOptions(configurationOptions -> configurationOptions.shouldCopyDefaults(true).serializers(builder -> {
                    builder.register(InetSocketAddress.class, new InetSocketAddressSerializer());
                    builder.register(NbtMessage.class, new NbtMessageSerializer());
                    builder.register(byte[].class, new ByteArraySerializer());
                }))
                .build();

        ConfigurationNode node = loader.load();
        LimboConfiguration configuration = node.get(LimboConfiguration.class);
        if (configuration != null) {
            ConfigurationNode nodeToSave = loader.createNode().set(LimboConfiguration.class, new LimboConfiguration());

            loader.save(nodeToSave);
            return configuration;
        }

        throw new IllegalStateException("Failed to load configuration");
    }

    private void stop() {
        Log.info("Stopping server...");

        if (this.keepAliveTask != null) {
            this.keepAliveTask.cancel(true);
        }

        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }

        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }

        Log.info("Server stopped, Goodbye!");
    }
}
