package com.xforceplus.wapp.modules.index.service.impl;

import com.aisino.common.StringUtil;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.index.dao.IndexStatisticsMapper;
import com.xforceplus.wapp.modules.index.entity.*;
import com.xforceplus.wapp.modules.index.service.IndexStatisticsService;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Bobby
 * @date 2018/4/16
 * 首页-业务
 */
@Service
public class IndexStatisticsServiceImpl implements IndexStatisticsService {

    private final static Logger LOGGER = getLogger(IndexStatisticsServiceImpl.class);
    private IndexStatisticsMapper indexStatisticsMapper;


    public IndexStatisticsServiceImpl(IndexStatisticsMapper indexStatisticsMapper) {
        this.indexStatisticsMapper = indexStatisticsMapper;
    }

    @Override
    public List<IndexInvoiceCollectionCountModel> getInvoiceCollectionSituation(Map<String, Object> params) {
        LOGGER.info("获取今日采集情况,params:{}", params);
        return indexStatisticsMapper.getInvoiceCollectionSituation( params);
    }

    @Override
    public List<IndexInvoiceCollectionCountModel> getInvoiceCollectionSituationMap(Map<String, Object> params) {
        LOGGER.info("获取今日发票情况,params:{}", params);
        Map<String, Integer> map = indexStatisticsMapper.getInvoiceCollectionSituationMap( params);
        final List<IndexInvoiceCollectionCountModel> modelList = newArrayList();
        if (map == null) {
            final Map<String, Integer> enMap = Maps.newHashMapWithExpectedSize(3);
            enMap.put("cjInvoiceCount", new Integer("0"));
            enMap.put("cyInvoiceCount", new Integer("0"));
            enMap.put("lrInvoiceCount", new Integer("0"));
            map = enMap;
        }
        Map<String, Integer> finalMap = map;
        finalMap.keySet().forEach(key -> {
            final IndexInvoiceCollectionCountModel model = new IndexInvoiceCollectionCountModel();
            model.setSourceSystem(key);
            model.setCountNum(finalMap.get(key));
            modelList.add(model);
        });

        return modelList;
    }

    @Override
    public List<IndexInvoiceScanMatchCountModel> getInvoiceCollectionSituationMap1( Map<String, Object> params) {
        LOGGER.info("获取今日发票情况,params:{}", params);
        Map<String, Integer> map = indexStatisticsMapper.getInvoiceCollectionSituationMap1(params);
        final List<IndexInvoiceScanMatchCountModel> modelList = newArrayList();
        if (map == null) {
            final Map<String, Integer> enMap = Maps.newHashMapWithExpectedSize(3);
            enMap.put("smwInvoiceCount", new Integer("0"));
            enMap.put("smsInvoiceCount", new Integer("0"));
            enMap.put("smfInvoiceCount", new Integer("0"));
            map = enMap;
        }
        Map<String, Integer> finalMap = map;
        finalMap.keySet().forEach(key -> {
            final IndexInvoiceScanMatchCountModel model = new IndexInvoiceScanMatchCountModel();
            model.setScanMatchStatus(key);
            model.setCountNum(finalMap.get(key));
            modelList.add(model);
        });

        return modelList;
    }

    @Override
    public PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionSituationList(Map<String, Object> params) {
        LOGGER.info("获取今日采集列表,params:{}", params);
        final PagedQueryResult<IndexInvoiceCollectionModel> result = new PagedQueryResult<>();
        List<IndexInvoiceCollectionModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceCollectionSituationListCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceCollectionSituationList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;

    }

    @Override
    public PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionSituationMatchList(Map<String, Object> params) {
        LOGGER.info("获取扫描匹配列表,params:{}", params);
        final PagedQueryResult<IndexInvoiceCollectionModel> result = new PagedQueryResult<>();
        List<IndexInvoiceCollectionModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceCollectionSituationListMatchCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceCollectionSituationMatchList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;

    }


    @Override
    public PagedQueryResult<IndexInvoiceScanMatchModel> getInvoiceCollectionSituationSQList(Map<String, Object> params) {
        LOGGER.info("获取申请已开红票列表,params:{}", params);
        final PagedQueryResult<IndexInvoiceScanMatchModel> result = new PagedQueryResult<>();
        List<IndexInvoiceScanMatchModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceCollectionSituationListSQCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceCollectionSituationSQList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;

    }


    @Override
    public PagedQueryResult<IndexInvoiceScanMatchModel> getInvoiceCollectionSituationYKList(Map<String, Object> params) {
        LOGGER.info("获取已开红票列表,params:{}", params);
        final PagedQueryResult<IndexInvoiceScanMatchModel> result = new PagedQueryResult<>();
        List<IndexInvoiceScanMatchModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceCollectionSituationListYKCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceCollectionSituationYKList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;

    }

    @Override
    public PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionSituationWqsList( Map<String, Object> params) {
        LOGGER.info("获取今日采集列表,params:{}", params);
        final PagedQueryResult<IndexInvoiceCollectionModel> result = new PagedQueryResult<>();
        List<IndexInvoiceCollectionModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceCollectionSituationWqsListCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceCollectionSituationWqsList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;

    }

    @Override
    public List<IndexInvoiceScanningCountModel> getInvoiceScanningSituation(Map<String, Object> params) {
        LOGGER.info("今日发票扫描情况,params {}", params);
        List<IndexInvoiceScanningCountModel> list = indexStatisticsMapper.getInvoiceScanningSituation(params);
        return list;
    }

    @Override
    public PagedQueryResult<IndexInvoiceScanningModel> getInvoiceScanningSituationList(Map<String, Object> params) {
        LOGGER.info("今日发票扫描列表,params {}", params);

        final PagedQueryResult<IndexInvoiceScanningModel> result = new PagedQueryResult<>();
        List<IndexInvoiceScanningModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceScanningSituationListCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceScanningSituationList( params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;
    }

    /**
     * 今日发票认证情况
     *
     * @param params {start:当天0点时间，end:当前时间}
     * @return
     */
    @Override
    public List<IndexInvoiceAuthenticationCountModel> getInvoiceAuthenticationSituation( Map<String, Object> params) {
        return indexStatisticsMapper.getInvoiceAuthenticationSituation( params);
    }

    /**
     * 今日发票认证列表
     *
     * @param params {start:当天0点时间，end:当前时间}
     * @return
     */
    @Override
    public PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceAuthenticationSituationList( Map<String, Object> params) {

        final PagedQueryResult<IndexInvoiceCollectionModel> result = new PagedQueryResult<>();
        List<IndexInvoiceCollectionModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getInvoiceAuthenticationSituationListCount( params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getInvoiceAuthenticationSituationList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;
    }

    /**
     * 本月新增发票信息（新增、认证、发票金额（未税）、发票税额）
     *
     * @param params {start:当天0点时间，end:当前时间}
     * @return
     */
    @Override
    public List<IndexInvoiceAuthenticationCountModel> getInvoiceInformationOfMonth(Map<String, Object> params) {
        LOGGER.info("本月新增发票信息（新增、认证、发票金额（未税）、发票税额）,params {}", params);
        return indexStatisticsMapper.getInvoiceInformationOfMonth(params);
    }

    /**
     * 本月发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatistics( Map<String, Object> params) {
        LOGGER.info("本月发票新增、认证成功、认证失败图表,params {}", params);
        List<IndexInvoiceChartStatisticsModel> chartStatisticsModelList = indexStatisticsMapper.getInvoiceChartStatistics(params);
        if (chartStatisticsModelList == null) {
            return newArrayList();
        }

        chartStatisticsModelList.forEach(indexInvoiceChartStatisticsModel -> {
            if (StringUtils.isNotEmpty(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear())) {
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear());
            }
            if (StringUtils.isNotEmpty(indexInvoiceChartStatisticsModel.getRzsbDayOfMonthInYear())) {
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzsbDayOfMonthInYear());
            }
        });

        for (int i = 0; i < chartStatisticsModelList.size() - 1; i++) {
            for (int j = chartStatisticsModelList.size() - 1; j > i; j--) {
                if (chartStatisticsModelList.get(j).getDayOfMonthInYear().equals(chartStatisticsModelList.get(i).getDayOfMonthInYear())) {
                    chartStatisticsModelList.remove(j);//删除重复元素
                }
            }
        }

        final List<IndexInvoiceChartStatisticsModel> modelList = complementingDayList(chartStatisticsModelList);
        modelList.sort((o1, o2) -> (o1.getDayOfMonthInYear().compareTo(o2.getDayOfMonthInYear())));
        return modelList;
    }

    /**
     * 本月发票签收图表
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsQS( Map<String, Object> params) {
        LOGGER.info("本月发票签收图表,params {}", params);
        List<IndexInvoiceChartStatisticsModel> chartStatisticsModelList = indexStatisticsMapper.getInvoiceChartStatisticsQS(params);
        if (chartStatisticsModelList == null) {
            return newArrayList();
        }

        chartStatisticsModelList.forEach(indexInvoiceChartStatisticsModel -> {
            if (StringUtils.isNotEmpty(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear())) {
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear());
            }
            if (StringUtils.isNotEmpty(indexInvoiceChartStatisticsModel.getRzsbDayOfMonthInYear())) {
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzsbDayOfMonthInYear());
            }
        });

        for (int i = 0; i < chartStatisticsModelList.size() - 1; i++) {
            for (int j = chartStatisticsModelList.size() - 1; j > i; j--) {
                if (chartStatisticsModelList.get(j).getDayOfMonthInYear().equals(chartStatisticsModelList.get(i).getDayOfMonthInYear())) {
                    chartStatisticsModelList.remove(j);//删除重复元素
                }
            }
        }

        final List<IndexInvoiceChartStatisticsModel> modelList = complementingDayList(chartStatisticsModelList);
        modelList.sort((o1, o2) -> (o1.getDayOfMonthInYear().compareTo(o2.getDayOfMonthInYear())));
        return modelList;
    }

    /**
     * 本月发票扫描匹配图表
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsSM( Map<String, Object> params) {
        LOGGER.info("本月发票扫描匹配图表,params {}", params);
        List<IndexInvoiceChartStatisticsModel> chartStatisticsModelList = indexStatisticsMapper.getInvoiceChartStatisticsSM(params);
        if (chartStatisticsModelList == null) {
            return newArrayList();
        }

        chartStatisticsModelList.forEach(indexInvoiceChartStatisticsModel -> {
            if (StringUtils.isNotEmpty(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear())) {
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear());
            }
            if (StringUtils.isNotEmpty(indexInvoiceChartStatisticsModel.getRzsbDayOfMonthInYear())) {
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzsbDayOfMonthInYear());
            }
        });

        for (int i = 0; i < chartStatisticsModelList.size() - 1; i++) {
            for (int j = chartStatisticsModelList.size() - 1; j > i; j--) {
                if (chartStatisticsModelList.get(j).getDayOfMonthInYear().equals(chartStatisticsModelList.get(i).getDayOfMonthInYear())) {
                    chartStatisticsModelList.remove(j);//删除重复元素
                }
            }
        }

        final List<IndexInvoiceChartStatisticsModel> modelList = complementingDayList(chartStatisticsModelList);
        modelList.sort((o1, o2) -> (o1.getDayOfMonthInYear().compareTo(o2.getDayOfMonthInYear())));
        return modelList;
    }

    /**
     * 任务提醒
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceTaskRemindingModel> getInvoiceTaskReminding(Map<String, Object> params) {
        return indexStatisticsMapper.getInvoiceTaskReminding( params);
    }

    /**
     * 本月发票新增、认证成功、认证失败图表
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYear(Map<String, Object> params) {
        LOGGER.info("本年发票新增、认证成功、认证失败图表,params {}", params);
        final List<IndexInvoiceChartStatisticsModel> modelList = indexStatisticsMapper.getInvoiceChartStatisticsYear(params);
        if (modelList == null) {
            return newArrayList();
        }
        final List<IndexInvoiceChartStatisticsModel> chartStatisticsModelList = complementingMonthList(modelList, params.get("endDate").toString());
        chartStatisticsModelList.sort((o1, o2) -> (o1.getDayOfMonthInYear().compareTo(o2.getDayOfMonthInYear())));
        return chartStatisticsModelList;
    }

    /**
     * 本年发票签收图表
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYearQS(Map<String, Object> params) {
        LOGGER.info("本年发票签收图表,params {}", params);
        final List<IndexInvoiceChartStatisticsModel> modelList = indexStatisticsMapper.getInvoiceChartStatisticsYearQS(params);
        if (modelList == null) {
            return newArrayList();
        }
        final List<IndexInvoiceChartStatisticsModel> chartStatisticsModelList = complementingMonthList(modelList, params.get("endDate").toString());
        chartStatisticsModelList.sort((o1, o2) -> (o1.getDayOfMonthInYear().compareTo(o2.getDayOfMonthInYear())));
        return chartStatisticsModelList;
    }

    /**
     * 本年发票扫描匹配图表
     *
     * @param params
     * @return
     */
    @Override
    public List<IndexInvoiceChartStatisticsModel> getInvoiceChartStatisticsYearSM(Map<String, Object> params) {
        LOGGER.info("本年发票扫描匹配图表,params {}", params);
        final List<IndexInvoiceChartStatisticsModel> modelList = indexStatisticsMapper.getInvoiceChartStatisticsYearSM(params);
        if (modelList == null) {
            return newArrayList();
        }
        final List<IndexInvoiceChartStatisticsModel> chartStatisticsModelList = complementingMonthList(modelList, params.get("endDate").toString());
        chartStatisticsModelList.sort((o1, o2) -> (o1.getDayOfMonthInYear().compareTo(o2.getDayOfMonthInYear())));
        return chartStatisticsModelList;
    }

    @Override
    public PagedQueryResult<IndexInvoiceCollectionModel> getInvoiceCollectionAuthList( Map<String, Object> params) {
        LOGGER.info("本月认证发票列表,params {}", params);
        final PagedQueryResult<IndexInvoiceCollectionModel> result = new PagedQueryResult<>();
        List<IndexInvoiceCollectionModel> resultList = newArrayList();
        final Integer count = indexStatisticsMapper.getCurrentMonthAuthCount(params);
        if (count > 0) {
            resultList = indexStatisticsMapper.getCurrentMonthAuthList(params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;
    }

    /**
     * 补全没有数据的月份
     *
     * @param list 年度统计数据
     * @return 补全后的年度统计数据
     */
    private List<IndexInvoiceChartStatisticsModel> complementingMonthList(List<IndexInvoiceChartStatisticsModel> list, String endDate) {
        //12个月
        final String[] monthArray = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        //12个月集合
        final List<String> arrayList = Arrays.asList(monthArray);
        final List<String> monthList = new ArrayList<>(arrayList);
        //移除存在数据的月份
        list.forEach(indexInvoiceChartStatisticsModel -> {
            String month;
            if(indexInvoiceChartStatisticsModel.getDayOfMonthInYear()!=null){
                month = indexInvoiceChartStatisticsModel.getDayOfMonthInYear().substring(5, 7);
            }else{
                month = indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear().substring(5, 7);
                indexInvoiceChartStatisticsModel.setDayOfMonthInYear(indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear());
            }
            monthList.remove(month);
        });
        if (monthList.size() > 0) {
            //补全不存在数据的月份
            monthList.forEach(monthStr -> {
                final IndexInvoiceChartStatisticsModel model = new IndexInvoiceChartStatisticsModel();
                //年-月
                model.setDayOfMonthInYear(endDate + "-" + monthStr);
                //新增数量默认为0
                model.setXzInvoiceCount(0);
                //认证成功数量默认为0
                model.setRzcgInvoiceCount(0);
                //认证失败数量默认为0
                model.setRzsbInvoiceCount(0);
                list.add(model);
            });
        }
        return list;
    }

    /**
     * 补全当月没有数据的天 数据默认0
     *
     * @param list 当月统计数据（日报）
     * @return 补全后的月度统计数据
     */
    private List<IndexInvoiceChartStatisticsModel> complementingDayList(List<IndexInvoiceChartStatisticsModel> list) {
        //当前时间所在月的天
        final Integer day = new DateTime().dayOfMonth().getMaximumValue();

        final List<String> dayList = newArrayList();
        IntStream.range(1, day + 1).forEach(i -> {
            if (i < 10) {
                dayList.add("0" + i);
            } else {
                dayList.add(String.valueOf(i));
            }
        });

        list.forEach(indexInvoiceChartStatisticsModel -> {
            String dayStr;
            if(indexInvoiceChartStatisticsModel.getDayOfMonthInYear()!=null){
                dayStr = indexInvoiceChartStatisticsModel.getDayOfMonthInYear().substring(8, 10);
            }else{
                dayStr = indexInvoiceChartStatisticsModel.getRzcgDayOfMonthInYear().substring(8, 10);
            }
            dayList.remove(dayStr);
        });

        if (dayList.size() > 0) {
            //补全不存在数据的天
            dayList.forEach(dayStr -> {
                final IndexInvoiceChartStatisticsModel model = new IndexInvoiceChartStatisticsModel();
                //年-月
                model.setDayOfMonthInYear(new DateTime().toString("yyyy-MM") + "-" + dayStr);
                //新增数量默认为0
                model.setXzInvoiceCount(0);
                //认证成功数量默认为0
                model.setRzcgInvoiceCount(0);
                //认证失败数量默认为0
                model.setRzsbInvoiceCount(0);
                list.add(model);
            });
        }

        return list;
    }


}
