package android.tracking.com.trimetracker1;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.*;
import android.os.Parcelable;


public class TagReader {
    private static final String[] TECHES = new String[]{
            NfcA.class.getName(), NfcB.class.getName(), NfcF.class.getName(), NfcV.class.getName(),
            IsoDep.class.getName(), Ndef.class.getName(), NdefFormatable.class.getName(), MifareClassic.class.getName(), MifareUltralight.class.getName()};

    public static String readTag(Tag tag, Intent intent) {
        if (tag != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] tagTechList = tag.getTechList();
            if (tagTechList != null) {
                for (int i = 0; i < tagTechList.length; i++) {
                    stringBuilder.append(readTech(tag, tagTechList[i], intent));
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    private static String readTech(Tag tag, String curTagTech, Intent intent) {
        StringBuilder stringBuilder = new StringBuilder();
        if (TECHES[5].equals(curTagTech)) {
            Ndef ndefTag = Ndef.get(tag);
            Parcelable[] rawsMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawsMsgs != null) {
                NdefMessage msg = (NdefMessage) rawsMsgs[0];
                stringBuilder.append(new String(msg.getRecords()[0].getPayload()));
            }
        }
        return stringBuilder.toString();
    }

}



