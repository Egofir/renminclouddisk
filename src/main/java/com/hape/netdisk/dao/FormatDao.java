package com.hape.netdisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hape.netdisk.pojo.Format;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormatDao {
    @Autowired
    private Format format;

    /**
     * 根据id查询
     * @param fid
     * @return
     */
    public Format findByFid(int fid){
        format.setFid(fid);
        return format.selectById();
    }

    /**
     * 根据文件后缀查询格式
     * @param fName
     * @return
     */
    public Format findByFName(String fName){
        QueryWrapper<Format> wrapper = new QueryWrapper<>();
        wrapper.eq("format_name",fName);
        return format.selectOne(wrapper);
    }
}
