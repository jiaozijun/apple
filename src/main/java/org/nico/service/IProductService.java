package org.nico.service;

import org.nico.vo.Product;

import java.util.List;

public interface IProductService {

    List<Product> list(String userId);
}
