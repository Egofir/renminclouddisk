package com.renmin.renminclouddisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@TableName("code")
@Data
public class Code extends Model<Code> {
    @TableId(type = IdType.AUTO)
    private int cid;
    @TableField("active_code")
    private String activeCode;
    @TableField("share_time")
    private String shareTime;
    @TableField("end_time")
    private String endTime;
    private int fid;
    @TableField(exist = false)
    private File file;
}
