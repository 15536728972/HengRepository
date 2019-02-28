package com.itheima.dao;

import com.itheima.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDao extends JpaRepository<Item,Long> {


}
