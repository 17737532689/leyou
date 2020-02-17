package com.leyou.item.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandVo {

    private Long id;

    private  String name;

    private  String image;

    private  Character letter;

    private List<Long> cids;
}
