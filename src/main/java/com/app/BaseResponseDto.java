package com.app;

import java.util.HashMap;
import java.util.Map;

public class BaseResponseDto {
    public Map<String, String> body;

    public BaseResponseDto() {
        this.body = new HashMap<>();
    }

    public Map<String, String> getBody() {
        return this.body;
    }
}
