package com.xforceplus.wapp.modules.einvoice.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceImage;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceLog;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoice;
import com.xforceplus.wapp.modules.einvoice.entity.RecordInvoiceDetail;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author marvin
 * 电票上传业务层接口
 */
public interface EinvoiceUploadService {

    /**
     * 上传电子发票
     * 1.上传电子发票文件，可以是zip或者rar类型的压缩文件（压缩包里面直接是pdf文件），也可以直接是上传pdf文件，
     * 2.对于上传的文件重新命名，命名规则当前时间加文件后缀如（20180504164823.zip/20180504164823.rar），
     * 若是直接上传的是pdf文件，和压缩文件一样命名，如（20180504164823.pdf），并把所有的文件都放在临时文件夹里操作
     * 3.如果是压缩包上传，解压当前压缩包，若是pdf上传，则是压缩当前pdf文件，然后把压缩包从临时文件转移到另外一个存储文件夹中保存，
     * 4.获取临时文件夹中的所有pdf文件，准备解析，
     * 5.循环解析得到的所有pdf文件，如有pdf解析失败，则解析下一个pdf文件，不对解析失败的文件上传图片保存。
     * 6.解析完成所有的pdf文件，并得到解析的信息，开始查验查验并保存解析的和查验的信息
     * 7.循环查验和签收解析的电票信息：
     * a.若是之前解析失败的文件，则不用查验和签收，直接返回，查验和签收下一个电票信息；
     * b.检查当前电票信息是否上传过，若上传过则返回，并设置返回信息：重复上传。
     * c.若不是重复上传，则判断发票类型是电票还是通行费电票。
     * d.若是电票，则设置查验参数（发票代码、发票号码、购方税号、校验码、开票日期、发票类型、发票金额），调用查验接口，得到查验的结果
     * 若查返回查验代码0001则代表查验成功，获取返回的数据，作为底账表数据，若底账表的发票状态为2（作废），则表示此电票签收失败，此时保存电票信息
     * 到扫描，同时保存记录和图片信息到表中，不保存底账表信息；若底账表的发票状态为0（正常），则表示此电票签收成功，保存底账信息到底账表，保存明细到
     * 明细表。同时保存电票信息、记录和图片信息到表中。
     * e.若不是电票是通行费发票，则直接用发票的代码和号码组成uuid，到底账表查询底账信息，若查询结果为空，则表示签收失败，此时保存电票信息
     * 到扫描，同时保存记录和图片信息到表中，若查询的底账信息不为空，则判断底账信息的开票日期、校验码、发票代码和发票号码是否都是一致的，若一致则签收
     * 成功，若有其中一个不一样，则签收失败，无论签收成功还是签收失败都保存发票信息到扫描表中，然后报记录和图片信息到表中。
     * 8.返回解析完成后的电票信息
     *
     * @param file        上传的电票文件：可以是zip或者rar类型的压缩文件（压缩包里面直接是pdf文件），也可以直接是上传pdf文件
     * @param schemaLabel 当前用户所在的分库名
     * @param user        当前用户信息：主要用到用户登录名称、用户名称
     * @return 解析完成的电票信息
     */
    List<ElectronInvoiceEntity> uploadElectronInvoice(String schemaLabel, MultipartFile file, UserEntity user);

    /**
     * 保存上传的电票信息
     *
     * @param invoiceEntity
     * @param schemaLabel   当前用户所在的分库名
     * @return
     */
    Long saveElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity);

    /**
     * 保存电票上传是的日志
     *
     * @param invoiceLog
     * @param schemaLabel 当前用户所在的分库名
     */
    void saveElectronInvoiceLog(String schemaLabel, ElectronInvoiceLog invoiceLog);

    /**
     * 保存电票上传的图片
     *
     * @param schemaLabel  当前用户所在的分库名
     * @param invoiceImage 图片信息
     * @param isSave       是否是保存true：保存    false：更新
     */
    void saveOrUpdateInvoiceImage(String schemaLabel, ElectronInvoiceImage invoiceImage, Boolean isSave);

    /**
     * 删除电票数据
     *
     * @param id          要删除的id
     * @param schemaLabel 当前用户所在的分库名
     * @return true：删除成功
     */
    Boolean deleteElectronInvoice(String schemaLabel, Long id);

    /**
     * 保存上传的电票的查验签收信息
     *
     * @param recordInvoice 查验的签收信息
     * @param schemaLabel   当前用户所在的分库名
     * @return 影响条数
     */
    int saveRecordInvoice(String schemaLabel, RecordInvoice recordInvoice);

    /**
     * 保存发票商品明细
     *
     * @param details     明细信息
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    int saveRecordInvoiceDetail(String schemaLabel, List<RecordInvoiceDetail> details);

    /**
     * 保存并查验签收用户手工输入的电子发票
     * 1.获取用户输入的发票代码和发票号码，组成uuid，到扫描表中查询，此张电票是否已经保存过。若保存，则返回重复上传，和该发票当前的签收状态
     * 2.若查询回来的数据为空，则开始查验和签收此发票
     * a.判断发票类型是电票还是通行费电票。
     * b.若是电票，则设置查验参数（发票代码、发票号码、购方税号、校验码、开票日期、发票类型、发票金额），调用查验接口，得到查验的结果
     * 若查返回查验代码0001则代表查验成功，获取返回的数据，作为底账表数据，若底账表的发票状态为2（作废），则表示此电票签收失败，此时保存电票信息
     * 到扫描，不保存底账表信息；若底账表的发票状态为0（正常），则表示此电票签收成功，保存底账信息到底账表，保存明细到
     * 明细表。
     * c.若不是电票是通行费发票，则直接用发票的代码和号码组成uuid，到底账表查询底账信息，若查询结果为空，则表示签收失败，此时保存电票信息
     * 到扫描，若查询的底账信息不为空，则判断底账信息的开票日期、校验码、发票代码和发票号码是否都是一致的，若一致则签收成功，
     * 若有其中一个不一样，则签收失败，无论签收成功还是签收失败都保存发票信息到扫描表中.
     * 3.返回保存和查验签收的结果
     *
     * @param invoiceEntity 用户手工录入的信息
     * @param user          用户信息
     * @param schemaLabel   当前用户所在的分库名
     * @return
     */
    ElectronInvoiceEntity saveInputElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity, UserEntity user);

    /**
     * 通过电票的id获取uuid查询电票的信息
     *
     * @param id          电票id 可以为空
     * @param uuid        电票的uuid 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @return 查询到的实体信息
     */
    ElectronInvoiceEntity selectElectronInvoice(String schemaLabel, Long id, String uuid);

    /**
     * 保存修改的电票信息
     *
     * @param invoiceEntity 修改的电票信息
     * @param schemaLabel   当前用户所在的分库名
     * @param userId        当前用户的id
     * @return 修改并保存之后的电票信息
     */
    ElectronInvoiceEntity saveUpdateElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity, Long userId);

    /**
     * 修改电票信息
     *
     * @param invoiceEntity 要修改的信息
     * @param schemaLabel   当前用户所在的分库名
     * @return
     */
    Boolean updateElectronInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity);

    /**
     * 获取图片
     *
     * @param id          电票扫描表id
     * @param schemaLabel 当前用户所在的分库名
     * @param user        用户信息
     * @return 图片的base64编码
     */
    String getInvoiceImage(String schemaLabel, Long id, UserEntity user);

    /**
     * 获取图片
     *
     * @param schemaLabel 当前用户所在的分库名
     * @param id          电票扫描表id
     * @param user        用户信息
     * @param response
     */
    void getInvoiceImageForAll(String schemaLabel, Long id, UserEntity user, HttpServletResponse response);

    /**
     * 根据uuid或者scanId获取图片的image_path
     *
     * @param uuid
     * @param scanId
     * @param schemaLabel 当前用户所在的分库名
     * @return
     */
    ElectronInvoiceImage getElectronInvoiceImage(String schemaLabel, String uuid, String scanId);

    /**
     * 根据扫描版发票的id获取该发票的所有信息
     *
     * @param id          发票在扫描表的id
     * @param schemaLabel 当前用户所在的分库名
     * @return 查询到的信息记录
     */
    ElectronInvoiceEntity selectElectronInvoiceAll(String schemaLabel, Long id);

    /**
     * 保存要删除发票的信息，保存到发票删除表
     *
     * @param invoiceEntity 要删除的发票信息
     * @param schemaLabel   当前用户所在的分库名
     * @return 添加行数
     */
    int saveOrUpdateDelInvoice(String schemaLabel, ElectronInvoiceEntity invoiceEntity);

    /**
     * 根据uuid查询底账表中的发票数据
     *
     * @param schemaLabel 当前用户所在的分库名 不可以为空
     * @param uuid        发票的uuid 不可以为空
     * @return 查询到的底账数据
     */
    RecordInvoice selectRecordInvoice(String schemaLabel, String uuid);

    /**
     * 税号权限限制：根据用户的id，查询用户税号集合，然后与传入的购方税号相比是否有该税号发票的操作权限
     * 如果传入的购方税号为空，也是返回true，如果userId为空，将返回false
     *
     * @param schemaLabel 当前用户所在的分库名 不可以为空
     * @param userId      用户id
     * @param gfTaxNo     购方税号
     * @return true：有此权限  false：无此权限
     */
    Boolean checkUserTaxNoPower(String schemaLabel, Long userId, String gfTaxNo);

    /**
     * 删除发票上传时的图片，包括同时删除sftp上面的图片,（id和uuid都是扫描表的数据）
     * 可以传入id或者uuid的其中一个，如果两个有，将会查询同时符合id和uuid的发票，若查询不到发票信息将不会删除任何数据，
     * 如果两个（id和uuid）都没有传入也将不会删除任何数据
     * 如果参数schemaLabel为空，也不会删除数据
     *
     * @param schemaLabel 当前用户所在的分库名 不可以为空
     * @param id          发票扫描表的id 可以为空
     * @param uuid        发票扫描表的uuid 可以为空
     */
    void delInvoiceImgIncludeSFTP(String schemaLabel, Long id, String uuid);
}
