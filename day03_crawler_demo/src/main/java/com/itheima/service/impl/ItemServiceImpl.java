package com.itheima.service.impl;

import com.itheima.dao.ItemDao;
import com.itheima.domain.Item;
import com.itheima.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemDao itemDao;

    @Override
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAllByid(Item item) {

        Example example  = Example.of(item);
        return itemDao.findAll(example);
    }
}
