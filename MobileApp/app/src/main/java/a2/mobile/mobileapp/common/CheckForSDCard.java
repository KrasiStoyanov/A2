package a2.mobile.mobileapp.common;

import android.os.Environment;

public class CheckForSDCard {
    //Check If SD Card is present or not method
    public boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED);
    }

    public boolean canWriteOnSDCard() {
        return Environment.getExternalStorageDirectory().canWrite();
    }
}