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

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class OmapiApplet extends Applet {

    final private static byte INS_BASIC_CLASS_1 = (byte) 0x06;
    final private static byte INS_BASIC_CLASS_3 = (byte) 0x0A;
    final private static byte INS_BASIC_CLASS_2 = (byte) 0x08;
    final private static byte INS_BASIC_CLASS_4 = (byte) 0x0C;

    final private static byte[] FCI_TEMPLATE = {
            (byte) 0x6F, (byte) 0x0A, (byte) 0x64, (byte) 0x03, (byte) 0x53, (byte) 0x01,
            (byte) 0x01, (byte) 0x62, (byte) 0x03, (byte) 0x85, (byte) 0x01, (byte) 0x01
    };

    private OmapiApplet() {
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        OmapiApplet applet = new OmapiApplet();
        applet.register();
    }

    public void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        if (selectingApplet()) {
            Util.arrayCopy(FCI_TEMPLATE, (short) 0, buffer, (short) 0, (short) FCI_TEMPLATE.length);
            apdu.setOutgoingAndSend((short) 0, (short) FCI_TEMPLATE.length);
            return;
        }
        byte[] output = null;
        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_BASIC_CLASS_1:
            case INS_BASIC_CLASS_3:
                if (buffer[ISO7816.OFFSET_P1] != 0x00 || buffer[ISO7816.OFFSET_P2] != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (buffer[ISO7816.OFFSET_INS] == INS_BASIC_CLASS_3
                        && apdu.setIncomingAndReceive() != 0x01) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                break;
            case INS_BASIC_CLASS_2:
            case INS_BASIC_CLASS_4:
                if (buffer[ISO7816.OFFSET_P1] != 0x00 || buffer[ISO7816.OFFSET_P2] != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (buffer[ISO7816.OFFSET_INS] == INS_BASIC_CLASS_4
                        && apdu.setIncomingAndReceive() != 0x01) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                output = new byte[256];
                Util.arrayFillNonAtomic(output, (short) 0, (short) output.length, (byte) 0x00);
                apdu.setOutgoing();
                apdu.setOutgoingLength((short) output.length);
                apdu.sendBytesLong(output, (short) 0, (short) output.length);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                break;
        }
    }
}

