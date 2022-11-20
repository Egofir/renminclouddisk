package com.renmin.renminclouddisk.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@TableName("category")
@Data
public class Category extends Model<Category> {
    @TableId(type = IdType.AUTO)
    private int cid;
    private String cname;
}
