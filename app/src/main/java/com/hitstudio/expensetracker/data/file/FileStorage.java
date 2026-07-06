package com.hitstudio.expensetracker.data.file;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileStorage {
    private final Context context;

    public FileStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    public InputStream openInput(Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Unable to open input stream");
        }
        return inputStream;
    }

    public OutputStream openOutput(Uri uri) throws IOException {
        OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
        if (outputStream == null) {
            throw new IOException("Unable to open output stream");
        }
        return outputStream;
    }
}
