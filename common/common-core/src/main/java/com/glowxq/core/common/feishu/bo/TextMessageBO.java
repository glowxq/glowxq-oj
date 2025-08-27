package com.glowxq.core.common.feishu.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author glowxq
 * @version 1.0
 * @date 2023/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextMessageBO implements Serializable {

    @JsonProperty("msg_type")
    private String msgType;

    @JsonProperty("content")
    private Content content;

    public TextMessageBO(String message) {
        this.msgType = "text";
        this.content = new Content(message);
    }

    @Data
    @NoArgsConstructor
    public static class Content {

        @JsonProperty("text")
        private String text;

        public Content(String text) {
            this.text = text;
        }
    }
}
