package com.renmin.renminclouddisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.renmin.renminclouddisk.pojo.Code;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CodeDao {
    @Resource
    private Code code;

    public int insert(Code code){
        if(!code.insert()) {
            return 0;
        }
        return code.getCid();
    }

    public Code findByCode(String activeCode) {
        QueryWrapper<Code> wrapper = new QueryWrapper<>();
        wrapper.eq("active_code", activeCode);
        return code.selectOne(wrapper);
    }

    public List<Code> findByFid(int fid) {
        QueryWrapper<Code> wrapper = new QueryWrapper<>();
        wrapper.eq("fid", fid);
        return code.selectList(wrapper);
    }

    public boolean del(int cid) {
        code.setCid(cid);
        return code.deleteById();
    }
}
