package com.example.emos.wx.db.pojo;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName tb_face_model
 */
@Data
public class TbFaceModel implements Serializable {
    /**
     * 主键值
     */
    private Object id;

    /**
     * 用户ID
     */
    private Object userId;

    /**
     * 用户人脸模型
     */
    private String faceModel;

    private static final long serialVersionUID = 1L;
}