package com.xforceplus.wapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 调用外部接口参数配置
 * @author Colin.hu
 * @date 4/16/2018
 */
@PropertySource(value = {"classpath:config.properties"})
@Component
@Getter @Setter
public class SystemConfig {

    private SystemConfig() {
    }

    public String getSecretId() {
		return secretId;
	}
	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getCollectUrl() {
		return collectUrl;
	}
	public void setCollectUrl(String collectUrl) {
		this.collectUrl = collectUrl;
	}
	public String getInvoiceGet() {
		return invoiceGet;
	}
	public void setInvoiceGet(String invoiceGet) {
		this.invoiceGet = invoiceGet;
	}
	public String getInvoiceSelection() {
		return invoiceSelection;
	}
	public void setInvoiceSelection(String invoiceSelection) {
		this.invoiceSelection = invoiceSelection;
	}
	public String getInvoiceState() {
		return invoiceState;
	}
	public void setInvoiceState(String invoiceState) {
		this.invoiceState = invoiceState;
	}
	public String getEnterpriseCode() {
		return enterpriseCode;
	}
	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getInvoiceCheckOne() {
		return invoiceCheckOne;
	}
	public void setInvoiceCheckOne(String invoiceCheckOne) {
		this.invoiceCheckOne = invoiceCheckOne;
	}
	public String getBuyerTaxNo() {
		return buyerTaxNo;
	}
	public void setBuyerTaxNo(String buyerTaxNo) {
		this.buyerTaxNo = buyerTaxNo;
	}
	public String getTempPath() {
		return tempPath;
	}
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
	public String getDepositPath() {
		return depositPath;
	}
	public void setDepositPath(String depositPath) {
		this.depositPath = depositPath;
	}
	public String getRemoteImageRootPath() {
		return remoteImageRootPath;
	}
	public void setRemoteImageRootPath(String remoteImageRootPath) {
		this.remoteImageRootPath = remoteImageRootPath;
	}
	public String getLocalImageRootPath() {
		return localImageRootPath;
	}
	public void setLocalImageRootPath(String localImageRootPath) {
		this.localImageRootPath = localImageRootPath;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDefaultPort() {
		return defaultPort;
	}
	public void setDefaultPort(String defaultPort) {
		this.defaultPort = defaultPort;
	}
	public String getDefaultTimeout() {
		return defaultTimeout;
	}
	public void setDefaultTimeout(String defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}
	public String getInvoiceTypeParam() {
		return invoiceTypeParam;
	}
	public void setInvoiceTypeParam(String invoiceTypeParam) {
		this.invoiceTypeParam = invoiceTypeParam;
	}


    private String secretId;

    private String secretKey;

    private String collectUrl;

    private String invoiceGet;

    private String invoiceSelection;

    private String invoiceState;

    private String enterpriseCode;

    private String version;

    private String appId;

    private String invoiceCheckOne;

    private String buyerTaxNo;

    /**
     * 上传时本地文件暂存路径
     */
    @Value("${filePathConstan.tempImgPath}")
    private String tempPath;
    /**
     * 上传时本地文件存放路径
     */
    @Value("${filePathConstan.depositPath}")
    private String depositPath;

    /**
     * 远程文件存放路径
     */
    @Value("${filePathConstan.remoteImageRootPath}")
    private String remoteImageRootPath;

    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.localImageRootPath}")
    private String localImageRootPath;

    //sftp IP底账
    @Value("${pro.sftp.host}")
    private String host;
    //sftp 用户名
    @Value("${pro.sftp.username}")
    private String userName;
    //sftp 密码
    @Value("${pro.sftp.password}")
    private String password;
    //sftp 默认端口号
    @Value("${pro.sftp.default.port}")
    private String defaultPort;
    //sftp 默认超时时间
    @Value("${pro.sftp.default.timeout}")
    private String defaultTimeout;
    private String invoiceTypeParam;

}
