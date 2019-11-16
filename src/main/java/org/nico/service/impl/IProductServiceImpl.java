package org.nico.service.impl;

import com.google.common.collect.Lists;
import org.nico.common.RedisRateLimit;
import org.nico.service.IProductService;
import org.nico.vo.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IProductServiceImpl implements IProductService {
    @Override
    @RedisRateLimit(key = "PollingService:polling:{0}",once = true,per = 4000L)
    public List<Product> list(String userId) {
        return Lists.newArrayList(
                Product.builder().id(1L).price(3.5F).productCode("0001OSFSS").productName("苹果笔记本").Description("新款").build(),
                Product.builder().id(1L).price(3.8F).productCode("0001OSFSS").productName("苹果笔记本").Description("新款").build(),
                Product.builder().id(1L).price(4.5F).productCode("0001OSFSS").productName("苹果笔记本").Description("新款").build()
        );
    }
}
