package com.xforceplus.wapp.modules.company.listener;

import com.xforceplus.wapp.modules.company.dto.CompanyImportDto;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyImportListenerTest {


    @Test
    void invoke() {
        CompanyImportListener listener = new CompanyImportListener();
        CompanyImportDto companyImportDto = new CompanyImportDto();


        listener.invoke(companyImportDto, null);
    }

}