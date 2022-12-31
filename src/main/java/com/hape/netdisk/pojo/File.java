package com.hape.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Scope("prototype")
@TableName("file")
@Data
public class File extends Model<File> {
    @TableId(type = IdType.AUTO)
    private int fid;//文件id
    @TableField("f_name")
    private String fName;//用户提交的文件名
    @TableField("file_size")
    private long fileSize;//文件大小(byte)
    @TableField("is_dir")
    private int isDir;//是否是文件夹,1为文件夹,0为文件
    @TableField("upload_time")
    private String uploadTime;//上传时间
    @TableField("update_time")
    private String updateTime;//更新时间
    @TableField("parent_id")
    private int parentId;//父文件id
    private String path;//文件路径
    private int uid;//用户id
    private int cid;//分类id
    @TableField("format_id")
    private int formatId;//格式id
    @TableField(exist = false)
//    private MultipartFile file;//文件
    private List<File> file;//子文件
    @TableField(exist = false)
    private User user;//用户
    @TableField(exist = false)
    private Category category;//分类
    @TableField(exist = false)
    private Format format;//格式
}
