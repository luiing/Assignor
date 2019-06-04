package com.uis.connector.multith;

import java.util.Map;
/**
 * @author uis
 */
public class MultithResponse {
    public Map<String,Object> result;

    public MultithResponse(Map<String, Object> result) {
        this.result = result;
    }
}
