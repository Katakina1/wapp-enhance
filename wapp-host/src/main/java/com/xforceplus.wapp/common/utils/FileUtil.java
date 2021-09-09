package com.xforceplus.wapp.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileUtil {

	protected static Logger log = Logger.getLogger(FileUtil.class);
	
	boolean flag;
	File file;
	
    /**
     * 添加内容到指定文件 如果该文件不存在，则创建并添加内容 如果该文件已存在，则添加内容到已有内容最后
     * flag为true，则向现有文件中添加内容，否则覆盖原有内容
     * @throws IOException 
      
     */
	public static void writeFile(String path, String fileName, String fileContent) throws IOException{
	        
	    	if (null == fileContent || true == "".equals(fileContent))
	            return;
	    	
	    	if(false == directoryIsExists(path)) 
	    		return;
	    	
	        FileWriter writer = null;
	        try{
	        	writer = new FileWriter(new File(path + fileName));
	        	writer.write(fileContent);
	        	writer.flush();
	        }finally{
	        	if(null != writer)
	        		try{
	        			writer.close();
	        		}catch(IOException e){
	        			e.printStackTrace();
	        		}
	        }
	    }
    
    public static boolean directoryIsExists(String path){
    	File directory = new File(path);
    	if(true == directory.exists())
    		return true;
    	
        if(false == directory.mkdir())
        	return false;
        
        if(true == directory.canWrite())
        	return true;
        
        return directory.setWritable(true);
    }
    
    /** 
	 *  根据路径删除指定的目录或文件，无论存在与否 
	 *@param sPath  要删除的目录或文件 
	 *@return 删除成功返回 true，否则返回 false。 
	 */  
	public boolean DeleteFolder(String sPath) {  
	    flag = false;  
	    file = new File(sPath);  
	    // 判断目录或文件是否存在  
	    if (!file.exists()) {  // 不存在返回 false  
	        return flag;  
	    } else {  
	        // 判断是否为文件  
	        if (file.isFile()) {  // 为文件时调用删除文件方法  
	            return deleteFile(sPath);  
	        } else {  // 为目录时调用删除目录方法  
	            return deleteDirectory(sPath);  
	        }  
	    }  
	} 
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public boolean deleteFile(String sPath) {  
	    flag = false;  
	    file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (null != sPath) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  
	
	/** 
	 * 删除目录（文件夹）以及目录下的文件 
	 * @param   sPath 被删除目录的文件路径 
	 * @return  目录删除成功返回true，否则返回false 
	 */  
	public boolean deleteDirectory(String sPath) {  
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //删除当前目录  
        return dirFile.delete();
	} 
	public static void main(String[] args) {
		FileUtil fileUtil = new FileUtil();
		fileUtil.deleteFile("D:/img1.png");
	}
}
