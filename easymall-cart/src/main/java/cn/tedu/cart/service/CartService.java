package cn.tedu.cart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jt.common.pojo.Cart;
import com.jt.common.pojo.Product;

import cn.tedu.cart.mapper.CartMapper;

@Service
public class CartService {
    @Autowired
    private CartMapper cartMapper;

    public List<Cart> queryMyCarts(String userId) {
        return cartMapper.selectCartsByUserId(userId);
    }

    @Autowired
    private RestTemplate client;

    public void saveMyCart(Cart cart) {
        //查询已有
        Cart exist = cartMapper.selectExistByUserIdAndProductId(cart);
        if (exist != null) {
            //已存在 更新num的数量
            //更新内存对象数据
            cart.setNum(cart.getNum() + exist.getNum());
            cartMapper.updateCartNumByUserIdAndProductId(cart);
        } else {//没数据 新增购物车
            //获取商品服务返回的数据，封装补齐cart对象
            Product prod =
                    client.getForObject("http://productservice/product" + "/manage/item/" + cart.getProductId(), Product.class);
            cart.setProductImage(prod.getProductImgurl());
            cart.setProductName(prod.getProductName());
            cart.setProductPrice(prod.getProductPrice());
            //调用insert语句
            cartMapper.insertCart(cart);
        }
    }

    public void updateMyCartNum(Cart cart) {
        cartMapper.updateCartNumByUserIdAndProductId(cart);
    }

    public void deleteMyCart(Cart cart) {
        cartMapper.deleteCartByUserIdAndProductId(cart);

    }

}
