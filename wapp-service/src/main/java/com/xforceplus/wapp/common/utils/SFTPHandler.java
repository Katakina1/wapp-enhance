/**
 * 
 */
package com.xforceplus.wapp.common.utils;

import com.xforceplus.wapp.config.SystemConfig;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

//import org.apache.log4j.Logger;

/**
 * <p>Title:  SFTPHandler</p>
 * <p>Description: sftp链接工具类</p> 
 * @author yuanlz
 * @date 2016年12月21日 下午4:31:19 
 */
public class SFTPHandler {
	/**
	 * sftp操作对象
	 */
	private ChannelSftp sftp = null;
	
	/**
	 * ssh 会话
	 */
	private Session session = null;
	
	/**
	 * sftp 通道
	 */
    private Channel channel = null;
    
    /**
     * 本地图片路径
     */
    private String localImageRootPath;
    /**
     * 远程主机报文备份路径
     */
    private String remoteImageRootPath;
	/**
	 * 远程服务器费用模块上传文件路径
	 */
	private String remoteCostFileRootPath;

	private String privateKey="/home/svcwapptomcat/.ssh/id_rsa";//密钥文件路径
	private String passphrase="";//密钥口令
	private static final Logger logger = Logger.getLogger(SFTPHandler.class);
	
    /**
     * <p>Description: 私有构造函数，通过 本地报文路径 远程主机报文路径 本地报文备份路径 远程主机报文备份路径 创建对象</p> 
     * @param remoteImageRootPath 远程主机根路径
     */
	private SFTPHandler(String remoteImageRootPath,String localImageRootPath){
        this.remoteImageRootPath = remoteImageRootPath;
		this.localImageRootPath = localImageRootPath;
	}

	private SFTPHandler(String remoteCostFileRootPath){
		this.remoteCostFileRootPath = remoteCostFileRootPath;
	}
	/**
	 * <p>Description: 对外暴露的方法，可以通过 本地报文路径 远程主机报文路径 本地报文备份路径 远程主机报文备份路径 获取handler对象</p> 
	 * @author yuanlz
	 * @date 2016年12月21日 下午10:43:40
	 *  localPath 本地报文路径
     *  remotePath 远程主机报文路径
     *  localBakPath 本地报文备份路径
     *  remoteBakPath 远程主机报文备份路径
     * @return SFTPHandler SFTP操作对象handler
	 */
	public static SFTPHandler getHandler(String remoteImageRootPath,String localImageRootPath){
		return new SFTPHandler(remoteImageRootPath,localImageRootPath);
	}

	public static SFTPHandler getHandler(String remoteCostFileRootPath){
		return new SFTPHandler(remoteCostFileRootPath);
	}

	/**
	 * <p>Description: 打开SFTP通道</p> 
	 * @author yuanlz
	 * @date 2016年12月21日 下午7:03:40
	 * @param host 主机地址
	 * @param user 用户名
	 * @param pwd 密码
	 * @param port 端口号
	 * @param timeout 超时时间
	 * @throws JSchException 
	 */
	//密码链接方式（跑本地环境）
//    public void openChannel(String host, String user, String pwd, int port, int timeout) throws JSchException{
//
//        if(null != this.session && this.session.isConnected()
//        		|| null != this.channel && this.channel.isConnected()
//        		|| null != this.sftp && this.sftp.isConnected()){
//        	this.closeChannel();
//        }
//
//        // 初始化端口
//    	if(0 > port){
//    		port = 22;
//    	}
//
//    	// 创建JSch对象
//        JSch jsch = new JSch();
//
////        jsch.addIdentity("vn088jh","Fth123456");
//        // 通过 用户名，主机地址，端口 获取一个Session对象
//        this.session = jsch.getSession(user, host, port);
//        //SFTPHandler.logger.info("SFTPHandler : Session Created");
//
//        // 设置密码
//        if (pwd != null) {
//	        	this.session.setPassword(pwd);
//        }
//        // 为Session对象设置properties
//        Properties config = new Properties();
//        config.put("StrictHostKeyChecking", "no");
//        config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
//        this.session.setConfig(config);
//
//        // 设置超时时间
//        this.session.setTimeout(timeout);
//
//        // 建立链接
//        this.session.connect();
//        //SFTPHandler.logger.info("SFTPHandler : Session Connected.");
//
//        // 打开SFTP通道
//        this.channel = this.session.openChannel("sftp");
//        //SFTPHandler.logger.info("SFTPHandler : Channel Opened.");
//
//        // 建立SFTP通道的连接
//        this.channel.connect();
//        //SFTPHandler.logger.info("SFTPHandler : Channel Connected.");
//        this.sftp = (ChannelSftp) this.channel;
//
//        //SFTPHandler.logger.info("SFTPHandler : Handler Created.");
//    }
    //密钥链接方式
	public void openChannel(String host, String user, String pwd, int port, int timeout) throws JSchException{

		if(null != this.session && this.session.isConnected()
				|| null != this.channel && this.channel.isConnected()
				|| null != this.sftp && this.sftp.isConnected()){
			this.closeChannel();
		}
		// 初始化端口
		if(0 > port){
			port = 22;
		}

		// 创建JSch对象
		JSch jsch = new JSch();

		if (privateKey != null && !"".equals(privateKey)) {
			//使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
			if (passphrase != null && "".equals(passphrase)) {
				jsch.addIdentity(privateKey, passphrase);
			} else {
				jsch.addIdentity(privateKey);
			}
		}else{
			logger.info("密钥路径初始化失败");
		}
//        jsch.addIdentity("vn088jh","Fth123456");
		// 通过 用户名，主机地址，端口 获取一个Session对象
		this.session = jsch.getSession(user, host, port);
		//SFTPHandler.logger.info("SFTPHandler : Session Created");

		// 设置密码
//        if (pwd != null) {
//	        	this.session.setPassword(pwd);
//        }
		// 为Session对象设置properties
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications","publickey,keyboard-interactive,password");
		this.session.setConfig(config);

		// 设置超时时间
		this.session.setTimeout(timeout);

		// 建立链接
		this.session.connect();
		//SFTPHandler.logger.info("SFTPHandler : Session Connected.");

		// 打开SFTP通道
		this.channel = this.session.openChannel("sftp");
		//SFTPHandler.logger.info("SFTPHandler : Channel Opened.");

		// 建立SFTP通道的连接
		this.channel.connect();
		//SFTPHandler.logger.info("SFTPHandler : Channel Connected.");
		this.sftp = (ChannelSftp) this.channel;

		//SFTPHandler.logger.info("SFTPHandler : Handler Created.");
	}
    /**
     * <p>Description: 关闭SFTP通道</p> 
     * @author yuanlz
     * @date 2016年12月21日 下午7:05:21
     */
    public void closeChannel(){
        if(null != this.channel) {
        	this.channel.disconnect();
        	//SFTPHandler.logger.info("SFTPHandler : Channel Closed.");
        }
        if(null != this.session) {
        	this.session.disconnect();
        	//SFTPHandler.logger.info("SFTPHandler : Session Closed.");
        }
        if(null != this.sftp){
        	this.sftp.disconnect();
        	//SFTPHandler.logger.info("SFTPHandler : Handler Closed.");
        }
    }
    
    /**
     * <p>Description: 返回SFTP指定目录下所有文件名</p> 
     * @author yuanlz
     * @date 2016年12月21日 下午7:12:28
     * @throws JSchException
     * @throws SftpException
     */
    public List<String> getFileNameList() throws JSchException, SftpException {
		
    	List<String> fileNamelist = new ArrayList<String>();
		
    	if(null == this.sftp || this.sftp.isClosed()){
    		//SFTPHandler.logger.error("SFTPHandler : Handler Missed.");
			throw new JSchException("链接丢失");
		}
    	
    	// 进入目录
    	try{
    		this.sftp.cd(this.remoteImageRootPath);
    	}catch(SftpException e){
		    if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){ //TODO 进入根目录了还未进入税号文件夹+年份文件夹
		    	this.sftp.mkdir(this.remoteImageRootPath);
			    this.sftp.cd(this.remoteImageRootPath);
		    }else{
		    	throw e;
		    }
    	}
    	
    	//SFTPHandler.logger.info("SFTPHandler : Opened Directory " + sftpDirectory);
    	
    	// 根据名称关键字查询所有文件名
		@SuppressWarnings("unchecked")
		Vector<ChannelSftp.LsEntry> vector = this.sftp.ls(this.remoteImageRootPath);
		for (LsEntry lsEntry : vector) {
			if(lsEntry.getFilename().toLowerCase().endsWith(".csv")) {
				fileNamelist.add(lsEntry.getFilename());
			}
			//SFTPHandler.logger.info("SFTPHandler : Get File Named " + lsEntry.getFilename());
        }
    	
		return fileNamelist;
	}
    /**
     * <p>Description: 获取本地文件名列表</p> 
     * @author yuanlz
     * @date 2016年12月22日 下午3:52:55
     * @param keyName 文件名关键字
     * @throws FileNotFoundException 
     */
    public List<String> getLocalFileNameList(final String keyName) throws FileNotFoundException{
    	List<String> fileNamelist = new ArrayList<String>();
    	File directory = new File(this.localImageRootPath); 
    	// 文件夹是否存在
        if(false == directory.exists()){
        	// 创建文件夹
        	if(false == directory.mkdir()){
        		throw new FileNotFoundException();
        	}
        	
        	// 可写
        	if(false == directory.canWrite()){
        		if(false == directory.setWritable(true)){
        			throw new FileNotFoundException();
        		}
        	}
        }
        String [] fileNames = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
                return name.contains(keyName);
            }
		});
        fileNamelist.addAll(Arrays.asList(fileNames));
    	return fileNamelist;
    }
    
    /**
     * 下载文件
     * @param downloadPath 远程文件路径
     * @param fileName 下载文件名
     * @throws SftpException
     */
	public void download(String downloadPath, String fileName)
			throws SftpException {
		try {
			String pathArry[] = downloadPath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (int i = 0; i < pathArry.length - 1; i++) {
				if (pathArry[i].equals("")) {
					continue;
				}
				filePath.append(pathArry[i] + "/");
				if (isDirExist(filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}
			}
			File directory = new File(this.localImageRootPath);
			// 文件夹是否存在
			if (false == directory.exists()) {
				// 创建文件夹
				if (false == directory.mkdirs()) {
					throw new FileNotFoundException();
				}

				// 可写
				if (false == directory.canWrite()) {
					if (false == directory.setWritable(true)) {
						throw new FileNotFoundException();
					}
				}
			}
			// 下载
			File file = new File(this.localImageRootPath + fileName);
			FileOutputStream out = new FileOutputStream(file);
			this.sftp.get(downloadPath, out);
			if (null != out)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 下载文件 该方法返回一个输入流，该输入流含有目标服务器上文件名为downloadPath的文件数据。可以从该输入流中读取数据，最终将数据传输到本地
	 * @param downloadPath 远程文件路径
	 * @throws SftpException
	 */
	public InputStream download(String downloadPath)
			throws SftpException, IOException {
			String pathArry[] = downloadPath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (int i = 0; i < pathArry.length - 1; i++) {
				if (pathArry[i].equals("")) {
					continue;
				}
				filePath.append(pathArry[i] + "/");
				if (isDirExist(filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}
			}
			// 下载
		InputStream is =this.sftp.get(downloadPath);



		return is;
	}
    /**
     * <p>Description: 上传文件</p> 
     * @author yuanlz
     * @date 2016年12月21日 下午10:01:26
     * @param pdfFileName
     * @param uploadFile 上传的文件名
     * @throws SftpException
     * @throws FileNotFoundException 
     */
    public void upload(String pdfFileName, String uploadFile) throws SftpException, FileNotFoundException{
    	try{
    		this.sftp.cd(this.remoteImageRootPath);
    	}catch(SftpException e){
		    if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){
		    	this.sftp.mkdir(this.remoteImageRootPath);
			    this.sftp.cd(this.remoteImageRootPath);
		    }else{
		    	throw e;
		    }
    	}
    	File file = new File(this.remoteImageRootPath + uploadFile);
    	FileInputStream in = new FileInputStream(file);
    	this.sftp.put(in, uploadFile); 
    	if(null != in)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }

	public void uploadTzd(File file, String uploadFile,String path) throws SftpException, FileNotFoundException{
		try{
			this.sftp.cd(path);
		}catch(SftpException e){
			if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){
				this.sftp.mkdir(this.remoteImageRootPath);
				this.sftp.cd(this.remoteImageRootPath);
			}else{
				throw e;
			}
		}
		FileInputStream in = new FileInputStream(file);
		this.sftp.put(in, uploadFile);
		if(null != in)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public void uploadExcel(InputStream in, String uploadFile,String path) throws SftpException, FileNotFoundException{
		try{
			this.sftp.cd(path);
		}catch(SftpException e){
			if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){
				this.sftp.mkdir(this.remoteImageRootPath);
				this.sftp.cd(this.remoteImageRootPath);
			}else{
				throw e;
			}
		}
		this.sftp.put(in, uploadFile);
		if(null != in)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public void upload(File file, String uploadFile) throws SftpException, FileNotFoundException{
		try{
			this.sftp.cd(this.remoteImageRootPath);
		}catch(SftpException e){
			if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){
				this.sftp.mkdir(this.remoteImageRootPath);
				this.sftp.cd(this.remoteImageRootPath);
			}else{
				throw e;
			}
		}
		FileInputStream in = new FileInputStream(file);
		this.sftp.put(in, uploadFile);
		if(null != in)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
     * 上传文件
     * @param gfTaxNo 购方税号
     * @param pathDate 当前年份
     * @param uploadFile  文件名
     * @return
     * @throws SftpException 
     * @throws FileNotFoundException
     */
    public String uploadImg(String gfTaxNo,String pathDate,String uploadFile) throws SftpException, FileNotFoundException{ 
    	String path = null;
    	try{
    		path =this.remoteImageRootPath+gfTaxNo+"/"+pathDate+"/";
    		this.sftp.cd(path);
    	}catch(SftpException e){
    		if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){
    			try {
					createDir(path, sftp);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
    		}else{
    			throw e;
    		}
    	}
    	File file = new File(this.localImageRootPath + uploadFile);
    	FileInputStream in = new FileInputStream(file);
    	this.sftp.put(in, uploadFile); 
    	if(null != in)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return path;
    }
    
    public String uploadScanImage(String pathDate,String uploadFile) throws SftpException, FileNotFoundException{ 
    	String path = null;

		Date scanDate = new Date();
		String years = new SimpleDateFormat("yyyy").format(scanDate);
		String month = new SimpleDateFormat("MM").format(scanDate);
		String day = new SimpleDateFormat("dd").format(scanDate);


    	path =this.remoteImageRootPath+years+"/"+month+"/"+day+"/";
    	try{
    		this.sftp.cd(path);
    	}catch(SftpException e){
    		if(ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id){
    			try {
					createDir(path, sftp);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
    		}else{
    			throw e;
    		}
    	}
    	File file = new File(this.localImageRootPath + uploadFile);
    	FileInputStream in = new FileInputStream(file);
    	this.sftp.put(in, uploadFile); 
    	if(null != in)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return path;
    }

	public String getRemoteImageRootPath() {
		return remoteImageRootPath;
	}

	public void setRemoteImageRootPath(String remoteImageRootPath) {
		this.remoteImageRootPath = remoteImageRootPath;
	}
	
	
	

    /**
     * <p>Description: 删除远程文件</p> 
     * @author yuanlz
     * @date 2016年12月21日 下午10:02:22
     *  bakFile 删除远程文件的文件名
     */
    public void deleteRemote(String deletePath) throws SftpException {
    	try{
    		String pathArry[] = deletePath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (int i = 0; i < pathArry.length - 1; i++) {
				if (pathArry[i].equals("")) {
					continue;
				}
				filePath.append(pathArry[i] + "/");
				if (isDirExist(filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}
			}
			this.sftp.rm(deletePath);
    	}catch(SftpException e){
		   throw e;
    	}
//    	FileUtil fu = new FileUtil();
//        fu.deleteFile(deletePath);
    } 
    
    /**
     * <p>Description: 备份本地</p> 
     * @author yuanlz
     * @date 2016年12月21日 下午10:33:47
     *  bakFile 本地备份的文件名
     * @throws SftpException
     * @throws IOException 
     */
   /* public void bakupLocal(String bakFile) throws SftpException, IOException {  
    	File directory = new File(this.localBakPath); 
    	//logger.error("++++++++++++++++++++++"+localBakPath);
        // 文件夹是否存在
        if(false == directory.exists()){
        	// 创建文件夹
        	if(false == directory.mkdir()){
        		throw new FileNotFoundException();
        	}
        	
        	// 可写
        	if(false == directory.canWrite()){
        		if(false == directory.setWritable(true)){
        			throw new FileNotFoundException();
        		}
        	}
        }
        FileUtil fu = new FileUtil();
        fu.deleteFile(this.localBakPath+bakFile);
        FileUtils.moveFile(new File(this.localPath + bakFile), new File(this.localBakPath + bakFile));
    }*/

	
	public String getLocalImageRootPath() {
		return localImageRootPath;
	}

	public void setLocalImageRootPath(String localImageRootPath) {
		this.localImageRootPath = localImageRootPath;
	}

	/** 
	  * 判断目录是否存在 
	  */  
	 public boolean isDirExist(String directory) {  
	  boolean isDirExistFlag = false;  
	  try {  
	   SftpATTRS sftpATTRS = sftp.lstat(directory);  
	   isDirExistFlag = true;  
	   return sftpATTRS.isDir();  
	  } catch (Exception e) {  
	   if (e.getMessage().toLowerCase().equals("no such file")) {  
	    isDirExistFlag = false;  
	   }  
	  }  
	  return isDirExistFlag;  
	 }  
	
	 /** 
	  * 创建一个文件目录 
	 * @throws Exception 
	  */  
	 public void createDir(String createpath, ChannelSftp sftp) throws Exception {  
	  try {  
	   if (isDirExist(createpath)) {  
	    this.sftp.cd(createpath);  
	   }  
	   String pathArry[] = createpath.split("/");  
	   StringBuffer filePath = new StringBuffer("/");  
	   for (String path : pathArry) {  
	    if (path.equals("")) {  
	     continue;  
	    }  
	    filePath.append(path + "/");  
	    if (isDirExist(filePath.toString())) {  
	     sftp.cd(filePath.toString());  
	    } else {  
	     // 建立目录  
	     sftp.mkdir(filePath.toString());  
	     // 进入并设置为当前目录  
	     sftp.cd(filePath.toString());  
	    }  
	   }  
	   this.sftp.cd(createpath);  
	  } catch (SftpException e) {  
	   throw new Exception("创建路径错误：" + createpath);  
	  }  
	 }  
	
	
	/*public static void main(String[] args) {
		SFTPHandler collectHandler = SFTPHandler.getHandler(
				local FilePathConstanEnum.LOCAL_VAT_S_PATH.value(), 
				remote FilePathConstanEnum.REMOTE_VAT_S_WORK_C_PATH.value(), 
				remote FilePathConstanEnum.REMOTE_VAT_S_WORK_D_PATH.value(), 
				local bak FilePathConstanEnum.LOCAL_VAT_S_BAK_PATH.value(), 
				remote bak FilePathConstanEnum.REMOTE_VAT_S_BAK_PATH.value());
		try {
			collectHandler.openChannel(
					SFTPConstanEnum.HOST.value(), 
					SFTPConstanEnum.USERNAME.value(), 
					SFTPConstanEnum.PASSWORD.value(),
					Integer.parseInt(SFTPConstanEnum.DEFAULT_PORT.value()), 
					Integer.parseInt(SFTPConstanEnum.DEFAULT_TIMEOUT.value()));
		} catch (NumberFormatException | JSchException e) {
			e.printStackTrace();
		}finally{
			collectHandler.closeChannel();
		}
	}*/


	/**
	 * 上传至临时文件目录
	 * @param file
	 * @return
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public String upload(MultipartFile file) throws SftpException, FileNotFoundException {
		try {
			this.sftp.cd(this.remoteCostFileRootPath);
		} catch (SftpException e) {
			if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
				this.sftp.mkdir(this.remoteCostFileRootPath);
				this.sftp.cd(this.remoteCostFileRootPath);
			} else {
				throw e;
			}
		}
		//生成时间戳作为服务器上的文件名
		String prefix = "" + new Date().getTime();
		String fileName = file.getOriginalFilename();
		String suffix = "";
		int index = fileName.indexOf('.');
		if (index > -1) {
			suffix = fileName.substring(index);
		}
		FileInputStream in = null;
		try {
			in = (FileInputStream)file.getInputStream();
		} catch (Exception e){
			e.printStackTrace();
		}
		this.sftp.put(in, prefix + suffix);
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.remoteCostFileRootPath+prefix+suffix;
	}

	/**
	 * 上传至临时文件目录
	 * @param file
	 * @return
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public String uploadRed(MultipartFile file) throws SftpException, FileNotFoundException {
		try {
			this.sftp.cd(this.remoteCostFileRootPath);
		} catch (SftpException e) {
			if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
				this.sftp.mkdir(this.remoteCostFileRootPath);
				this.sftp.cd(this.remoteCostFileRootPath);
			} else {
				throw e;
			}
		}
		//生成时间戳作为服务器上的文件名
		String prefix = "" + new Date().getTime();
		String fileName = file.getOriginalFilename();
		String suffix = "";
		int index = fileName.indexOf('.');
		if (index > -1) {
			suffix = fileName.substring(index);
		}
		FileInputStream in = null;
		try {
			in = (FileInputStream)file.getInputStream();
		} catch (Exception e){
			e.printStackTrace();
		}
		this.sftp.put(in, fileName);
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.remoteCostFileRootPath+fileName;
	}

	/**
	 * 上传至时间文件目录
	 * @param file
	 * @return
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public String uploadDate(MultipartFile file) throws Exception {
		Date scanDate = new Date();
		String years = new SimpleDateFormat("yyyy").format(scanDate);
		String month = new SimpleDateFormat("MM").format(scanDate);
		String day = new SimpleDateFormat("dd").format(scanDate);


		String path =this.remoteCostFileRootPath+years+"/"+month+"/"+day+"/";
		try {
			String pathArry[] = path.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (String path1 : pathArry) {
				if (path1.equals("")) {
					continue;
				}
				filePath.append(path1 + "/");
				if (isDirExist(filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}
			}
		}catch (SftpException e) {
			throw new Exception("创建路径错误：" + path);
		}

		//生成时间戳作为服务器上的文件名
		String prefix = "" + new Date().getTime();
		String fileName = file.getOriginalFilename();
		String suffix = "";
		int index = fileName.indexOf('.');
		if (index > -1) {
			suffix = fileName.substring(index);
		}
		FileInputStream in = null;
		try {
			in = (FileInputStream)file.getInputStream();
		} catch (Exception e){
			e.printStackTrace();
		}
		this.sftp.put(in, prefix + suffix);
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return path+prefix+suffix;
	}

	/**
	 * 上传至临时文件目录
	 * @param file
	 * @param tempPath 临时文件路径
	 * @return
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public String upload(MultipartFile file,String tempPath) throws SftpException, FileNotFoundException {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String now = formatter.format(new Date());
			this.sftp.cd(this.remoteCostFileRootPath+now+"/");
			this.remoteCostFileRootPath=this.remoteCostFileRootPath+now+"/";
		} catch (SftpException e) {
			if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String now = formatter.format(new Date());
				this.sftp.mkdir(this.remoteCostFileRootPath+now+"/");
				this.sftp.cd(this.remoteCostFileRootPath+now+"/");
				this.remoteCostFileRootPath=this.remoteCostFileRootPath+now+"/";
			} else {
				throw e;
			}
		}
		//生成时间戳作为服务器上的文件名
		String prefix = "" + new Date().getTime();
		String fileName = file.getOriginalFilename();
		String suffix = "";
		int index = fileName.indexOf('.');
		if (index > -1) {
			suffix = fileName.substring(index);
		}
		//临时文件夹如果不存在，创建之
		File tempFileDir = new File(tempPath);
		if (!tempFileDir.exists()) {
			tempFileDir.mkdirs();
		}
		//将Multipartfile转为file,存到临时目录
		File tempFile = new File(tempPath + fileName);
		try {
			file.transferTo(tempFile.getAbsoluteFile());
		} catch (IOException e){
			logger.error(e.getMessage(),e);
		}
		FileInputStream in = new FileInputStream(tempFile);
		this.sftp.put(in, prefix + suffix);
		if (null != in) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.remoteCostFileRootPath+prefix+suffix;
	}


	public String move(String tempPath) throws SftpException, IOException, FileNotFoundException {
		try {
			this.sftp.cd(this.remoteImageRootPath);
		} catch (SftpException e) {
			if (ChannelSftp.SSH_FX_NO_SUCH_FILE == e.id) {
				this.sftp.mkdir(this.remoteImageRootPath);
				this.sftp.cd(this.remoteImageRootPath);
			} else {
				throw e;
			}
		}
		//复制文件
		String fileName = tempPath.substring(tempPath.lastIndexOf('/')+1);
		InputStream tInputStream = sftp.get(tempPath);
		//拷贝读取到的文件流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = tInputStream.read(buffer)) > -1 ) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		InputStream nInputStream = new ByteArrayInputStream(baos.toByteArray());
		this.sftp.put(nInputStream, this.remoteImageRootPath+fileName);
		nInputStream.close();
		baos.close();
		tInputStream.close();

		//删除临时文件
		this.sftp.cd(this.localImageRootPath);
		this.sftp.rm(tempPath);
		return this.remoteImageRootPath+fileName;
	}
	/**
	 * 匹配查询——获取ftp文件流
	 * */
	public InputStream getInputStream(String path)throws SftpException{
		return sftp.get(path);
	}
}