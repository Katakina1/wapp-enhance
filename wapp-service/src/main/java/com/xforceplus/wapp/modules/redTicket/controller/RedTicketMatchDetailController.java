package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatchDetail;
import com.xforceplus.wapp.modules.redTicket.service.RedTicketMatchDetailService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_INVOICE_DETAILSBYNAME_LIST;
import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.URI_INVOICE_DETAILS_LIST;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class RedTicketMatchDetailController extends AbstractController {
    private RedTicketMatchDetailService redTicketMatchDetailService;
    private static final Logger LOGGER = getLogger(RedTicketMatchDetailController.class);
    @Autowired
    public RedTicketMatchDetailController(RedTicketMatchDetailService redTicketMatchDetailService){ this.redTicketMatchDetailService=redTicketMatchDetailService; }






}
