/*
 *  Copyright (C) 2018 cheeriotb
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
import javacard.framework.ISOException;
import javacard.framework.Util;

public class OmapiApplet extends Applet {

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
        }
    }
}

