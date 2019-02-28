package com.itheima.service;

import com.itheima.domain.Item;

import java.util.List;

public interface ItemService {

    /**
     * 保存
     * @param item
     */
     void save(Item item);

    /**
     * 根据skuid 判断是否重复
     * @param
     * @return
     */
    List<Item> findAllByid(Item item);
}
