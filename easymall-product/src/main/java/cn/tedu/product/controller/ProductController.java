package cn.tedu.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jt.common.pojo.Product;
import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;

import cn.tedu.product.service.ProductService;

@RestController
@RequestMapping("/product/manage")
public class ProductController {
    @Autowired
    private ProductService productService;

    //分页查询功能
    @RequestMapping("pageManage")
    public EasyUIResult queryPageProducts(Integer page, Integer rows) {
        //在业务层封装EasyUIResult对象
        EasyUIResult result = productService.queryPageProducts(page, rows);
        return result;
    }

    //单个商品查询,利用productId 搜索一条商品数据
    @RequestMapping("item/{productId}")
    public Product queryProduct(@PathVariable String productId) {
        return productService.queryProduct(productId);
    }

    //新增商品
    @RequestMapping("save")
    public SysResult deployProduct(Product product) {
        //判断成功失败的逻辑
        try {
            productService.deployProduct(product);
            //表示执行成功 200成功其他表示失败
            return SysResult.ok();
            //{"status":200,"msg":"ok","data":null}
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, e.getMessage(), null);
        }
    }

    //商品的更新
    @RequestMapping("update")
    public SysResult renewProduct(Product product) {
        try {
            productService.renewProduct(product);
            //成功调用返回200 reslt
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, e.getMessage(), null);
        }

    }


}
