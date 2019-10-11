package cn.tedu.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jt.common.pojo.Cart;
import com.jt.common.vo.SysResult;

import cn.tedu.cart.service.CartService;

@RestController
@RequestMapping("cart/manage")
public class CartController {
    @Autowired
    private CartService cartService;

    //查询我的购物车
    @RequestMapping("query")
    public List<Cart> queryMyCarts(String userId) {
        return cartService.queryMyCarts(userId);
    }

    //新增商品到我的购物车
    @RequestMapping("save")
    public SysResult saveMyCart(Cart cart) {
        //userId productId num
        try {
            cartService.saveMyCart(cart);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "新增购物车失败", null);
        }
    }

    //更新购物车num数量
    @RequestMapping("update")
    public SysResult updateMyCartNum(Cart cart) {
        try {
            cartService.updateMyCartNum(cart);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "更新失败", null);
        }
    }

    //购物车的删除
    @RequestMapping("delete")
    public SysResult deleteMyCart(Cart cart) {
        try {
            cartService.deleteMyCart(cart);
            return SysResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SysResult.build(201, "删除失败", null);
        }
    }
}













