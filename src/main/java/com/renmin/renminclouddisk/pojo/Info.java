package com.renmin.renminclouddisk.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Info {
    private boolean status;
    private String msg;
}
