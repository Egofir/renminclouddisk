package com.renmin.renminclouddisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("renminclouddisk_file")
public class File extends Model<File> {
    @TableId(type = IdType.AUTO)
    private int fid;
    @TableField("f_name")
    private String fName;
    @TableField("file_size")
    private long fileSize;
    @TableField("is_dir")
    private int isDir;
    @TableField("upload_time")
    private String uploadTime;
    @TableField("update_time")
    private String updateTime;
    @TableField("parent_id")
    private int parentId;
    private String path;
    private int uid;
    private int cid;
    @TableField("format_id")
    private int formatId;
    @TableField(exist = false)
    private List<File> file;
    @TableField(exist = false)
    private User user;
    @TableField(exist = false)
    private Category category;
    @TableField(exist = false)
    private Format format;
}
