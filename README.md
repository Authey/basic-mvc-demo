# Java Spring-MVC
#### 一个基础SpringMVC演示项目 &nbsp;&nbsp; A basic Spring-MVC Demo Project
#### 持续更新中，想法或问题都可以和我分享 &nbsp;&nbsp; Keep updating, welcome to share any thoughts and questions

## 工具 &thinsp; Tools
+ Java 8 / Java v 1.8.0
+ IntelliJ IDEA 2023.3.2 Ultimate Edition
+ Apache Maven v 3.3.9
+ Oracle 数据库 &thinsp; Oracle Database 19.3.0

## 启动 &thinsp; Launching
+ 使用IDEA通过Git获取项目后，会自动安装Maven依赖  
  After importing this project from Git in IDEA, dependencies would be installed automatically
+ 点击左上角[文件]-[项目结构]，进入项目设置页面  
  Click [File]-[Project Structure], Open Project Settings Page
+ 在项目标签页选择SDK，并把语言等级设置为8  
  In Project tab, choose SDK and set language level as 8
+ 在模块标签页添加网页模块，部署文件为该项目中的web.xml文件，资源文件夹设置为webapp文件夹  
  In Modules tab, add a web module, set web.xml in this project as deployment and webapp as resource directory
+ 确保库标签页中包含Maven下载的所有依赖  
  Ensure all Maven dependencies exists in Libraries tab
+ 在Facets标签页下按照模块标签页同步配置部署文件及资源文件夹，并确保路径正确  
  In Facets tab, configure web, deployment and resource directory as in Modules tab, ensure all paths are correct
+ 在产品标签页添加一个未压缩的网页应用并选择配置的模块，输出路径配置为该项目的webapp文件夹  
  In Artifacts tab, add an exploded web application from modules, set webapp folder in this project as the output directory
+ 确保输出布局子标签页中的WEB-INF文件夹下存在classes及lib文件夹，并把右侧依赖全部添加到lib文件夹中  
  Ensure classes and lib directories exist under WEB-INF folder and add all dependencies right-hand to lib directory in output layout sub-tab

## 特性 &thinsp; Features

