package com.hape.netdisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@TableName("user")
@Data
public class User extends Model<User> {
    @TableId(type = IdType.AUTO)
    private int uid;//用户id
    private String uname;//用户名
    private String passwd;//密码
    private int gender;//性别,1为男
    private String birthday;//出生日期
    private String city;//所在地
    private double total;//网盘总容量（G）
    private double remain;//网盘剩余容量（G）
    private int role;//身份标识,1为普通用户,0为管理员
}
