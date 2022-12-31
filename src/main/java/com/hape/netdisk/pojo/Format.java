package com.hape.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@TableName("format")
@Data
public class Format extends Model<Format> {
    @TableId(type = IdType.AUTO)
    private int fid;//格式id
    @TableField("format_name")
    private String formatName;//格式名
    private int cid;//分类id
    @TableField(exist = false)
    private Category category;//分类
}
