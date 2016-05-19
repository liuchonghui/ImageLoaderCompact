package com.android.imageloadercompact;

import java.io.File;

public class Utils {

    public static Size getDirSize(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                Size size = new Size();
                if (children != null && children.length > 0) {
                    for (File f : children) {
                        if (f == null || !f.exists()) {
                            continue;
                        }
                        Size fsize = getDirSize(f);
                        size.setValue(size.getValue() + fsize.getValue());
                    }
                }
                return size;
            } else {
                Size size = new Size();
                size.setValue(file.length());
                return size;
            }
        } else {
            return new Size();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }
}
