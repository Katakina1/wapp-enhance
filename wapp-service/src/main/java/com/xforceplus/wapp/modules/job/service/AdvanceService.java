package com.xforceplus.wapp.modules.job.service;

import org.springframework.stereotype.Service;

@Service
public interface AdvanceService {

    public boolean getDataFromBPMS(String epsNos);

    public void getDPFKFromBPMS();

}
