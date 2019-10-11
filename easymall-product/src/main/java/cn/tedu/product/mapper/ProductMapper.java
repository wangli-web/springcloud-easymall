package cn.tedu.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jt.common.pojo.Product;

public interface ProductMapper {

    int selectProductCount();

    //在方法中存在多个参数，使用Param注解，实现parameter的翻译导入
    List<Product> selectProductList(@Param("start") int start, @Param("rows") Integer rows);

    Product selectProductById(String productId);

    void insertProduct(Product product);

    void updateProductById(Product product);

}
