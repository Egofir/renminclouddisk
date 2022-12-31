package com.hape.netdisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hape.netdisk.pojo.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FileDao {
    @Autowired
    private File file;

    /**
     * 添加文件
     * @param file
     * @return
     */
    public int insert(File file){
        if(!file.insert())return 0;
        return file.getFid();
    }

    /**
     * 根据父文件id查询
     * @param parentId
     * @return
     */
    public List<File> findByParentId(int parentId,int uid){
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",parentId);
        wrapper.eq("uid",uid);
        return file.selectList(wrapper);
    }

    /**
     * 根据文件id查询
     * @param fid
     * @return
     */
    public File findByFid(int fid){
        file.setFid(fid);
        return file.selectById();
    }

    /**
     * 根据当前目录 种类id和文件名查询
     * @param cid
     * @param fName
     * @return
     */
    public List<File> findByParentIdCidAndFName(int pid,int cid,String fName,int uid){
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        if(cid!=0)wrapper.eq("cid",cid);
        wrapper.eq("parent_id",pid);
        wrapper.like("f_name",fName);
        wrapper.eq("uid",uid);
        return file.selectList(wrapper);
    }

    /**
     * 根据激活码查询文件
     * @param code
     * @return
     */
    public File findByCode(String code){
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("code",code);
        return file.selectOne(wrapper);
    }

    /**
     * 修改文件
     * @param file
     * @return
     */
    public boolean update(File file){
        return file.updateById();
    }

    /**
     * 删除文件
     * @param fid
     * @return
     */
    public boolean del(int fid){
        file.setFid(fid);
        return file.deleteById();
    }
}
