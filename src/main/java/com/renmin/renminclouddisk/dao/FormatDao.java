package com.renmin.renminclouddisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.renmin.renminclouddisk.pojo.Format;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class FormatDao {
    @Resource
    private Format format;

    public Format findByFid(int fid){
        format.setFid(fid);
        return format.selectById();
    }

    public Format findByFName(String fName) {
        QueryWrapper<Format> wrapper = new QueryWrapper<>();
        wrapper.eq("format_name", fName);
        return format.selectOne(wrapper);
    }
}
