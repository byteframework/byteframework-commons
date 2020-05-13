//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.byteframework.commons.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseNode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long parentId;

    private List<BaseNode> childNode;

    public BaseNode() {
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }


    public List<BaseNode> getChildNode() {
        if (this.childNode == null) {
            this.childNode = new ArrayList<>();
        }
        return this.childNode;
    }

    public void setChildNode(List<BaseNode> childNode) {
        this.childNode = childNode;
    }
}
