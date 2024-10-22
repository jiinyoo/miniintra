package com.example.demo;

import lombok.Data;

@Data
public class SawonVo {
    private int id,level;
    private String sabun,name,pwd,email,phone,ipsa,writeday;
    
    private String depart;
    
    // 년,월,일을 분리하여 저장
    private int y,m,d;
}