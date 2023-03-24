# -*- coding:utf-8 -*-
import sys
import datetime
import os
  

# 获取前1天或N天的日期，beforeOfDay=1：前1天；beforeOfDay=N：前N天
def getdate(beforeOfDay):
    today = datetime.datetime.now()
    # 计算偏移量
    offset = datetime.timedelta(days=-beforeOfDay)
    # 获取想要的日期的时间
    re_date = (today + offset).strftime('%Y-%m-%d') # #号可以去除0
    return re_date

def process_datax(configFilePath,logFilePath,start_time,end_time,card_no,clinic_code):
    print('datax param, %s,%s,%s,%s,%s,%s' % (configFilePath,logFilePath,start_time,end_time,card_no,clinic_code))
    script2execute = "python D:\\DataX\\datax\\bin\\datax.py %s -p \"-Dstart_time=\\\"'%s'\\\" -Dend_time=\\\"'%s'\\\" -Dcard_no='%s' -Dclinic_code='%s' \" >> %s"%(configFilePath,start_time,end_time,card_no,clinic_code,logFilePath)
    print("to be excute script:"+ script2execute)
    os.system(script2execute)

if __name__ == '__main__':
    if len(sys.argv) == 5:
        configFilePath = sys.argv[1]
        logFilePath = sys.argv[2]
        print(getdate(1))
        start_time = getdate(1) + ' 00:00:00'
        print(getdate(0)) 
        end_time = getdate(0) + ' 00:00:00'
        card_no = sys.argv[3]
        clinic_code = sys.argv[4]
        process_datax(configFilePath,logFilePath,start_time,end_time,card_no,clinic_code)
    elif len(sys.argv) == 7:
        configFilePath = sys.argv[1]
        logFilePath = sys.argv[2]
        start_time = sys.argv[3] + ' 00:00:00'
        start_time = start_time
        end_time = sys.argv[4] + ' 00:00:00'
        end_time = end_time
        card_no = sys.argv[5]
        clinic_code = sys.argv[6]
        process_datax(configFilePath,logFilePath,start_time,end_time,card_no,clinic_code)
    else:
        print("param count wrong")
    
