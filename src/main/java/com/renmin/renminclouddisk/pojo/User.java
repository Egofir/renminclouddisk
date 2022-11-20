package com.renmin.renminclouddisk.pojo;

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
    private int uid;
    private String uname;
    private String passwd;
    private int gender;
    private String birthday;
    private String city;
    private double total;
    private double remain;
    private int role;
}
