package com.xforceplus.wapp.client;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.Collection;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Data
@NoArgsConstructor
public class NbrBean {
    @Getter
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class SamsNbrBean extends NbrBean {
        private final Collection<String> itemNbrs;
        private final List<String> filterKeys = Lists.newArrayList("sellingUnit", "specification");
        private final List<String> types = Lists.newArrayList("item", "tradeitem");
    }

    @Getter
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class HyperNbrBean extends NbrBean {
        private final Collection<String> condition;
        private final Integer pageIndex = 1;
        private final Integer pageSize = 10;
    }
}
