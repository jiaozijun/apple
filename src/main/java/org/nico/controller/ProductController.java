package org.nico.controller;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.nico.product.entity.Person;
import org.nico.product.service.IUserService;
import org.nico.service.IProductService;
import org.nico.vo.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "v1/products")
@Slf4j
public class ProductController {

    @Resource
    private IUserService iUserService;

    @Resource
    private IProductService iProductService;

    @GetMapping("/{productCode}")
    public Product getProduct(@PathVariable("productCode")String productCode){
        log.info("getProductList");
        return Product.builder().id(1L).price(3.5F).productCode("0001OSFSS").productName("苹果笔记本").Description("新款").build();
    }


    @GetMapping("/list/{productCode}")
    public List<Product> getProductList(@PathVariable("productCode")String productCode,String userId){
        log.info("getProductList");
       return iProductService.list(userId);
    }


    @GetMapping("/getperson/{userId}")
    public Person getPerson(@PathVariable("userId")Integer userId){
        log.info("getProductList");
        return iUserService.getPerson(userId);
    }




}
