package com.leyou.service.mapper;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    //添加商品分类和品牌中间表
    @Insert("insert into tb_category_brand (category_id, brand_id) values (#{cid}, #{bid})")
    int saveCategoryBrand(@Param("cid")Long cid,@Param("bid") Long bid);

    //删除商品品牌
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteCategoryBrandByBid(@Param("bid") Long bid);

    @Select("select * from tb_category where id in (select  category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> queryCategoryByBid(Long bid);

    @Select("select * from tb_brand where id in (select brand_id  from tb_category_brand where category_id = #{cid})")
    List<Brand> queryBrandByCid(Long cid);
}
