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

package ua.nanit.limbo.configuration;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import ua.nanit.limbo.configuration.data.*;

import java.net.InetSocketAddress;

@ConfigSerializable
@Data
public class LimboConfiguration {

    @Comment("""
            
            NanoLimbo configuration
            
            
            Server's host address and port. Set ip empty to use public address""")
    private InetSocketAddress bind = new InetSocketAddress("localhost", 65535);

    @Comment("Max number of players can join to server\n" +
            "Set -1 to make it infinite")
    private int maxPlayers = 100;

    @Comment("Server's data in servers list")
    private PingData ping = new PingData();

    @Comment("Available dimensions: OVERWORLD, NETHER, THE_END")
    private DimensionType dimensionType = DimensionType.THE_END;

    @Comment("Whether to display the player in the player list\n" +
            "For 1.16.5 clients, the player list will be sent even if disabled, to avoid crash")
    private PlayerList playerList = new PlayerList();

    @Comment("Whether to display header and footer in player list\n" +
            "For 1.8+ clients")
    private HeaderAndFooter headerAndFooter = new HeaderAndFooter();

    @Comment("""
            Setup player's game mode
            0 - Survival
            1 - Creative (hide HP and food bar)
            2 - Adventure
            3 - Spectator (hide all UI bars)
            Spectator works on 1.8+ clients""")
    private int gameMode = 3;

    @Comment("Remove secure-chat toast\n" +
            "For 1.20.5+ clients")
    private boolean secureProfile = false;

    @Comment("Server name which is shown under F3\n" +
            "For 1.13+ clients")
    private BrandName brandName = new BrandName();

    @Comment("Message sends when player joins to the server")
    private JoinMessage joinMessage = new JoinMessage();

    @Comment("BossBar displays when player joins to the server\n" +
            "For 1.9+ clients")
    private BossBar bossBar = new BossBar();

    @Comment("Display title and subtitle\n" +
            "For 1.8+ clients")
    private Title title = new Title();

    @Comment("""
            Player info forwarding support.
            Available types:
             - NONE
             - LEGACY
             - MODERN
             - BUNGEE_GUARD
            Don't use secret if you do not use MODERN type""")
    private InfoForwarding infoForwarding = new InfoForwarding();

    @Comment("Read timeout for connections in milliseconds")
    private long readTimeout = 30000;

    @Comment("""
            Define log level. For production, I'd recommend to use level 2
            Log levels:
            0 - Display only errors
            1 - Display errors, warnings
            2 - Display errors, warnings, info
            3 - Display errors, warnings, info, debug""")
    private int debugLevel = 2;

    @Comment("Warning! Do not touch params of this block if you are not completely sure what is this!")
    private Netty netty = new Netty();

    @Comment("Options to check incoming traffic and kick potentially malicious connections.\n" +
            "Take into account that player can send many small packets, for example, just moving mouse.")
    private Traffic traffic = new Traffic();

}
