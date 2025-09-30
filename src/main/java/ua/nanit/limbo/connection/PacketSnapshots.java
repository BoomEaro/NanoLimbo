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

package ua.nanit.limbo.connection;

import io.netty.buffer.ByteBufAllocator;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import ua.nanit.limbo.LimboConstants;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.packets.configuration.PacketFinishConfiguration;
import ua.nanit.limbo.protocol.packets.configuration.PacketKnownPacks;
import ua.nanit.limbo.protocol.packets.configuration.PacketRegistryData;
import ua.nanit.limbo.protocol.packets.configuration.PacketUpdateTags;
import ua.nanit.limbo.protocol.packets.login.PacketLoginSuccess;
import ua.nanit.limbo.protocol.packets.play.*;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.data.Title;
import ua.nanit.limbo.util.NbtMessageUtil;
import ua.nanit.limbo.util.UuidUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class PacketSnapshots {

    public static PacketSnapshot PACKET_LOGIN_SUCCESS;
    public static PacketSnapshot PACKET_JOIN_GAME;
    public static PacketSnapshot PACKET_SPAWN_POSITION;
    public static PacketSnapshot PACKET_PLUGIN_MESSAGE;
    public static PacketSnapshot PACKET_PLAYER_ABILITIES;
    public static PacketSnapshot PACKET_PLAYER_INFO;
    public static PacketSnapshot PACKET_DECLARE_COMMANDS;
    public static PacketSnapshot PACKET_JOIN_MESSAGE;
    public static PacketSnapshot PACKET_BOSS_BAR;
    public static PacketSnapshot PACKET_HEADER_AND_FOOTER;

    public static PacketSnapshot PACKET_PLAYER_POS_AND_LOOK_LEGACY;
    // For 1.19 we need to spawn player outside the world to avoid stuck in terrain loading
    public static PacketSnapshot PACKET_PLAYER_POS_AND_LOOK;

    public static PacketSnapshot PACKET_TITLE_TITLE;
    public static PacketSnapshot PACKET_TITLE_SUBTITLE;
    public static PacketSnapshot PACKET_TITLE_TIMES;

    public static PacketSnapshot PACKET_TITLE_LEGACY_TITLE;
    public static PacketSnapshot PACKET_TITLE_LEGACY_SUBTITLE;
    public static PacketSnapshot PACKET_TITLE_LEGACY_TIMES;

    public static PacketSnapshot PACKET_REGISTRY_DATA;

    public static PacketSnapshot PACKET_KNOWN_PACKS;

    public static PacketSnapshot PACKET_UPDATE_TAGS;

    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_20_5;
    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_21;
    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_21_2;
    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_21_4;
    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_21_5;
    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_21_6;
    public static List<PacketSnapshot> PACKETS_REGISTRY_DATA_1_21_7;

    public static PacketSnapshot PACKET_FINISH_CONFIGURATION;

    public static List<PacketSnapshot> PACKETS_EMPTY_CHUNKS;
    public static PacketSnapshot PACKET_START_WAITING_CHUNKS;

    public static void initPackets(LimboServer server) {
        final String username = server.getConfig().getPingData().getVersion();
        final UUID uuid = UuidUtil.getOfflineModeUuid(username);

        PacketLoginSuccess loginSuccess = new PacketLoginSuccess();
        loginSuccess.setUsername(username);
        loginSuccess.setUuid(uuid);

        PacketJoinGame joinGame = new PacketJoinGame();
        String worldName = "minecraft:" + server.getConfig().getDimensionType().toLowerCase(Locale.ROOT);
        joinGame.setEntityId(0);
        joinGame.setEnableRespawnScreen(true);
        joinGame.setFlat(false);
        joinGame.setGameMode(server.getConfig().getGameMode());
        joinGame.setSecureProfile(server.getConfig().isSecureProfile());
        joinGame.setHardcore(false);
        joinGame.setMaxPlayers(server.getConfig().getMaxPlayers());
        joinGame.setPreviousGameMode(-1);
        joinGame.setReducedDebugInfo(true);
        joinGame.setDebug(false);
        joinGame.setViewDistance(0);
        joinGame.setWorldName(worldName);
        joinGame.setWorldNames(worldName);
        joinGame.setHashedSeed(0);
        joinGame.setDimensionRegistry(server.getDimensionRegistry());

        PacketPlayerAbilities playerAbilities = new PacketPlayerAbilities();
        playerAbilities.setFlyingSpeed(0.0F);
        playerAbilities.setFlags(0x02);
        playerAbilities.setFieldOfView(0.1F);

        int teleportId = ThreadLocalRandom.current().nextInt();

        PacketPlayerPositionAndLook positionAndLookLegacy
                = new PacketPlayerPositionAndLook(0, 64, 0, 0, 0, teleportId);

        PacketPlayerPositionAndLook positionAndLook
                = new PacketPlayerPositionAndLook(0, 400, 0, 0, 0, teleportId);

        PacketSpawnPosition packetSpawnPosition = new PacketSpawnPosition(0, 400, 0);

        PacketDeclareCommands declareCommands = new PacketDeclareCommands();
        declareCommands.setCommands(Collections.emptyList());

        PacketPlayerInfo info = new PacketPlayerInfo();
        String playerListName = server.getConfig().getPlayerListUsername();
        if (playerListName.length() > 16) {
            playerListName = playerListName.substring(0, 16);
        }
        info.setUsername(playerListName);
        info.setGameMode(server.getConfig().getGameMode());
        info.setUuid(uuid);

        PACKET_LOGIN_SUCCESS = PacketSnapshot.of(loginSuccess);
        PACKET_JOIN_GAME = PacketSnapshot.of(joinGame);
        PACKET_PLAYER_POS_AND_LOOK_LEGACY = PacketSnapshot.of(positionAndLookLegacy);
        PACKET_PLAYER_POS_AND_LOOK = PacketSnapshot.of(positionAndLook);
        PACKET_SPAWN_POSITION = PacketSnapshot.of(packetSpawnPosition);
        PACKET_PLAYER_ABILITIES = PacketSnapshot.of(playerAbilities);
        PACKET_PLAYER_INFO = PacketSnapshot.of(info);

        PACKET_DECLARE_COMMANDS = PacketSnapshot.of(declareCommands);

        if (server.getConfig().isUseHeaderAndFooter()) {
            PacketPlayerListHeader header = new PacketPlayerListHeader();
            header.setHeader(NbtMessageUtil.create(server.getConfig().getPlayerListHeader()));
            header.setFooter(NbtMessageUtil.create(server.getConfig().getPlayerListFooter()));
            PACKET_HEADER_AND_FOOTER = PacketSnapshot.of(header);
        }

        if (server.getConfig().isUseBrandName()) {
            PacketPluginMessage pluginMessage = new PacketPluginMessage();
            pluginMessage.setChannel(LimboConstants.BRAND_CHANNEL);
            ByteMessage byteMessage = new ByteMessage(ByteBufAllocator.DEFAULT.heapBuffer());
            try {
                byteMessage.writeString(server.getConfig().getBrandName());
                pluginMessage.setData(byteMessage.toByteArray());
            } finally {
                byteMessage.release();
            }
            PACKET_PLUGIN_MESSAGE = PacketSnapshot.of(pluginMessage);
        }

        if (server.getConfig().isUseJoinMessage()) {
            PacketChatMessage joinMessage = new PacketChatMessage();
            joinMessage.setMessage(NbtMessageUtil.create(server.getConfig().getJoinMessage()));
            joinMessage.setPosition(PacketChatMessage.PositionLegacy.SYSTEM_MESSAGE);
            joinMessage.setSender(UUID.randomUUID());
            PACKET_JOIN_MESSAGE = PacketSnapshot.of(joinMessage);
        }

        if (server.getConfig().isUseBossBar()) {
            PacketBossBar bossBar = new PacketBossBar();
            bossBar.setBossBar(server.getConfig().getBossBar());
            bossBar.setUuid(UUID.randomUUID());
            PACKET_BOSS_BAR = PacketSnapshot.of(bossBar);
        }

        if (server.getConfig().isUseTitle()) {
            Title title = server.getConfig().getTitle();

            PacketTitleSetTitle packetTitle = new PacketTitleSetTitle();
            PacketTitleSetSubTitle packetSubtitle = new PacketTitleSetSubTitle();
            PacketTitleTimes packetTimes = new PacketTitleTimes();

            PacketTitleLegacy legacyTitle = new PacketTitleLegacy();
            PacketTitleLegacy legacySubtitle = new PacketTitleLegacy();
            PacketTitleLegacy legacyTimes = new PacketTitleLegacy();

            packetTitle.setTitle(title.getTitle());
            packetSubtitle.setSubtitle(title.getSubtitle());
            packetTimes.setFadeIn(title.getFadeIn());
            packetTimes.setStay(title.getStay());
            packetTimes.setFadeOut(title.getFadeOut());

            legacyTitle.setTitle(title);
            legacyTitle.setAction(PacketTitleLegacy.Action.SET_TITLE);

            legacySubtitle.setTitle(title);
            legacySubtitle.setAction(PacketTitleLegacy.Action.SET_SUBTITLE);

            legacyTimes.setTitle(title);
            legacyTimes.setAction(PacketTitleLegacy.Action.SET_TIMES_AND_DISPLAY);

            PACKET_TITLE_TITLE = PacketSnapshot.of(packetTitle);
            PACKET_TITLE_SUBTITLE = PacketSnapshot.of(packetSubtitle);
            PACKET_TITLE_TIMES = PacketSnapshot.of(packetTimes);

            PACKET_TITLE_LEGACY_TITLE = PacketSnapshot.of(legacyTitle);
            PACKET_TITLE_LEGACY_SUBTITLE = PacketSnapshot.of(legacySubtitle);
            PACKET_TITLE_LEGACY_TIMES = PacketSnapshot.of(legacyTimes);
        }

        PACKET_KNOWN_PACKS = PacketSnapshot.of(PacketKnownPacks.class, (version) -> {
            PacketKnownPacks packetKnownPacks = new PacketKnownPacks();

            packetKnownPacks.setKnownPacks(List.of(
                    new PacketKnownPacks.KnownPack(
                            "minecraft",
                            "core",
                            version.getDisplayName()
                    )
            ));

            return packetKnownPacks;
        });

        PACKET_UPDATE_TAGS = PacketSnapshot.of(PacketUpdateTags.class, (version) -> {
            PacketUpdateTags packetUpdateTags = new PacketUpdateTags();
            if (version.moreOrEqual(Version.V1_21_7)) {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_21_7()));
            } else if (version.moreOrEqual(Version.V1_21_6)) {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_21_6()));
            } else if (version.moreOrEqual(Version.V1_21_5)) {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_21_5()));
            } else if (version.moreOrEqual(Version.V1_21_4)) {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_21_4()));
            } else if (version.moreOrEqual(Version.V1_21_2)) {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_21_2()));
            } else if (version.moreOrEqual(Version.V1_21)) {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_21()));
            } else {
                packetUpdateTags.setTags(parseUpdateTags(server.getDimensionRegistry().getTags_1_20_5()));
            }

            return packetUpdateTags;
        });

        PacketRegistryData packetRegistryData = new PacketRegistryData();
        packetRegistryData.setDimensionRegistry(server.getDimensionRegistry());

        PACKET_REGISTRY_DATA = PacketSnapshot.of(packetRegistryData);

        PACKETS_REGISTRY_DATA_1_20_5 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_20_5());
        PACKETS_REGISTRY_DATA_1_21 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_21());
        PACKETS_REGISTRY_DATA_1_21_2 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_21_2());
        PACKETS_REGISTRY_DATA_1_21_4 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_21_4());
        PACKETS_REGISTRY_DATA_1_21_5 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_21_5());
        PACKETS_REGISTRY_DATA_1_21_6 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_21_6());
        PACKETS_REGISTRY_DATA_1_21_7 = createRegistryData(server, server.getDimensionRegistry().getCodec_1_21_7());

        PACKET_FINISH_CONFIGURATION = PacketSnapshot.of(new PacketFinishConfiguration());

        PacketGameEvent packetGameEvent = new PacketGameEvent();
        packetGameEvent.setType((byte) 13); // Waiting for chunks type
        packetGameEvent.setValue(0);
        PACKET_START_WAITING_CHUNKS = PacketSnapshot.of(packetGameEvent);

        int chunkXOffset = 0; // Default x position is 0
        int chunkZOffset = 0; // Default z position is 0
        int chunkEdgeSize = 1; // TODO Make configurable?

        List<PacketSnapshot> emptyChunks = new ArrayList<>();
        // Make multiple chunks for edges
        for (int chunkX = chunkXOffset - chunkEdgeSize; chunkX <= chunkXOffset + chunkEdgeSize; ++chunkX) {
            for (int chunkZ = chunkZOffset - chunkEdgeSize; chunkZ <= chunkZOffset + chunkEdgeSize; ++chunkZ) {
                PacketEmptyChunk packetEmptyChunk = new PacketEmptyChunk();
                packetEmptyChunk.setX(chunkX);
                packetEmptyChunk.setZ(chunkZ);

                emptyChunks.add(PacketSnapshot.of(packetEmptyChunk));
            }
        }
        PACKETS_EMPTY_CHUNKS = emptyChunks;
    }

    private static Map<String, Map<String, List<Integer>>> parseUpdateTags(CompoundBinaryTag tags) {
        Map<String, Map<String, List<Integer>>> tagsMap = new HashMap<>();

        for (Map.Entry<String, ? extends BinaryTag> namedTag : tags) {
            Map<String, List<Integer>> subTagsMap = new HashMap<>();

            CompoundBinaryTag subTag = (CompoundBinaryTag) namedTag.getValue();
            for (Map.Entry<String, ? extends BinaryTag> subNamedTag : subTag) {
                List<Integer> idsList = new ArrayList<>();
                ListBinaryTag ids = (ListBinaryTag) subNamedTag.getValue();
                for (BinaryTag id : ids) {
                    idsList.add(((IntBinaryTag) id).value());
                }

                subTagsMap.put(subNamedTag.getKey(), idsList);
            }

            tagsMap.put(namedTag.getKey(), subTagsMap);
        }

        return tagsMap;
    }

    private static List<PacketSnapshot> createRegistryData(LimboServer server, CompoundBinaryTag dimensionTag) {
        List<PacketSnapshot> packetRegistries = new ArrayList<>();
        for (String registryType : dimensionTag.keySet()) {
            CompoundBinaryTag compoundRegistryType = dimensionTag.getCompound(registryType);

            PacketRegistryData registryData = new PacketRegistryData();
            registryData.setDimensionRegistry(server.getDimensionRegistry());

            ListBinaryTag values = compoundRegistryType.getList("value");
            registryData.setMetadataWriter((message, version) -> {
                message.writeString(registryType);

                message.writeVarInt(values.size());
                for (BinaryTag entry : values) {
                    CompoundBinaryTag entryTag = (CompoundBinaryTag) entry;

                    String name = entryTag.getString("name");
                    CompoundBinaryTag element = entryTag.getCompound("element", null);

                    message.writeString(name);
                    if (element != null) {
                        message.writeBoolean(true);
                        message.writeCompoundTag(element, version);
                    } else {
                        message.writeBoolean(false);
                    }
                }
            });

            packetRegistries.add(PacketSnapshot.of(registryData));
        }

        return packetRegistries;
    }
}
