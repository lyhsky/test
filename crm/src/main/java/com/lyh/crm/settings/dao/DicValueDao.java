package com.lyh.crm.settings.dao;

import com.lyh.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}
