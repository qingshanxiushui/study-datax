package com.alibaba.datax.plugin.writer.httpwriter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteColumnDto {
    private Object rawData;
}
