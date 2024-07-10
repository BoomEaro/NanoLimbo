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

package ua.nanit.limbo.world;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class DimensionRegistry {

    private final LimboServer server;

    private Dimension defaultDimension_1_16;
    private Dimension defaultDimension_1_18_2;
    private Dimension dimension_1_20_5;
    private Dimension dimension_1_21;

    private CompoundBinaryTag codec_1_16;
    private CompoundBinaryTag codec_1_18_2;
    private CompoundBinaryTag codec_1_19;
    private CompoundBinaryTag codec_1_19_1;
    private CompoundBinaryTag codec_1_19_4;
    private CompoundBinaryTag codec_1_20;
    private CompoundBinaryTag codec_1_21;
    private CompoundBinaryTag oldCodec;

    public DimensionRegistry(LimboServer server) {
        this.server = server;
    }

    public CompoundBinaryTag getCodec_1_16() {
        return codec_1_16;
    }

    public CompoundBinaryTag getCodec_1_18_2() {
        return codec_1_18_2;
    }

    public CompoundBinaryTag getCodec_1_19() {
        return codec_1_19;
    }

    public CompoundBinaryTag getCodec_1_19_1() {
        return codec_1_19_1;
    }

    public CompoundBinaryTag getCodec_1_19_4() {
        return codec_1_19_4;
    }

    public CompoundBinaryTag getCodec_1_20() {
        return codec_1_20;
    }

    public CompoundBinaryTag getCodec_1_21() {
        return codec_1_21;
    }

    public CompoundBinaryTag getOldCodec() {
        return oldCodec;
    }

    public Dimension getDefaultDimension_1_16() {
        return defaultDimension_1_16;
    }

    public Dimension getDefaultDimension_1_18_2() {
        return defaultDimension_1_18_2;
    }

    public Dimension getDimension_1_20_5() {
        return dimension_1_20_5;
    }

    public Dimension getDimension_1_21() {
        return dimension_1_21;
    }

    public void load(String def) throws IOException {
        // On 1.16-1.16.1 different codec format
        oldCodec = readCodecFile("/dimension/codec_old.snbt");
        codec_1_16 = readCodecFile("/dimension/codec_1_16.snbt");
        codec_1_18_2 = readCodecFile("/dimension/codec_1_18_2.snbt");
        codec_1_19 = readCodecFile("/dimension/codec_1_19.snbt");
        codec_1_19_1 = readCodecFile("/dimension/codec_1_19_1.snbt");
        codec_1_19_4 = readCodecFile("/dimension/codec_1_19_4.snbt");
        codec_1_20 = readCodecFile("/dimension/codec_1_20.snbt");
        codec_1_21 = readCodecFile("/dimension/codec_1_21.snbt");

        defaultDimension_1_16 = getDefaultDimension(def, codec_1_16);
        defaultDimension_1_18_2 = getDefaultDimension(def, codec_1_18_2);

        dimension_1_20_5 = getModernDimension(def, codec_1_20);
        dimension_1_21 = getModernDimension(def, codec_1_21);
    }

    private Dimension getDefaultDimension(String def, CompoundBinaryTag tag) {
        ListBinaryTag dimensions = tag.getCompound("minecraft:dimension_type").getList("value");

        CompoundBinaryTag overWorld = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(0)).get("element");
        CompoundBinaryTag nether = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(2)).get("element");
        CompoundBinaryTag theEnd = (CompoundBinaryTag) ((CompoundBinaryTag) dimensions.get(3)).get("element");

        switch (def.toLowerCase()) {
            case "overworld":
                return new Dimension(0, "minecraft:overworld", overWorld);
            case "the_nether":
                return new Dimension(-1, "minecraft:nether", nether);
            case "the_end":
                return new Dimension(1, "minecraft:the_end", theEnd);
            default:
                Log.warning("Undefined dimension type: '%s'. Using THE_END as default", def);
                return new Dimension(1, "minecraft:the_end", theEnd);
        }
    }

    private Dimension getModernDimension(String def, CompoundBinaryTag tag) {
        switch (def.toLowerCase()) {
            case "overworld":
                return new Dimension(0, "minecraft:overworld", tag);
            case "the_nether":
                return new Dimension(2, "minecraft:nether", tag);
            case "the_end":
                return new Dimension(3, "minecraft:the_end", tag);
            default:
                Log.warning("Undefined dimension type: '%s'. Using THE_END as default", def);
                return new Dimension(3, "minecraft:the_end", tag);
        }
    }

    private CompoundBinaryTag readCodecFile(String resPath) throws IOException {
        InputStream in = server.getClass().getResourceAsStream(resPath);

        if (in == null)
            throw new FileNotFoundException("Cannot find dimension registry file");

        return TagStringIO.get().asCompound(streamToString(in));
    }

    private String streamToString(InputStream in) throws IOException {
        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return bufReader.lines().collect(Collectors.joining("\n"));
        }
    }
}
