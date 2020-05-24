package com.kabarakuniversityforumApp.ProfileIssues;

import android.app.Application;

import java.io.File;



public class KabarakUniversity extends Application {

    private static KabarakUniversity instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static KabarakUniversity getInstance() {
        return instance;
    }

    public void clearApplicationData() {
        getCacheDir();
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!"lib".equals(fileName)) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

}
