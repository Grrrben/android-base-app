package com.atog.grrrben.share.classes;

import java.io.Serializable;

/**
 * Just a pojo
 * @todo make parcelable
 */
public class User implements Serializable {
    public int id;
    public String username;
    public String email;
    public String uuid;
    public String createdAt;
}
