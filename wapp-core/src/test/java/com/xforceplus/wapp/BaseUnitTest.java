package com.xforceplus.wapp;

import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.model.AddRedNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WappApplication.class)
@Slf4j
@ActiveProfiles("local")
public class BaseUnitTest {

    @Test
    public void testFile() throws Exception {
        String fileName2 = "data/AddRedNotificationRequest.json";
        String json = readJsonFromFile(fileName2);
        AddRedNotificationRequest request = JsonUtil.fromJson(json, AddRedNotificationRequest.class);
        System.out.println(request);
    }

    public String readJsonFromFile(String fileName) throws IOException {
        InputStreamReader read = new InputStreamReader(this.getClass().getResourceAsStream("/"+fileName)) ;
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt ;
        StringBuilder stringBuilder = new StringBuilder();
        while((lineTxt = bufferedReader.readLine()) != null){
            stringBuilder.append(lineTxt);
        }
        read.close();
        return stringBuilder.toString();
    }

}
