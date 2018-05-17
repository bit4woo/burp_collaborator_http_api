# burp_collaborator_http_api

A burp extender that let you use burp collaborator server within http api
一个让你可以通过http API调用burp的collaborator服务器的插件



接口说明：

生成payload：
http://127.0.0.1:8000/generatePayload

获取payload的记录：

http://127.0.0.1:8000/fetchFor?payload=e0f34wndn15gs5xyisqzw8nwyn4ds2

它可以接受的请求类型包括: http\https\DNS\SMTP\SMTPS\FTP；demo版本暂不区分，后续有空会继续优化，提供特定类型的查询和数据提取。

简单的python调用举例：
```
# !/usr/bin/env python
# -*- coding:utf-8 -*-
__author__ = 'bit4'
__github__ = 'https://github.com/bit4woo'

import requests

proxy = {"http": "http://127.0.0.1:8888", "https": "https://127.0.0.1:8888"}
url = "http://127.0.0.1:8000/generatePayload"
response = requests.get(url)
payload = response.text
print payload
requests.get("http://{0}".format(payload))
url = "http://127.0.0.1:8000/fetchFor?payload={0}".format(payload.split(".")[0])
res = requests.get(url)
print  res.content

```

部署说明：

to do

docker部署：

to do