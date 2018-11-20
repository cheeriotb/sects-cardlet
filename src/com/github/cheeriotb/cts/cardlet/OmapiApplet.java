/*
 *  Copyright (C) 2018 cheeriotb <cheerio.the.bear@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *  MA 02110-1301, USA.
 */

package com.github.cheeriotb.cts.cardlet;

import javacard.framework.AID;
import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class OmapiApplet extends Applet {

    private static final byte INS_BASIC_CASE_1 = (byte) 0x06;
    private static final byte INS_BASIC_CASE_3 = (byte) 0x0A;
    private static final byte INS_BASIC_CASE_2 = (byte) 0x08;
    private static final byte INS_BASIC_CASE_4 = (byte) 0x0C;

    private static final byte[] SELECT_RESPONSE_FCP = {
            (byte) 0x62, (byte) 0x1A, (byte) 0x82, (byte) 0x02, (byte) 0x38, (byte) 0x21,
            (byte) 0x83, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x8A, (byte) 0x01,
            (byte) 0x00, (byte) 0x8C, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0xC6,
            (byte) 0x09, (byte) 0x90, (byte) 0x01, (byte) 0x00, (byte) 0x83, (byte) 0x01,
            (byte) 0x00, (byte) 0x83, (byte) 0x01, (byte) 0x00
    };

    private static final byte[] SELECT_RESPONSE_FMD = {
            (byte) 0x64, (byte) 0x07, (byte) 0x53, (byte) 0x05, (byte) 0x01, (byte) 0x02,
            (byte) 0x03, (byte) 0x04, (byte) 0x05
    };

    private static final byte[] SELECT_RESPONSE_FCI_LONG = {
            (byte) 0x6F, (byte) 0x81, (byte) 0x87, (byte) 0x84, (byte) 0x10, (byte) 0x53,
            (byte) 0x6C, (byte) 0x63, (byte) 0x74, (byte) 0x52, (byte) 0x65, (byte) 0x73,
            (byte) 0x70, (byte) 0x54, (byte) 0x65, (byte) 0x73, (byte) 0x74, (byte) 0x20,
            (byte) 0x31, (byte) 0x2E, (byte) 0x30, (byte) 0xA5, (byte) 0x73, (byte) 0x65,
            (byte) 0x06, (byte) 0x07, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
            (byte) 0xFC, (byte) 0x6B, (byte) 0x01, (byte) 0x60, (byte) 0x0B, (byte) 0x06,
            (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xFC,
            (byte) 0x6B, (byte) 0x02, (byte) 0x02, (byte) 0x02, (byte) 0x63, (byte) 0x09,
            (byte) 0x06, (byte) 0x07, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
            (byte) 0xFC, (byte) 0x6B, (byte) 0x03, (byte) 0x64, (byte) 0x0B, (byte) 0x06,
            (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xFC,
            (byte) 0x6B, (byte) 0x04, (byte) 0x80, (byte) 0x00, (byte) 0x64, (byte) 0x0B,
            (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86,
            (byte) 0xFC, (byte) 0x6B, (byte) 0x04, (byte) 0x02, (byte) 0x55, (byte) 0x64,
            (byte) 0x0B, (byte) 0x06, (byte) 0x09, (byte) 0x2A, (byte) 0x86, (byte) 0x48,
            (byte) 0x86, (byte) 0xFC, (byte) 0x6B, (byte) 0x04, (byte) 0x03, (byte) 0x70,
            (byte) 0x65, (byte) 0x0D, (byte) 0x06, (byte) 0x0B, (byte) 0x2A, (byte) 0x86,
            (byte) 0x48, (byte) 0x86, (byte) 0xFC, (byte) 0x6B, (byte) 0x05, (byte) 0x04,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x66, (byte) 0x0C, (byte) 0x06,
            (byte) 0x0A, (byte) 0x2B, (byte) 0x06, (byte) 0x01, (byte) 0x04, (byte) 0x01,
            (byte) 0x2A, (byte) 0x02, (byte) 0x6E, (byte) 0x01, (byte) 0x03, (byte) 0x9F,
            (byte) 0x6E, (byte) 0x06, (byte) 0x00, (byte) 0x77, (byte) 0x60, (byte) 0x22,
            (byte) 0x01, (byte) 0x20, (byte) 0x9F, (byte) 0x65, (byte) 0x01, (byte) 0xFF
    };

    private static final byte[] SELECT_RESPONSE_FCI_SHORT = {
            (byte) 0x6F, (byte) 0x0A, (byte) 0x64, (byte) 0x03, (byte) 0x53, (byte) 0x01,
            (byte) 0x01, (byte) 0x62, (byte) 0x03, (byte) 0x85, (byte) 0x01, (byte) 0x01
    };

    private static final byte[] AID_LONG_RESPONSE = {
            (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x76, (byte) 0x41,
            (byte) 0x6E, (byte) 0x64, (byte) 0x72, (byte) 0x6F, (byte) 0x69, (byte) 0x64,
            (byte) 0x43, (byte) 0x54, (byte) 0x53, (byte) 0x32
    };

    private static AID sAidLongResponse;
    private static byte[] sResponseBuffer;

    private OmapiApplet() {
        sAidLongResponse = new AID(AID_LONG_RESPONSE, (short) 0, (byte) AID_LONG_RESPONSE.length);
        sResponseBuffer = new byte[256];
        Util.arrayFillNonAtomic(sResponseBuffer, (short) 0, (short) sResponseBuffer.length,
                (byte) 0x00);
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        OmapiApplet applet = new OmapiApplet();
        applet.register();
    }

    public void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        if (selectingApplet()) {
            byte[] response;
            switch (buffer[ISO7816.OFFSET_P2] & 0x0C) {
                // Return FCI template, optional use of FCI tag and length
                case 0x00:
                    if (sAidLongResponse.equals(buffer, ISO7816.OFFSET_CDATA,
                            buffer[ISO7816.OFFSET_LC])) {
                        response = SELECT_RESPONSE_FCI_LONG;
                    } else {
                        response = SELECT_RESPONSE_FCI_SHORT;
                    }
                    break;
                // Return FCP template, mandatory use of FCP tag and length
                case 0x04:
                    response = SELECT_RESPONSE_FCP;
                    break;
                // Return FMD template, mandatory use of FMD tag and length
                case 0x08:
                    response = SELECT_RESPONSE_FMD;
                    break;
                // No response data if Le field absent, or proprietary if Le field present
                case 0x0C:
                    return;
                default:
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                    return;
            }
            Util.arrayCopy(response, (short) 0, buffer, (short) 0, (short) response.length);
            apdu.setOutgoingAndSend((short) 0, (short) response.length);
            return;
        }
        byte[] output = null;
        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_BASIC_CASE_1:
                if (buffer[ISO7816.OFFSET_P1] != 0x00 || buffer[ISO7816.OFFSET_P2] != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                break;
            case INS_BASIC_CASE_2:
                if (buffer[ISO7816.OFFSET_P1] != 0x00 || buffer[ISO7816.OFFSET_P2] != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                apdu.setOutgoing();
                apdu.setOutgoingLength((short) sResponseBuffer.length);
                apdu.sendBytesLong(sResponseBuffer, (short) 0, (short) sResponseBuffer.length);
                break;
            case INS_BASIC_CASE_3:
                if (buffer[ISO7816.OFFSET_P1] != 0x00 || buffer[ISO7816.OFFSET_P2] != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (apdu.setIncomingAndReceive() != 0x01) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                break;
            case INS_BASIC_CASE_4:
                if (buffer[ISO7816.OFFSET_P1] != 0x00 || buffer[ISO7816.OFFSET_P2] != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (apdu.setIncomingAndReceive() != 0x01) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                apdu.setOutgoing();
                apdu.setOutgoingLength((short) sResponseBuffer.length);
                apdu.sendBytesLong(sResponseBuffer, (short) 0, (short) sResponseBuffer.length);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                break;
        }
    }
}

