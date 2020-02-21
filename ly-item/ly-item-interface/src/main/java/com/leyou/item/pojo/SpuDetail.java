package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spu_detail")
public class SpuDetail {
    @Id
    private Long spuId;

    private String description;
    //通用规格参数数据
    private String genericSpec;
    //特殊规格参数数据
    private String specialSpec;
    //包装清单
    private String packingList;

    //售后服务
    private String afterService;




}
