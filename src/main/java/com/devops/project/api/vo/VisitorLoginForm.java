package com.devops.project.api.vo;

import lombok.Data;

@Data
public class VisitorLoginForm {

    private String visitor_id;
    private String refer;
    private String refer_url;
    private String url;
    private String to_id;
    private String avator;
    private String user_agent;
    private String extra;
    private String client_ip;
    private String city_address;
    private String visitor_name;
    private Integer ent_id;
    private String dept_id;


}
