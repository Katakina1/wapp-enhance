/**
 * Copyright (c) 2022-2030, Janah Wang / 王柱 (wangzhu@cityape.tech).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xforceplus.wapp.htmladapter.service;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.htmladapter.dto.XmlToPdf;

/**
 * PDF生成接口
 *
 * 方法中htmlLocation,templateLocation参数支持的协议如下：
 * <p>
 * 1. classpath:
 * 2. file:
 * 3. ftp:
 * 4. http: and https:
 * 5. classpath*:
 * 6.path
 * </p>
 * @author yuhongxia
 */
public interface PdfGenerator {

    /**
     * 数据格式{invoiceMain:[{"invoiceNo":"",details:[]},{"invoiceNo":"",details:[]}]}
     * @param jsonData
     * @return
     */
    byte[] generatePdfFileByHtmlAndData(XmlToPdf jsonData);
}
