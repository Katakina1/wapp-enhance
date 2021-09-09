package com.xforceplus.wapp.modules.signin;

/**
 * CreateBy leal.liang on 2018/4/14.
 **/
public class ResponseVo {

        private Boolean success;
        private String reason;

        public ResponseVo(Boolean success) {
            this.success = success;
        }

        public Boolean isSuccess() {
            return this.success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getReason() {
            return this.reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public ResponseVo() {
            this.success = true;
            this.reason = "ok";
        }

}
