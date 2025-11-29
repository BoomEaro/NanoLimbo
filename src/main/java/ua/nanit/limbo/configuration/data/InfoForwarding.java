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

package ua.nanit.limbo.configuration.data;

import lombok.Data;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.charset.StandardCharsets;
import java.util.List;

@ConfigSerializable
@Data
public class InfoForwarding {

    private Type type = Type.NONE;
    private byte[] secret = "<YOUR_SECRET_HERE>".getBytes(StandardCharsets.UTF_8);
    private List<String> tokens = List.of("<BUNGEE_GUARD_TOKEN>");

    public boolean hasToken(@Nullable String token) {
        return this.tokens != null && token != null && tokens.contains(token);
    }

    public boolean isNone() {
        return this.type == Type.NONE;
    }

    public boolean isLegacy() {
        return this.type == Type.LEGACY;
    }

    public boolean isModern() {
        return this.type == Type.MODERN;
    }

    public boolean isBungeeGuard() {
        return this.type == Type.BUNGEE_GUARD;
    }

    public enum Type {
        NONE,
        LEGACY,
        MODERN,
        BUNGEE_GUARD
    }
}
