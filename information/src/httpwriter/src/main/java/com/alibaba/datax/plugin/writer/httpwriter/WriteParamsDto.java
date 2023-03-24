package com.alibaba.datax.plugin.writer.httpwriter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteParamsDto {
    private List<WriteRecordDto> records= new ArrayList<WriteRecordDto>();
    public void addRecord(WriteRecordDto  record) {
        records.add(record);
    }
}
