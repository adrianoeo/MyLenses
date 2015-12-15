package com.aeo.mylenses.backup;

import java.io.File;
import java.io.IOException;

import com.aeo.mylenses.activity.MainActivity;
import com.aeo.mylenses.db.DB;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;

public class MyBackupAgent extends BackupAgentHelper {

    static final String FILE_HELPER_KEY = "mylenses_file";
    
	@Override
	public void onCreate() {
//        FileBackupHelper helper = new FileBackupHelper(this, "../databases/" + DB.dbName);
        FileBackupHelper helper = new FileBackupHelper(this, DB.DB_NAME);
        addHelper(FILE_HELPER_KEY, helper);
	}
	
    /**
     * We want to ensure that the UI is not trying to rewrite the data file
     * while we're reading it for backup, so we override this method to
     * supply the necessary locking.
     */
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
             ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper performs the backup operation
        synchronized (MainActivity.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    /**
     * Adding locking around the file rewrite that happens during restore is
     * similarly straightforward.
     */
    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
            ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper restores the file from
        // the data provided here.
        synchronized (MainActivity.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
    
    @Override
    public File getFilesDir() {
    	File path = getDatabasePath(DB.DB_NAME);
        return path.getParentFile();
//        return super.getFilesDir();
    }
}
