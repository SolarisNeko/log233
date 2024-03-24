package com.neko233.log233.api.constant

object Log233Constant {
    // 环境变量中的配置文件路径  | classpath 则从 resources/ 下加载
    const val CONFIG_FILE_ENV_KEY = "log233.configFilePath"

    // 默认配置文件
    const val CONFIG_FILE_DEFAULT_PATH = "classpath:log233-default.xml"

}