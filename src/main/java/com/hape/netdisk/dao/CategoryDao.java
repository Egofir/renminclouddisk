package com.hape.netdisk.dao;

import com.hape.netdisk.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CategoryDao {
    @Autowired
    private Category category;

    /**
     * 查询所有分类
     * @return
     */
    public List<Category> findAll(){
        return category.selectAll();
    }

    /**
     * 根据id查询
     * @param cid
     * @return
     */
    public Category findByCid(int cid){
        category.setCid(cid);
        return category.selectById();
    }
}
