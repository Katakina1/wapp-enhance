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
package com.xforceplus.wapp.htmladapter.factory;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.extend.FSCacheEx;
import com.openhtmltopdf.extend.FSCacheValue;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.latexsupport.LaTeXDOMMutator;
import com.openhtmltopdf.mathmlsupport.MathMLDrawer;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import com.openhtmltopdf.util.XRLog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

/**
 * OpenhttptopdfRenderBuilder对象工厂
 * 实例化OpenhttptopdfRenderBuilder对象，添加字体
 *
 * @author
 *
 * @version 1.0
 * @since 1.8
 */
@Slf4j
public class HtmlToPdfRendererFactory implements PooledObjectFactory<HtmlToPdfRenderBuilder> {
    private static HashMap<String, FSSupplier<InputStream>> fontFSSupplierCache = new HashMap<>();

    private static HtmlToPdfRendererFactory factory = new HtmlToPdfRendererFactory();
    private static GenericObjectPool<HtmlToPdfRenderBuilder> objectPool;

    /**
     * 最小线程数
     */
    private static int MinIdle = 5;

    /**
     * 最大空闲数
     */
    private static int MaxIdle = 8;

    /**
     * 最大线程数
     */
    private static int MaxTotal = 10;
    /**
     * 连接空闲的最小时间，达到此值后空闲链接将会被移除
     */
    private static long SoftMinEvictableIdleTimeMillis = 30000L;


    public static void init() {
        factory.fontCache();
        //设置对象池的相关参数
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        //最大空闲数
        poolConfig.setMaxIdle(MaxIdle);
        //最大线程数
        poolConfig.setMaxTotal(MaxTotal);
        //最小线程数
        poolConfig.setMinIdle(MinIdle);

        poolConfig.setSoftMinEvictableIdleTimeMillis(SoftMinEvictableIdleTimeMillis);

        log.debug("pool param:");
        log.debug("MaxIdle:" + MaxIdle);
        log.debug("MaxTotal:" + MaxTotal);
        log.debug("MinIdle:" + MinIdle);
        //新建一个对象池,传入对象工厂和配置
        objectPool = new GenericObjectPool<HtmlToPdfRenderBuilder>(factory, poolConfig);
    }

    /**
     * 获取OpenhttptopdfRenderBuilder实例
     *
     * @return OpenhttptopdfRenderBuilder
     */
    @SneakyThrows
    public static HtmlToPdfRenderBuilder getPdfRendererBuilderInstance() {
        log.debug("pollActiveNum:" + objectPool.getNumActive());
        return objectPool.borrowObject();
    }

    /**
     * 归还openhtpdfRenderObject对象
     * @param openhtpdfRenderObject  openhtpdfRenderObject
     */
    public static void returnPdfBoxRenderer(HtmlToPdfRenderBuilder openhtpdfRenderObject) {
        if(openhtpdfRenderObject != null && openhtpdfRenderObject.isActive() == true) {
            objectPool.returnObject(openhtpdfRenderObject);
        }
    }

    /**
     * 字体缓存
     */
    @SneakyThrows
    private void fontCache(){

        ClassPathResource resource = new ClassPathResource("fonts/Kaiti.ttf");
        InputStream inputStream = resource.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOUtils.copy(inputStream,os);
        fontFSSupplierCache.put("Kaiti", () -> {
            log.debug("read font PSName Requesting font");
            return new ByteArrayInputStream(os.toByteArray());
        });
        inputStream.close();
        os.flush();
        os.close();
        resource = new ClassPathResource("fonts/SimSun.ttf");
        InputStream is1 = resource.getInputStream();
        ByteArrayOutputStream os1 = new ByteArrayOutputStream();
        IOUtils.copy(is1,os1);
        fontFSSupplierCache.put("SimSun", () -> {
            log.debug("read font PSName Requesting font");
            return new ByteArrayInputStream(os1.toByteArray());
        });
        is1.close();
        os1.flush();
        os1.close();
    }

    @Override
    public PooledObject<HtmlToPdfRenderBuilder> makeObject() {
        String i = UUID.randomUUID().toString();
        log.debug("make OpenhttptopdfRender object：" + i);
        FSCacheEx<String, FSCacheValue> fsCacheEx = new FSDefaultCacheStore();
        HtmlToPdfRenderBuilder openhttptopdfRenderBuilder = new HtmlToPdfRenderBuilder();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
        builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
        builder.defaultTextDirection(PdfRendererBuilder.TextDirection.LTR);
        builder.useSVGDrawer(new BatikSVGDrawer());
        builder.useMathMLDrawer(new MathMLDrawer());
        builder.addDOMMutator(LaTeXDOMMutator.INSTANCE);
        builder.defaultTextDirection(BaseRendererBuilder.TextDirection.LTR);
        builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fsCacheEx);
        fontFSSupplierCache.forEach((key,value)-> builder.useFont(value,key, 500, BaseRendererBuilder.FontStyle.NORMAL, true));

        builder.useFastMode();
        XRLog.setLoggingEnabled(false);
        openhttptopdfRenderBuilder.setPdfRendererBuilder(builder);

        return new DefaultPooledObject(openhttptopdfRenderBuilder);
    }

    @Override
    public void destroyObject(PooledObject<HtmlToPdfRenderBuilder> pooledObject) {
        //logger.debug("destroyObject" );
        pooledObject.getObject().setActive(false);
    }

    @Override
    public boolean validateObject(PooledObject<HtmlToPdfRenderBuilder> pooledObject) {
        //logger.debug("validateObject" );
        return pooledObject.getObject().isActive();

    }

    @Override
    public void activateObject(PooledObject<HtmlToPdfRenderBuilder> pooledObject) {
        //logger.debug("activateObject");
        pooledObject.getObject().setActive(true);
    }

    @Override
    public void passivateObject(PooledObject<HtmlToPdfRenderBuilder> pooledObject){
        //logger.debug("passivateObject");
        //pooledObject.getObject().setActive(false);
    }

}
