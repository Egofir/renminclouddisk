package com.renmin.renminclouddisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("format")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Format extends Model<Format> {
    @TableId(type = IdType.AUTO)
    private int fid;
    @TableField("format_name")
    private String formatName;
    private int cid;
    @TableField(exist = false)
    private Category category;
}
