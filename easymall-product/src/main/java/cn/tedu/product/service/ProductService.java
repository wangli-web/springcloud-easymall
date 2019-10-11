package cn.tedu.product.service;

import java.util.List;
import java.util.UUID;

import com.jt.common.utils.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.common.pojo.Product;
import com.jt.common.vo.EasyUIResult;

import cn.tedu.product.mapper.ProductMapper;
import redis.clients.jedis.JedisCluster;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;

    public EasyUIResult queryPageProducts(Integer page, Integer rows) {
        //准备EasyUIResult
        EasyUIResult result = new EasyUIResult();
        //total
        int total = productMapper.selectProductCount();
        result.setTotal(total);
        //rows List<Product>对象
        int start = (page - 1) * rows;
        List<Product> pList = productMapper.selectProductList(start, rows);
        result.setRows(pList);
        return result;
    }

    @Autowired
    private JedisCluster cluster;

    public Product queryProduct(String productId) {
        //引入缓存判断更新锁的存在
        //生成当前逻辑中需要的key
        //锁的key product_update_productId+.lock
        String updateLock = "product_update_" + productId + ".lock";
        String productKey = "product_" + productId;
        //判断锁是否存在
        try {
            if (cluster.exists(updateLock)) {
                //锁存在,有人更新数据,缓存就不能使用
                return productMapper.selectProductById(productId);
            } else {
                //锁不存在,判断缓存逻辑
                if (cluster.exists(productKey)) {
                    //缓存命中,解析productJson
                    //src就是json字符串
                    //value class对象解析的类反射对象
                    return MapperUtil.MP.readValue(cluster.get(productKey), Product.class);
                } else {
                    //查持久层
                    Product product = productMapper.selectProductById(productId);
                    //加入缓存 维护少量数据的写入缓存
                    cluster.setex(productKey, 60 * 60 * 2, MapperUtil.MP.writeValueAsString(product));
                    return product;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deployProduct(Product product) {
        //TODO 新增缓存 新增热点数据商品
        //补齐数据 uuid保存productId
        product.setProductId(UUID.randomUUID().toString());
        //103e5414-0da2-4fba-b92f-0ba876e08939
        productMapper.insertProduct(product);
    }

    //
    public void renewProduct(Product product) {
        /*1 加锁(超时时间5分钟)
         *2 删除缓存productKey
         *3 更新数据库
         *4 释放锁
         */
        String updateLock = "product_update_" + product.getProductId() + ".lock";
        String productKey = "product_" + product.getProductId();
        cluster.setex(updateLock, 60 * 5, "");
        cluster.del(productKey);
        productMapper.updateProductById(product);
        cluster.del(updateLock);
    }


}
