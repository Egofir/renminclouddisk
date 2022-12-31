package com.hape.netdisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hape.netdisk.pojo.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CodeDao {
    @Autowired
    private Code code;

    /**
     * 插入
     * @param code
     * @return
     */
    public int insert(Code code){
        if(!code.insert())return 0;
        return code.getCid();
    }

    /**
     * 根据激活码查询
     * @param activeCode
     * @return
     */
    public Code findByCode(String activeCode){
        QueryWrapper<Code> wrapper = new QueryWrapper<>();
        wrapper.eq("active_code",activeCode);
        return code.selectOne(wrapper);
    }

    /**
     * 根据文件id查询
     * @param fid
     * @return
     */
    public List<Code> findByFid(int fid){
        QueryWrapper<Code> wrapper = new QueryWrapper<>();
        wrapper.eq("fid",fid);
        return code.selectList(wrapper);
    }

    /**
     * 删除
     * @param cid
     * @return
     */
    public boolean del(int cid){
        code.setCid(cid);
        return code.deleteById();
    }
}
