package com.xforceplus.wapp.modules.posuopei.service.impl;

/**
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:17:06
*/

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.modules.posuopei.dao.DetailsDao;
import com.xforceplus.wapp.modules.posuopei.dao.MatchDao;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.DetailsService;
import com.xforceplus.wapp.modules.posuopei.service.MatchDetaAllService;
import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@Service
public class MatchDetaAllServiceImpl implements MatchDetaAllService {

    private static final Logger LOGGER= getLogger(MatchDetaAllServiceImpl.class);

    private MatchDao matchDao;

    @Override
    public MatchEntity getMatchDetail(String matchno) {
        MatchEntity matchEntity=new MatchEntity();
        matchEntity.setInvoiceEntityList(matchDao.invoiceList(matchno));
        matchEntity.setPoEntityList(matchDao.poListDetail(matchno));
        matchEntity.setClaimEntityList(matchDao.claimList(matchno));
        return matchEntity;
    }


}
