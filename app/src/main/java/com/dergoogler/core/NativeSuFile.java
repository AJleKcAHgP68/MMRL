package com.dergoogler.core;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dergoogler.mmrl.MainActivity;
import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuFileInputStream;
import com.topjohnwu.superuser.io.SuFileOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class NativeSuFile {
    private final MainActivity ctx;
    private static final String TAG = "NativeSuFile";

    public NativeSuFile(MainActivity ctx) {
        this.ctx = ctx;
    }

    @JavascriptInterface
    public Object v2(String path) {
        SuFile file = new SuFile(path);
        return new Object() {
            @JavascriptInterface
            public void write(String data) {
                try {
                    OutputStream outputStream = SuFileOutputStream.open(file);
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                } catch (IOException e) {
                    Log.e(TAG + ":write", e.toString());
                }
            }

            @JavascriptInterface
            public String read(String def) {
                try {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(SuFileInputStream.open(file)))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                            sb.append('\n');
                        }
                        return sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG + ":read", e.toString());
                    return def;
                }
            }

            @JavascriptInterface
            public String readAsBase64() {
                try {
                    InputStream is = SuFileInputStream.open(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Base64OutputStream b64os = new Base64OutputStream(baos, Base64.DEFAULT);
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    try {
                        while ((bytesRead = is.read(buffer)) > -1) {
                            b64os.write(buffer, 0, bytesRead);
                        }
                        return baos.toString();
                    } catch (IOException e) {
                        Log.e(TAG + ":readAsBase64", e.toString());
                        return "";
                    } finally {
                        closeQuietly(is);
                        closeQuietly(b64os); // This also closes baos
                    }
                } catch (FileNotFoundException e) {
                    Log.e(TAG + ":readAsBase64", e.toString());
                    return "";
                }
            }

            private void closeQuietly(Closeable closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }

            @JavascriptInterface
            public String list(String delimiter) {
                String[] files = file.list();
                if (delimiter == null) {
                    return String.join(",", files);
                } else {
                    return String.join(delimiter, files);
                }
            }

            @JavascriptInterface
            public long lastModified() {
                return file.lastModified();
            }

            @JavascriptInterface
            public boolean create(int type) {
                return switch (type) {
                    case 0 -> file.createNewFile();
                    case 1 -> file.mkdirs();
                    case 2 -> file.mkdir();
                    default -> false;
                };
            }

            @JavascriptInterface
            public boolean delete() {
                return file.delete();
            }


            @JavascriptInterface
            public boolean deleteRecursive() {
                return file.deleteRecursive();
            }

            @JavascriptInterface
            public boolean exists() {
                return file.exists();
            }

            @JavascriptInterface
            public boolean _can_TypeMethod(int type) {
                return switch (type) {
                    case 0 -> file.canRead();
                    case 1 -> file.canWrite();
                    case 2 -> file.canExecute();
                    default -> false;
                };
            }

            @JavascriptInterface
            public boolean setExecuteWriteReadable(int type, boolean state, boolean ownerOnly) {
                return switch (type) {
                    case 0 -> file.setReadable(state, ownerOnly);
                    case 1 -> file.setWritable(state, ownerOnly);
                    case 2 -> file.setExecutable(state, ownerOnly);
                    default -> false;
                };
            }

            @JavascriptInterface
            public boolean _is_TypeMethod(int type) {
                return switch (type) {
                    case 0 -> file.isFile();
                    case 1 -> file.isSymlink();
                    case 2 -> file.isDirectory();
                    case 3 -> file.isBlock();
                    case 4 -> file.isCharacter();
                    case 5 -> file.isNamedPipe();
                    case 6 -> file.isSocket();
                    case 7 -> file.isHidden();
                    default -> false;
                };
            }

            @JavascriptInterface
            public boolean createNewSym_link(int type, String existing) {
                return switch (type) {
                    case 0 -> file.createNewLink(existing);
                    case 1 -> file.createNewSymlink(existing);
                    default -> false;
                };
            }

            @JavascriptInterface
            public int hasCode() {
                return file.hashCode();
            }
        };
    }

    @JavascriptInterface
    public String readFile(String path) {
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(SuFileInputStream.open(path)))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @JavascriptInterface
    public String listFiles(String path) {
        String[] modules = new SuFile(path).list();
        return String.join(",", modules);
    }

    @JavascriptInterface
    public boolean createFile(String path) {
        return new SuFile(path).createNewFile();
    }

    @JavascriptInterface
    public boolean deleteFile(String path) {
        return new SuFile(path).delete();
    }

    @JavascriptInterface
    public void deleteRecursive(String path) {
        new SuFile(path).deleteRecursive();
    }

    @JavascriptInterface
    public boolean existFile(String path) {
        return new SuFile(path).exists();
    }

    @JavascriptInterface
    public @Nullable String getSharedFile() {
        Intent intent = ctx.getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            return createCopyAndReturnRealPath(uri);
        } else {
            return null;
        }
    }

    public @Nullable String createCopyAndReturnRealPath(Uri uri) {
        final ContentResolver contentResolver = ctx.getContentResolver();
        if (contentResolver == null)
            return null;

        // Create file path inside app's data dir
        String filePath = ctx.getApplicationInfo().dataDir + File.separator + "cache" + File.separator
                + getFileName(uri);

        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);

            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }

}
