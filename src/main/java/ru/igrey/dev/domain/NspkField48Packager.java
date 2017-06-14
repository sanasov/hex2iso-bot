package ru.igrey.dev.domain;

/*
 * ++ _NSPK_ Front Office system. ++
 */

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import org.jpos.iso.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * MIR DE 48 packager
 */
public class NspkField48Packager extends ISOBasePackager {

    public static BiMap<String, Integer> getPvuFixedIdMap() {
        BiMap<String, Integer> map = HashBiMap.create(new HashMap<String, Integer>());
        Integer id = 500;
        for (char i = 'A'; i <= 'Z'; i++) {
            for (char j = '0'; j <= 'Z'; j++) {
                String strI = String.valueOf(i);
                String strJ = String.valueOf(j);
                if (strI.matches("[0-9A-Z]") && strJ.matches("[0-9A-Z]")) {
                    map.put(strI + strJ, id++);
                }
            }
        }
        return map;
    }

    private static final Charset DEF_CHARSET = Charset.forName("UTF-8");
    private static final Charset ISO_8859_5 = Charset.forName("ISO-8859-5");

    private static final char TYPE_ASCII = '^';
    private static final char TYPE_BINARY = '%';
    private static final char TYPE_CYRILLIC = '&';

    private static final Set<Integer> BINARY_ELEMENTS = ImmutableSet.of(); // #MIR-998 64,65 not binary now, MIR-1717 also 51, 52
    private static final Set<Integer> CYRILLIC_ELEMENTS = ImmutableSet.of(6);

    protected BiMap<String, Integer> getFixedIdsMap() {
        return getPvuFixedIdMap();
    }

    protected boolean emitBitMap() {
        return false;
    }

    public byte[] pack(ISOComponent c) throws ISOException {
        if (!(c instanceof ISOMsg)) {
            throw new IllegalArgumentException("pack de48: not ISOMsg");
        }

        ISOMsg de48 = (ISOMsg) c;

        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream(128);

            Enumeration<ISOComponent> see = de48.enumerateFields();
            ArrayList<ISOComponent> list = Collections.list(see);
            Collections.sort(list, (c1, c2) -> {
                try {
                    return String.valueOf(c1.getKey()).compareTo(String.valueOf(c2.getKey()));
                } catch (ISOException e) {
                    // some bad shit happened
                }
                return 0;
            });
            for (ISOComponent se : list) {
                Integer index = (Integer) se.getKey();
                if (index < 1 || se.getValue() == null) continue;//NOPMD
                if (index == 65 && se instanceof ISOBitMap) {
                    continue;
                }

                boolean isBinary = BINARY_ELEMENTS.contains(index);
                boolean isCyrillic = false;
                if (isBinary) {
                    buf.write(TYPE_BINARY);
                } else {
                    isCyrillic = CYRILLIC_ELEMENTS.contains(index);
                    buf.write(isCyrillic ? TYPE_CYRILLIC : TYPE_ASCII);
                }

                byte[] b;
                if (se instanceof ISOField) {
                    String s = (String) se.getValue();
                    if (isBinary) {
                        //b = ISOUtil.hex2byte(s);
                        throw new ISOException("pack de48 se " + index + ": string value for binary element");
                    }
                    b = s.getBytes(isCyrillic ? ISO_8859_5 : DEF_CHARSET);
                } else if (se instanceof ISOBinaryField) {
                    b = se.getBytes();
                } else {
                    throw new ISOException("pack de48 se" + index + ": unknown type " + se.getClass().getName());
                }

                packSePrefix(buf, index, b.length);
                buf.write(b);
            }

            return buf.toByteArray();
        } catch (IOException e) {
            throw new ISOException(e);
        }
    }

    private void packSePrefix(ByteArrayOutputStream buf, int index, int len) throws ISOException, IOException {
        String byIndex = getFixedIdsMap().inverse().get(index);
        String id = byIndex == null ? Integer.toString(index) : byIndex;

        if (id.length() > 2)//NOPMD
            throw new ISOException("pack de48: invalid se id: " + id);

        if (len > 255)//NOPMD
            throw new ISOException("pack de48 se" + id + ": invalid length: " + len);

        buf.write((ISOUtil.zeropad(id, 2)
                + ISOUtil.zeropad(Integer.toString(len, 16), 2)
        ).getBytes(DEF_CHARSET));
    }

    public int unpack(ISOComponent m, byte[] b) throws ISOException {
        int consumed = 0;
        if (b.length < 6 || b.length > 999) {
            return b.length;
        }
        while (consumed < b.length) {
            if (consumed + 5 > b.length) {
                throw new ISOException("unpack de48, offset=" + consumed + ": unexpected end of field");
            }

            char type = new String(b, consumed, 1, DEF_CHARSET).charAt(0);
            boolean isBinary = type == TYPE_BINARY,
                    isCyrillic = type == TYPE_CYRILLIC;
            if (type != TYPE_ASCII && !isBinary && !isCyrillic) {
                throw new ISOException("unpack de48, unknown element type: '" + type + "', offset=" + consumed);
            }

            consumed += 1;

            int id;
            try {
                String s = new String(b, consumed, 2, DEF_CHARSET);
                try {
                    id = getFixedIdsMap().get(s);
                } catch (NullPointerException | IllegalArgumentException ignore) {//NOPMD
                    id = Integer.parseInt(s);
                }
            } catch (Exception e) {
                throw new ISOException("unpack de48 se id, offset=" + consumed, e);
            }

            if (BINARY_ELEMENTS.contains(id) ^ isBinary) {
                throw new ISOException("unpack de48 se " + id +
                        " (offset=" + consumed + "): inconsistent binary type");
            }

            consumed += 2;

            int len;
            try {
                len = Integer.parseInt(new String(b, consumed, 2, DEF_CHARSET), 16);
            } catch (Exception e) {
                throw new ISOException("unpack de48 se " + id +//NOPMD
                        " length, offset=" + consumed + ": " + e.toString());
            }

            consumed += 2;

            if (consumed + len > b.length) {
                throw new ISOException("unpack de48 se " + id +
                        " (offset=" + consumed + "): unexpected end of de48");
            }

            ISOComponent f = isBinary ?
                    new ISOBinaryField(id, b, consumed, len) :
                    new ISOField(id, new String(b, consumed, len, isCyrillic ? ISO_8859_5 : DEF_CHARSET));

            consumed += len;

            m.set(f);
        }

        return consumed;
    }
}
