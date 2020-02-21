package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Table(name = "tb_sku")
public class Sku {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long  id;
    private Long spuId;
    private String title;
    private String images;
    private Double  price;
    // 商品特殊规格的下标
    private String indexes;
    // 商品特殊规格的键值对
    private String  ownSpec;
    // 是否有效，逻辑删除用
    private Boolean enable;
    private Date createTime;
    private Date lastUpdateTime;
    @Transient
    private Integer stock;// 库存


}
