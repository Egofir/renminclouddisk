package com.renmin.renminclouddisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.renmin.renminclouddisk.pojo.File;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class FileDao {
    @Resource
    private File file;

    public int insert(File file) {
        if (!file.insert()) {
            return 0;
        }
        return file.getFid();
    }

    public List<File> findByParentId(int parentId, int uid) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        wrapper.eq("uid", uid);
        return file.selectList(wrapper);
    }

    public File findByFid(int fid) {
        file.setFid(fid);
        return file.selectById();
    }

    public List<File> findByParentIdCidAndFName(int pid, int cid, String fName, int uid) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        if (cid != 0) {
            wrapper.eq("cid", cid);
        }
        wrapper.eq("parent_id", pid);
        wrapper.like("f_name", fName);
        wrapper.eq("uid", uid);
        return file.selectList(wrapper);
    }

    public boolean update(File file) {
        return file.updateById();
    }

    public boolean del(int fid) {
        file.setFid(fid);
        return file.deleteById();
    }
}
