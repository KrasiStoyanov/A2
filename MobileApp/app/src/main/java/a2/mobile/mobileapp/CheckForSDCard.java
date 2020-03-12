package a2.mobile.mobileapp;

import android.os.Environment;

public class CheckForSDCard {
    //Check If SD Card is present or not method
    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public boolean canWriteOnSDCard() {
        return Environment.getExternalStorageDirectory().canWrite();
    }
}