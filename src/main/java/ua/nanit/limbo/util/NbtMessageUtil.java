package ua.nanit.limbo.util;

import com.alibaba.fastjson2.*;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.nbt.*;
import ua.nanit.limbo.protocol.NbtMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@UtilityClass
public class NbtMessageUtil {

    public static NbtMessage create(String json) {
        CompoundBinaryTag compoundBinaryTag = (CompoundBinaryTag) fromJson(JSON.parse(json));

        return new NbtMessage(json, compoundBinaryTag);
    }

    public static BinaryTag fromJson(Object json) {
        if (json instanceof String) {
            return StringBinaryTag.stringBinaryTag((String) json);
        } else if (json instanceof Number) {
            Number number = (Number) json;

            if (number instanceof Byte) {
                return ByteBinaryTag.byteBinaryTag((Byte) number);
            } else if (number instanceof Short) {
                return ShortBinaryTag.shortBinaryTag((Short) number);
            } else if (number instanceof Integer) {
                return IntBinaryTag.intBinaryTag((Integer) number);
            } else if (number instanceof Long) {
                return LongBinaryTag.longBinaryTag((Long) number);
            } else if (number instanceof Float) {
                return FloatBinaryTag.floatBinaryTag((Float) number);
            } else if (number instanceof Double) {
                return DoubleBinaryTag.doubleBinaryTag((Double) number);
            }
        } else if (json instanceof Boolean) {
            return ByteBinaryTag.byteBinaryTag((Boolean) json ? (byte) 1 : (byte) 0);
        } else if (json instanceof JSONObject) {
            CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
            for (Map.Entry<String, Object> property : ((JSONObject) json).entrySet()) {
                builder.put(property.getKey(), fromJson(property.getValue()));
            }

            return builder.build();
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;

            if (jsonArray.isEmpty()) {
                return ListBinaryTag.listBinaryTag(EndBinaryTag.endBinaryTag().type(), Collections.emptyList());
            }

            BinaryTagType<ByteBinaryTag> tagByteType = ByteBinaryTag.ZERO.type();
            BinaryTagType<IntBinaryTag> tagIntType = IntBinaryTag.intBinaryTag(0).type();
            BinaryTagType<LongBinaryTag> tagLongType = LongBinaryTag.longBinaryTag(0).type();

            BinaryTag listTag;
            BinaryTagType<? extends BinaryTag> listType = fromJson(jsonArray.get(0)).type();
            if (listType.equals(tagByteType)) {
                byte[] bytes = new byte[jsonArray.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = ((Number) jsonArray.get(i)).byteValue();
                }

                listTag = ByteArrayBinaryTag.byteArrayBinaryTag(bytes);
            } else if (listType.equals(tagIntType)) {
                int[] ints = new int[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = ((Number) jsonArray.get(i)).intValue();
                }

                listTag = IntArrayBinaryTag.intArrayBinaryTag(ints);
            } else if (listType.equals(tagLongType)) {
                long[] longs = new long[jsonArray.size()];
                for (int i = 0; i < longs.length; i++) {
                    longs[i] = ((Number) jsonArray.get(i)).longValue();
                }

                listTag = LongArrayBinaryTag.longArrayBinaryTag(longs);
            } else {
                List<BinaryTag> tagItems = new ArrayList<>(jsonArray.size());

                for (Object jsonEl : jsonArray) {
                    BinaryTag subTag = fromJson(jsonEl);
                    if (subTag.type() != listType) {
                        throw new IllegalArgumentException("Cannot convert mixed JsonArray to Tag");
                    }

                    tagItems.add(subTag);
                }

                listTag = ListBinaryTag.listBinaryTag(listType, tagItems);
            }

            return listTag;
        } else if (json == null) {
            return EndBinaryTag.endBinaryTag();
        }

        throw new IllegalArgumentException("Unknown JSON element: " + json);
    }
}
