package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import com.xforceplus.wapp.modules.base.entity.KnowledgeFileExcelEntity;
import com.xforceplus.wapp.modules.base.export.KnowCenterExcel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface KnowCenterService {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<KnowledgeFileEntity> queryList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 上传知识中心文件
     * @param file
     */
    void uploadKnow(MultipartFile file,String venderType);

    /**
     * 下载知识中心文件
     * @param path
     * @param response
     */
    void getDownLoadFile(String path,String fileName, HttpServletResponse response);

    /**
     * 删除上传的文件信息
     * @param entity 文件
     * */
    void deleteOne(KnowledgeFileEntity entity)throws JSchException, SftpException;
    /**
     * 转换excel
     * @param entity 文件
     * */
    List<KnowledgeFileExcelEntity> toExcel(List<KnowledgeFileEntity> list);
}
