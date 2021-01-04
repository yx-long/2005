package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spec_group")
public class SpceGroupEntity {

    @Id
    private Integer id;

    private String name;

    private Integer cid;

}
