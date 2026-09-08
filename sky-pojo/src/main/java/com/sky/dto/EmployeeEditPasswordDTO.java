package com.sky.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class EmployeeEditPasswordDTO implements Serializable {
    //旧密码
    private String oldPassword;
    //新密码
    private String newPassword;
    //员工id
    private Long empId;
}
