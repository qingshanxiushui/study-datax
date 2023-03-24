package com.alibaba.datax.plugin.writer.httpwriter;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteRecordDto {
    private List<WriteColumnDto> columns= new ArrayList<WriteColumnDto>();
    public void addColumn(WriteColumnDto column) {
        columns.add(column);
    }

    public int getColumnSize(){return columns.size();}
}
