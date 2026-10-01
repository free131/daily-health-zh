package com.apoorvdarshan.calorietracker.services.ai
internal object ResponseLanguage {
    fun instruction(): String = "\n\n所有面向用户的食物名称、食材名称、解释、建议和回答统一使用自然简洁的简体中文。JSON 键、枚举值、标准份量单位代码、模型 ID 和数值保持原样。不要给中文词语添加英文复数后缀。保留用户原始输入，不编造翻译。"
}
