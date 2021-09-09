package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.certification.dao.ImportCheckDao;
import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import com.xforceplus.wapp.modules.certification.export.InvoiceCertificationImport;
import com.xforceplus.wapp.modules.certification.service.ImportCheckService;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.signin.enumflord.InvoiceTypeEnum;
import org.apache.commons.lang.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 导入勾选业务层接口
 *
 * @author kevin.wang
 * @date 4/19/2018
 */
@Service
public class ImportCheckServiceImpl implements ImportCheckService {

    private final static Logger LOGGER = getLogger(ImportCheckServiceImpl.class);

    private final ImportCheckDao importCheckDao;

    @Autowired
    public ImportCheckServiceImpl(ImportCheckDao importCheckDao) {
        this.importCheckDao = importCheckDao;
    }

    /**
     * 提交勾选
     * @param param 提交勾选的发票信息
     * @return 结果
     */
    @Override
    public Integer submit(String schemaLabel,Map<String, String> param,String loginName,String userName) {
        final String jsonParam = param.get("jsonParam");
        final List<ImportCertificationEntity> entityList = new Gson().fromJson(jsonParam, new TypeToken<List<ImportCertificationEntity>>(){}.getType());
        return importCheckDao.submit(schemaLabel,entityList,loginName,userName);
    }
    
}
