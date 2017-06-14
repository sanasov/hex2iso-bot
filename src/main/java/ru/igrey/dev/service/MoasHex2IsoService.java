package ru.igrey.dev.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOMsg;
import ru.igrey.dev.domain.Bytes;
import ru.igrey.dev.domain.IsoHeaderType;

/**
 * Created by sanasov on 14.06.2017.
 */
@Slf4j
public class MoasHex2IsoService implements Hex2IsoService {

    @Override
    public String parse(String message) {
        try {
            message = StringUtils.deleteWhitespace(message).trim();
            ISOMsg isoMsg;
            if (hasTwoByteHeader(message)) {
                isoMsg = new Bytes(Hex.decodeHex(message.toCharArray()), IsoHeaderType.TWO_BYTES_LENGTH).toJposIsoMessage();
            } else {
                isoMsg = new Bytes(Hex.decodeHex(message.toCharArray()), IsoHeaderType.NONE).toJposIsoMessage();
            }
            return isoMsg.toString();
        } catch (Exception e) {
            log.error("Parsing error! Details", e);
            return "cannot parse this message";
        }
    }


    private boolean hasTwoByteHeader(String message) throws DecoderException {
        int expectedMessageLength = Integer.parseInt(message.trim().substring(0, 4), 16);
        return expectedMessageLength == remainingMessageLength(message);
    }

    private int remainingMessageLength(String data) throws DecoderException {
        return Hex.decodeHex(data.substring(4).toCharArray()).length;
    }


    /**
     * Эмпирический способ определение ascii - в hex слэша быть никак не может, а в ascii есть.
     * Фактически, метод говорит, что к нам пришел не hex
     */
    private boolean isAscii(String message) {
        return message.contains("\\");
    }


}
