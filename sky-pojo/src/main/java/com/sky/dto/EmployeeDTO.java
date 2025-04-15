package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;//用户名

    private String name;//姓名

    private String phone;

    private String sex;

    private String idNumber;//身份证号

}
