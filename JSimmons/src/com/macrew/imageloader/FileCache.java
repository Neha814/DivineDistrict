package com.macrew.imageloader;

import java.io.File;

import android.content.Context;

public class FileCache {
        
        private File cacheDir;
        
        /**
         * Using FileCache,
         *  we will create TTImages_cache folder for caching images into it. 
         *  Also to load image if synced already. 
         *  We can use clear() method of FileCache class to clear synced images.
         * @param context
         */
         
        public FileCache(Context context){
            //Find the dir to save cached images
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
            else
                cacheDir=context.getCacheDir();
            if(!cacheDir.exists())
                cacheDir.mkdirs();
        }
         
        public File getFile(String url){
            //I identify images by hashcode. Not a perfect solution, good for the demo.
            String filename=String.valueOf(url.hashCode());
            //Another possible solution (thanks to grantland)
            //String filename = URLEncoder.encode(url);
            File f = new File(cacheDir, filename);
            return f;
             
        }
         
        public void clear(){
            File[] files=cacheDir.listFiles();
            if(files==null)
                return;
            for(File f:files)
                f.delete();
        }
     
    }