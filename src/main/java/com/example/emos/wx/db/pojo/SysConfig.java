package com.example.emos.wx.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName sys_config
 */
@Data
public class SysConfig implements Serializable {
    /**
     * 主键
     */
    private Object id;

    /**
     * 参数名
     */
    private String paramKey;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}