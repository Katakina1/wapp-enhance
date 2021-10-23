package com.xforceplus.wapp.repository.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * upc_test
 * @author 
 */
@Data
public class UpcTest implements Serializable {
    private Long id;

    private String upc;

    private String itemNo;

    private static final long serialVersionUID = 1L;
}