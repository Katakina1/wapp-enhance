package com.xforceplus.wapp.modules.deduct.mapstruct;

import com.xforceplus.wapp.modules.epd.dto.SummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface SummaryResponseMapper {
//
//    @Mapping(expression = "java(new SummaryResponse(s.getValue(),s.getKey()))")
//    List<SummaryResponse> toSummary(Map<String,Integer> map);
}
