package com.xforceplus.wapp.modules.fixed.entity;

import java.io.Serializable;

/**
 * 文件实体类
 * @author Karry.xie
 *
 */
public class FileEntity implements Serializable {
	
	private String fileName;
	private String filePath;
	private Long id;
	private Long matchId;
	private String fileType;
	
	
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMatchId() {
		return matchId;
	}
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	

}
