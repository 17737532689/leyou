package com.leyou.service.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.vo.BrandVo;
import com.leyou.service.service.BrandService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**分页查询
     *
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @RequestMapping("page")
    public ResponseEntity<PageResult<Brand>>
    queryBrandByPage(
                  @RequestParam(value = "page",defaultValue = "1") Integer page,
                  @RequestParam(value = "rows",defaultValue = "5") Integer rows,
                  @RequestParam(value = "sortBy",required = false)   String sortBy,
                  @RequestParam(value = "desc",defaultValue = "false")   Boolean desc,
                  @RequestParam(value = "key",required = false)   String key){

        PageResult<Brand> brandPageResult = brandService.queryBrandAndSort(page, rows, sortBy, desc, key);

        return ResponseEntity.ok(brandPageResult);
    }

    /**
     * 添加
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addBrand(Brand brand, @RequestParam("cids")List<Long> cids){
            brandService.saveBrand(brand,cids);
            return new  ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * 删除
     * @param bid
     * @return
     */
    @Delete("bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid){
        brandService.deleteBrand(bid);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新品牌
     * @param brandVo
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandVo brandVo){
        brandService.updateBrand(brandVo);
        return ResponseEntity.ok().build();
    }

    /**
     *根据分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {

        List<Brand> brands = brandService.queryBrandByCid(cid);

        return ResponseEntity.ok(brands);

    }

    /**
     * 根据品牌id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
        Brand brand = brandService.queryByBid(id);

        return  ResponseEntity.ok(brand);
    }

    /**
     * 根据ids查询品牌
     * @param ids
     * @return
     */
    @GetMapping("list")
    public  ResponseEntity<List<Brand>> queryBrandByids(@RequestParam("ids") List<Long> ids){

        List<Brand> brands = brandService.queryBrandByids(ids);

        return ResponseEntity.ok(brands);
    }

}
