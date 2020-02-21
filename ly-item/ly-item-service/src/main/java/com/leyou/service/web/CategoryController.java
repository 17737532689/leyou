package com.leyou.service.web;

import com.leyou.item.pojo.Category;
import com.leyou.service.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点查询商品类型
     *
     * @return
     */
    @RequestMapping("list")
    public ResponseEntity<List<Category>> queryByParentId(
            @RequestParam(value = "pid", defaultValue = "0") Long pid
    ) {

        List<Category> categories = categoryService.queryCategoryByPid(pid);

        return ResponseEntity.ok(categories);

    }

    /**
     * 根据商品分类ids先查询
     *
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {

        List<Category> categories = categoryService.queryCategoryByIds(ids);

        return ResponseEntity.ok(categories);
    }

    /**
     * 根据cid3查询三级分类
     *
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id) {
        List<Category> categories = categoryService.queryAllByCid3(id);
        return ResponseEntity.ok(categories);
    }
}
