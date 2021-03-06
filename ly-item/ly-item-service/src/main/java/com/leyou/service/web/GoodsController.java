package com.leyou.service.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.service.service.GoodsService;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(

        @RequestParam(value = "page",defaultValue = "1") Integer page,
        @RequestParam(value = "rows",defaultValue = "5") Integer rows,
        @RequestParam(value = "key",required = false) String key,
        @RequestParam(value = "saleable",required = false) Boolean saleable
    ){
        PageResult<Spu> spuPageResult = goodsService.querySpuByPage(page, rows, key, saleable);
        return ResponseEntity.ok(spuPageResult);
    }

    /**
     * 查找详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail>  querySpuDetailBySpuId(@PathVariable("spuId") Long spuId){
        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(spuId));
    }

    /**
     * 根据spuid查找商品
     * @param
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        return ResponseEntity.ok(goodsService.querySkuBySpuId(id));
    }

    /**
     *根据skus ids查询sku
     * @param ids
     * @return
     */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkusByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(goodsService.querySkusByIds(ids));
    }

    /**
     * 删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("spu/spuId/{spuId}")
    public ResponseEntity<Void> deleteGoodsBySpuId(@PathVariable("spuId") Long spuId){
        goodsService.deleteGoodsBySpuId(spuId);
        return ResponseEntity.ok().build();
    }
    /**
     * 添加商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> aadGoods(@RequestBody Spu spu){
        goodsService.aadGoods(spu);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 更新商品
     * @param spu
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.ok().build();
    }

    /**
     * 手动控制上下架
     * @param spu
     * @return
     */
    @PutMapping("spu/saleable")
    public ResponseEntity<Void> handleSaleable(@RequestBody Spu spu){
        goodsService.handleSaleable(spu);
        return ResponseEntity.ok().build();
    }
    /**
     * 根据spuId查询spu及skus
     * @param spuId
     * @return
     */
    @GetMapping("spu/{id}")
    public  ResponseEntity<Spu> querySpuBySpuId(@PathVariable("id") Long spuId){
        return ResponseEntity.ok(goodsService.querySpuBySpuId(spuId));
    }
}
