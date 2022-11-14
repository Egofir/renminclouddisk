package com.renmin.renminclouddisk.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveWord {
    private Integer id;
    private String value;
    private Integer type;
    private Date createTime;
    private Date updateTime;
}
