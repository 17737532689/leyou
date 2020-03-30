package com.leyou.user.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Table(name = "tb_user")
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Length(max = 30,min = 4,message = "用户长度只能在4-30之间")
    private String username;
    @Length(max = 30,min = 4,message = "用户密码只能在4-30之间")
    private String password;
    @Pattern(regexp = "^1[35678]\\d{9}$",message = "手机号格式不正确")
    private String phone;
    private Date created;
    private String salt;
}
