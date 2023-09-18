package com.xforceplus.wapp.modules.backfill.events;

import com.xforceplus.wapp.modules.backfill.dto.AnalysisXmlResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XmlToPdfEvent {
    private String vendorId;
    private String uploadFileId;
    private AnalysisXmlResult result;
//    private Consumer<UploadCallBack> uploadCallBack;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadCallBack {
        private String errMsg;
        private String uploadId;
        private String uploadPath;

        public boolean success() {
            return StringUtils.isBlank(errMsg);
        }
    }
}
