package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.common.utils.DB2Conn;
import com.xforceplus.wapp.modules.job.dao.HostCountDao;
import com.xforceplus.wapp.modules.job.service.HostCountService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import static org.slf4j.LoggerFactory.getLogger;

@Service("hostCountService")
public class HostCountServiceImpl implements HostCountService {
    private static  final  Logger LOGGER=getLogger(HostCountServiceImpl.class);

    @Autowired
    private HostCountDao hostCountDao;
    @Override
    public Integer hostCount() {
        Integer count=0;
        Connection conn=null;
        PreparedStatement st=null;
        ResultSet rs=null;
        try{
            conn= DB2Conn.getConnection();
            String sql="select count(1) from CNINVMAT.FINANCIAL_TXN";
            st=conn.prepareStatement(sql);
            rs=st.executeQuery();
            while(rs.next()){
                 count=rs.getInt(1);
            }




        }catch (Exception e){
            LOGGER.info("hostCountException {}",e);
            count=-1;
        }finally {
            DB2Conn.closeConnection(conn);
        }
        return hostCountDao.insertHostCount(count);
    }
}
