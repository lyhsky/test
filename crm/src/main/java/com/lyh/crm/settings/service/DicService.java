package com.lyh.crm.settings.service;

import com.lyh.crm.settings.domain.DicValue;

import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public interface DicService {
    Map<String, List<DicValue>> getAll();
}
