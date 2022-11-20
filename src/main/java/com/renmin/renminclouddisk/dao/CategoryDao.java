package com.renmin.renminclouddisk.dao;

import com.renmin.renminclouddisk.pojo.Category;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CategoryDao {
    @Resource
    private Category category;

    public List<Category> findAll(){
        return category.selectAll();
    }

    public Category findByCid(int cid){
        category.setCid(cid);
        return category.selectById();
    }
}
