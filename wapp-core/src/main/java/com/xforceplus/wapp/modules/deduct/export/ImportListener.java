package com.xforceplus.wapp.modules.deduct.export;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author aiwentao@xforceplus.com
 */
@Slf4j
@Getter
public class ImportListener<T> extends AnalysisEventListener<T> {
    private int rows;
    @Setter
    private int batchSize = 1000;
    private final List<T> validInvoices = Lists.newArrayList();
    private final List<T> invalidInvoices = Lists.newArrayList();
    private final Consumer<List<T>> consumer;
    @Setter
    private Consumer<List<T>> failConsumer;
    private final List<Predicate<T>> checkList = Lists.newArrayList();

    public void addCheck(Predicate<T> check) {
        checkList.add(check);
    }

    public ImportListener(Consumer<List<T>> consumer) {
        this(consumer, null);
    }

    public ImportListener(Consumer<List<T>> consumer, Consumer<List<T>> failConsumer) {
        this(consumer, failConsumer, null);
    }

    public ImportListener(Consumer<List<T>> consumer, Consumer<List<T>> failConsumer, List<Predicate<T>> checkList) {
        this.consumer = consumer;
        this.failConsumer = failConsumer;
        if (CollectionUtils.isNotEmpty(checkList)) {
            this.checkList.addAll(checkList);
        }
    }
    public ImportListener(Predicate<T> check, Consumer<List<T>> consumer, Consumer<List<T>> failConsumer) {
        this.consumer = consumer;
        this.failConsumer = failConsumer;
        if (check != null) {
            this.checkList.add(check);
        }
    }


    @Override
    public void invoke(T vo, AnalysisContext analysisContext) {
        rows++;
        if(checkData(vo)) {
            validInvoices.add(vo);
        } else {
            invalidInvoices.add(vo);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (consumer != null) {
            List<T> list = Lists.newArrayListWithCapacity(batchSize);
            for (T validInvoice : validInvoices) {
                list.add(validInvoice);
                if (list.size() >= batchSize) {
                    try {
                        consumer.accept(list);
                    } catch (Exception e) {
                        invalidInvoices.addAll(list);
                    }
                    list.clear();
                }
            }
            try {
                consumer.accept(list);
            } catch (Exception e) {
                log.error("Excel校验正常数据处理异常：{}", e.getMessage(), e);
                invalidInvoices.addAll(list);
            }
            list.clear();
        }
        if (failConsumer != null) {
            try {
                failConsumer.accept(invalidInvoices);
            } catch (Exception e) {
                log.error("Excel校验异常数据处理异常：{}", e.getMessage(), e);
            }
        }
    }

    public Boolean checkData(T vo) {
        return checkList.stream().allMatch(it->it.test(vo));
    }

}
