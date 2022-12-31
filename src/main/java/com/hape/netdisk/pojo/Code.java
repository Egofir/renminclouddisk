package com.hape.netdisk.pojo;

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
    private int cid;//激活码id
    @TableField("active_code")
    private String activeCode;//激活码
    @TableField("share_time")
    private String shareTime;//分享时间
    @TableField("end_time")
    private String endTime;//截止时间
    private int fid;//文件id
    @TableField(exist = false)
    private File file;//文件
}
