package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_brand")
public class BrandEntity {

    @Id
    private Long id;

    private String name;

    private String image;

    private String letter;

}
