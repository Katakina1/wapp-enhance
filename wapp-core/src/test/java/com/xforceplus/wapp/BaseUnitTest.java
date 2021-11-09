package com.xforceplus.wapp;

import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WappApplication.class,ClientFactoryMockConfig.class})
@Slf4j
@ActiveProfiles({"local","unit"})
@Rollback
@Transactional
//@TestExecutionListeners({TransactionalTestExecutionListener.class})
public class BaseUnitTest {

//    @Test
//    public void testFile() throws Exception {
//        String fileName2 = "data/AddRedNotificationRequest.json";
//        String json = readJsonFromFile(fileName2);
//        AddRedNotificationRequest request = JsonUtil.fromJson(json, AddRedNotificationRequest.class);
//        System.out.println(request);
//    }

    public String readJsonFromFile(String fileName)  {
        try {
            InputStreamReader read = new InputStreamReader(this.getClass().getResourceAsStream("/"+fileName)) ;
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt ;
            StringBuilder stringBuilder = new StringBuilder();
            while((lineTxt = bufferedReader.readLine()) != null){
                stringBuilder.append(lineTxt);
            }
            read.close();
            return stringBuilder.toString();
        }catch ( IOException e){
            log.error("json序列化异常",e);
        }
        return null;

    }

}
