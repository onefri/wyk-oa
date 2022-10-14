package com.example.emos.wx.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName tb_permission
 */
@Data
public class TbPermission implements Serializable {
    /**
     * 主键
     */
    private Object id;

    /**
     * 权限
     */
    private String permissionName;

    /**
     * 模块ID
     */
    private Object moduleId;

    /**
     * 行为ID
     */
    private Object actionId;

    private static final long serialVersionUID = 1L;
}