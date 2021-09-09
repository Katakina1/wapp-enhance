package com.xforceplus.wapp.interfaceBPMS;

/*import com.actionsoft.bpms.api.OpenApiClient;
import com.actionsoft.sdk.service.model.FormFileModel;
import com.actionsoft.sdk.service.model.UploadFile;
import com.actionsoft.sdk.service.response.process.ProcessInstResponse;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.json.JSON;*/

public class CGTest {

   /* private static final String apiServer = "http://bpms-qa.cn.wal-mart.com/portal/openapi";
    private static final String apiMethod = "process.create";
    private static final String accessKey = "svcacctapt";
    private static final String secret = "mycA93NSEghCfez3";

    private void _dopost(){
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("processDefId", "obj_0cabaf0991594ac9aef6321f1da62223");
        args.put("uid", "214566515");
        args.put("title", "采购问题单测试");
        OpenApiClient client = new OpenApiClient(apiServer, accessKey, secret);
        ProcessInstResponse r = client.exec(apiMethod, args, ProcessInstResponse.class);
        String instanceId = r.getData().getId();
        System.out.println("-------获取实例ID："+instanceId+"--------");


        args.clear();
        args.put("boName", "BO_ACT_DISCREPANCYREQUEST");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("APPROVETEMPLATENO","vvv");
        data.put("DEPARTMENTNO","测试部");
        data.put("ENGLISHNAME","test1");
        data.put("CHINESENAME","泰斯特依");
        data.put("DIVISIONNO","1001");
        data.put("JV","WI");
        data.put("BUYERCITY","32257");
        data.put("VENDORNO","365422");
        data.put("VENDORNAME","test1");
        data.put("VENDORCONTACTTEL","13838383838");
        data.put("DISCREPANCYNO","1010");
        data.put("REASONCONTENT","100100");
        data.put("URGENTFLAG","1");
        data.put("BUYERAPPROVER","test1");
        data.put("APPROVETEMPLATENOSAVE","123");
        data.put("REQSTATUS","Cancelled");
        args.put("recordData", data);
        args.put("bindId", instanceId);
        String res2 = client.exec("bo.create", args);
        System.out.println("-------创建BO完成:"+res2+"---------");
        JSONObject json = JSONObject.fromObject(res2);
        String boId = (String)json.get("data");

        args.clear();
        args.put("processInstId", instanceId);
        String res3 = client.exec("process.start", args);
        System.out.println("-------启动流程:"+res3+"---------");

        args.clear();
        args.put("processInstId", instanceId);
        ProcessInstResponse res4 = client.exec("process.inst.get", args, ProcessInstResponse.class);
        String taskId = res4.getData().getStartTaskInstId();
        System.out.println("-------获取任务实例:"+taskId+"---------");


        FormFileModel fileModel = new FormFileModel();
        fileModel.setAppId("com.actionsoft.apps.walmart.fin.process.discrepancyrequest");
        fileModel.setBoId(boId);
        fileModel.setBoItemName("ATTACHMENT");
        fileModel.setBoName("BO_ACT_DISCREPANCYREQUEST");
        fileModel.setCreateDate(new Timestamp(System.currentTimeMillis()));
        fileModel.setCreateUser("214566515");
        fileModel.setFileName("test.txt");
        fileModel.setProcessInstId(instanceId);
        fileModel.setRemark("测试附件");
        fileModel.setTaskInstId(taskId);

        UploadFile uploadFile = new UploadFile();
        uploadFile.setName("test.txt");

        byte[] buffer = null;
        try
        {
            File file = new File("C:/Temp/test.txt");
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        uploadFile.setContent(buffer);
        args.clear();
        args.put("formFileModel", fileModel);
        args.put("data", uploadFile);
        String res5 = client.exec("bo.file.up", args);
        System.out.println("-------推送附件:"+res5+"---------");


    }

    public static void main(String[] args) {
        CGTest test = new CGTest();
        test._dopost();
    }*/
}
