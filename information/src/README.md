## 插件开发步骤

* (1)、新建模块。
* (2)、复制参考reader中的assembly包和resource包下的plugin.json、plugin_job_template.json,放入相应模块。
* (3)、修改第2步复制过来的文件。package.xml、plugin.json、plugin_job_template.json。
* (4)、新建插件代码包。继承Reader,内部类分别继承Reader.Job、Reader.Task。
* (5)、如果用到内部工具类，以情况进行相应调整。
* (6)、打包运行。在最外层的pom将其他模块注释只留下公共模块和自己的项目模块。在最外层的package.xml加上相应fileSet。打包命令: mvn -U clean package assembly:assembly -Dmaven.test.skip=true 。