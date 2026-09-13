package com.hpu.xinqingcommon.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmoResult {
    String label;
    Double score;
}
