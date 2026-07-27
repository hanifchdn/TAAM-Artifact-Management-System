package com.example.b07demosummer2024;

import androidx.annotation.RequiresPermission;

public interface DatabaseUpdater {

    public void updateDatabase(DatabaseItem e, WriteCallback writeCallback);
}
