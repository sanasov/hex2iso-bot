package ru.igrey.dev.domain;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;

import java.util.Arrays;

/**
 * Created by vkoba on 04.05.2017.
 */

@EqualsAndHashCode
@Slf4j
public class Bytes {
    private final byte[] bytes;
    private IsoHeaderType isoHeaderType;

    public Bytes(byte[] bytes, IsoHeaderType isoHeaderType) {
        this.bytes = bytes;
        this.isoHeaderType = isoHeaderType;
    }

    public ISOMsg toJposIsoMessage() {
        try {
            ISOMsg isoMessage = new ISOMsg();
            isoMessage.setPackager(GenericPackagerFactory.packager());
            switch (isoHeaderType) {
                case TWO_BYTES_LENGTH:
                    isoMessage.unpack(getBytesWithoutHeader(bytes));
                    break;
                case NONE:
                    isoMessage.unpack(bytes);
                    break;
                default:
                    isoMessage.unpack(bytes);
            }
            return isoMessage;
        } catch (Exception e) {
            throw new RuntimeException("byte[] to PVU message parsing error :", e);
        }
    }

    private byte[] getBytesWithoutHeader(byte[] src) {
        return Arrays.copyOfRange(src, 2, src.length);
    }
}
