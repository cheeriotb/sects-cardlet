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
    private static final short DATA_BUFFER_SIZE = 0x100;

    private static final byte INS_BASIC_CASE_1 = (byte) 0x06;
    private static final byte INS_BASIC_CASE_3 = (byte) 0x0A;
    private static final byte INS_BASIC_CASE_2 = (byte) 0x08;
    private static final byte INS_BASIC_CASE_4 = (byte) 0x0C;

    private static final byte INS_SEGMENTED_CASE2_FF_A = (byte) 0xC2;
    private static final byte INS_SEGMENTED_CASE2_FF_B = (byte) 0xCF;
    private static final byte INS_SEGMENTED_CASE2_00_A = (byte) 0xC6;
    private static final byte INS_SEGMENTED_CASE4_FF = (byte) 0xC4;
    private static final byte INS_SEGMENTED_CASE4_00 = (byte) 0xC8;

    private static final byte INS_WARNING_SW = (byte) 0xF3;

    private static final byte P2_WARNING_SW_CASE1 = (byte) 0x06;
    private static final byte P2_WARNING_SW_CASE2 = (byte) 0x08;
    private static final byte P2_WARNING_SW_CASE3 = (byte) 0x0A;
    private static final byte P2_WARNING_SW_CASE4 = (byte) 0x0C;

    private static final byte INS_CHECCK_P2 = (byte) 0xF4;

    private static final byte INS_GET_RESPONSE = (byte) 0xC0;

    private static final short SEGMENT_00 = DATA_BUFFER_SIZE;
    private static final short SEGMENT_FF = (short) 0x00FF;

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

    private static final byte[] SPECIAL_CASE4_APDU = {
            (byte) 0x01, (byte) 0xF3, (byte) 0x00, (byte) 0x0C, (byte) 0x01, (byte) 0xAA,
            (byte) 0x00
    };

    private static final short[] WARNING_SWS = {
            (short) 0x6200, (short) 0x6281, (short) 0x6282, (short) 0x6283,
            (short) 0x6285, (short) 0x62F1, (short) 0x62F2, (short) 0x63F1,
            (short) 0x63F2, (short) 0x63C2, (short) 0x6202, (short) 0x6280,
            (short) 0x6284, (short) 0x6286, (short) 0x6300, (short) 0x6381
    };

    private static AID sAidLongResponse;
    private static byte[] sOutgoingData;

    private short mSegmentSize = 0;
    private short mRemainingSize = 0;
    private byte mCurrentClass = 0x00;
    private byte mWarningP1 = 0x00;
    private byte mSelectP2 = 0x00;

    private OmapiApplet() {
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        OmapiApplet applet = new OmapiApplet();
        applet.register();

        sAidLongResponse = new AID(AID_LONG_RESPONSE, (short) 0, (byte) AID_LONG_RESPONSE.length);
        sOutgoingData = new byte[DATA_BUFFER_SIZE];
        Util.arrayFillNonAtomic(sOutgoingData, (short) 0, DATA_BUFFER_SIZE, (byte) 0x00);
        sOutgoingData[DATA_BUFFER_SIZE - 1] = (byte) 0xFF;
    }

    public void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        byte ins = buffer[ISO7816.OFFSET_INS];
        byte cla = buffer[ISO7816.OFFSET_CLA];
        byte p1 = buffer[ISO7816.OFFSET_P1];
        byte p2 = buffer[ISO7816.OFFSET_P2];

        if (selectingApplet()) {
            byte[] response;

            mSelectP2 = p2;

            switch (p2 & 0x0C) {
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

        if (ins != INS_GET_RESPONSE) {
            clearOutgoingData();
        }

        switch (ins) {
            case INS_BASIC_CASE_1:
                if (p1 != 0x00 || p2 != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                break;

            case INS_BASIC_CASE_2:
                if (p1 != 0x00 || p2 != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                processOutgoingCase2(apdu, cla, DATA_BUFFER_SIZE, buffer[ISO7816.OFFSET_LC],
                        SEGMENT_00);
                break;

            case INS_BASIC_CASE_3:
                if (p1 != 0x00 || p2 != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (apdu.setIncomingAndReceive() != 0x01) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                break;

            case INS_BASIC_CASE_4:
                if (p1 != 0x00 || p2 != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (apdu.setIncomingAndReceive() != 0x01) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                processOutgoingCase4(cla, DATA_BUFFER_SIZE, SEGMENT_00);
                break;

            case INS_SEGMENTED_CASE2_FF_A:
            case INS_SEGMENTED_CASE2_FF_B:
                processOutgoingCase2(apdu, cla, Util.getShort(buffer, (short) ISO7816.OFFSET_P1),
                        buffer[ISO7816.OFFSET_LC], SEGMENT_FF);
                break;

            case INS_SEGMENTED_CASE2_00_A:
                processOutgoingCase2(apdu, cla, Util.getShort(buffer, (short) ISO7816.OFFSET_P1),
                        buffer[ISO7816.OFFSET_LC], SEGMENT_00);
                break;

            case INS_SEGMENTED_CASE4_FF:
                if (apdu.setIncomingAndReceive() != 0x02) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                processOutgoingCase4(cla, Util.getShort(buffer, (short) ISO7816.OFFSET_P1),
                        SEGMENT_FF);
                break;

            case INS_SEGMENTED_CASE4_00:
                if (apdu.setIncomingAndReceive() != 0x02) {
                    ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                }
                processOutgoingCase4(cla, Util.getShort(buffer, (short) ISO7816.OFFSET_P1),
                        SEGMENT_00);
                break;

            case INS_WARNING_SW:
                if (p1 < 1 || p1 > WARNING_SWS.length) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                switch (p2) {
                    case P2_WARNING_SW_CASE1:
                        ISOException.throwIt(WARNING_SWS[p1 - 1]);
                        break;
                    case P2_WARNING_SW_CASE2:
                        sendShortData(apdu, buffer[ISO7816.OFFSET_LC]);
                        ISOException.throwIt(WARNING_SWS[p1 - 1]);
                        break;
                    case P2_WARNING_SW_CASE3:
                        if (apdu.setIncomingAndReceive() != 0x01) {
                            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                        }
                        ISOException.throwIt(WARNING_SWS[p1 - 1]);
                        break;
                    case P2_WARNING_SW_CASE4:
                        if (apdu.setIncomingAndReceive() != 0x01) {
                            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
                        }
                        mCurrentClass = cla;
                        mWarningP1 = p1;
                        // Return SW 61xx for now as Le is unknown.
                        ISOException.throwIt((short) (ISO7816.SW_BYTES_REMAINING_00
                                + SPECIAL_CASE4_APDU.length));
                        break;
                    default:
                        ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                        break;
                }
                break;

            case INS_CHECCK_P2:
                if (p1 != 0x00 || p2 != 0x00) {
                    ISOException.throwIt(ISO7816.SW_WRONG_P1P2);
                }
                if (buffer[ISO7816.OFFSET_LC] == 0x01) {
                    buffer[0] = mSelectP2;
                    apdu.setOutgoingAndSend((short) 0, (short) 1);
                } else {
                    // Return SW 6C01 if Le is bigger than the the actual outgoing data.
                    ISOException.throwIt((short) (ISO7816.SW_CORRECT_LENGTH_00 + 0x01));
                }
                break;

            case INS_GET_RESPONSE:
                if (cla != mCurrentClass) {
                    ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
                }
                if (mRemainingSize != 0) {
                    processOutgoingCase2(apdu, cla, mRemainingSize, buffer[ISO7816.OFFSET_LC],
                            mSegmentSize);
                }
                if (mWarningP1 != 0x00) {
                    short length = (short) buffer[ISO7816.OFFSET_LC];

                    Util.arrayCopy(SPECIAL_CASE4_APDU, (short) 0, buffer, (short) 0,
                            (short) SPECIAL_CASE4_APDU.length);
                    buffer[ISO7816.OFFSET_P1] = mWarningP1;

                    apdu.setOutgoingAndSend((short) 0, (length != 0x00) ? length
                            : DATA_BUFFER_SIZE);

                    clearOutgoingData();
                    ISOException.throwIt(WARNING_SWS[buffer[ISO7816.OFFSET_P1] - 1]);
                }
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                break;
        }
    }

    private void processOutgoingCase4(byte cla, short total, short segment)
            throws ISOException {
        mSegmentSize = segment;
        mRemainingSize = total;
        mCurrentClass = cla;

        short available = (segment < total) ? segment : total;
        // Return SW 61xx for now as Le is unknown.
        ISOException.throwIt((short) (ISO7816.SW_BYTES_REMAINING_00
                + ((available > 0xFF) ? 0x00 : available)));
    }

    private void processOutgoingCase2(APDU apdu, byte cla, short total, short expected,
            short segment) throws ISOException {
        short available = (segment < total) ? segment : total;
        expected = (expected != 0x00) ? expected : DATA_BUFFER_SIZE;

        if (expected > available) {
            // Return SW 6Cxx if Le is bigger than the the actual outgoing data.
            ISOException.throwIt((short) (ISO7816.SW_CORRECT_LENGTH_00
                    + ((available > 0xFF) ? 0x00 : available)));
        }

        apdu.setOutgoing();
        apdu.setOutgoingLength(available);
        apdu.sendBytesLong(sOutgoingData, (short) (DATA_BUFFER_SIZE - available), available);

        if ((total -= available) > 0) {
            mSegmentSize = segment;
            mRemainingSize = total;
            mCurrentClass = cla;

            expected = (total > segment) ? segment : total;
            // Return SW 61xx if remaining outgoing data exists after sending outgoing data.
            ISOException.throwIt((short) (ISO7816.SW_BYTES_REMAINING_00
                    + ((expected > 0xFF) ? (short) 0x00 : expected)));
        } else {
            clearOutgoingData();
        }
    }

    private void sendShortData(APDU apdu, short expected) {
        if (expected == 0x00) {
            expected = DATA_BUFFER_SIZE;
        }
        apdu.setOutgoing();
        apdu.setOutgoingLength(expected);
        apdu.sendBytesLong(sOutgoingData, (short) (DATA_BUFFER_SIZE - expected), expected);
    }

    private void clearOutgoingData() {
        if (mRemainingSize != 0) {
            mRemainingSize = 0;
        }
        if (mWarningP1 != 0x00) {
            mWarningP1 = 0x00;
        }
        if (mCurrentClass != 0x00) {
            mCurrentClass = 0x00;
        }
    }
}

