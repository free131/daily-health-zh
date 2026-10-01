# 每日健康记录 1.0.0

基于 Fud AI 的 Android 中文定制版本，整合 r8.1 审查修复并采用独立版本号。

- Android 8.0 及以上；versionName 1.0.0，versionCode 50。
- 沿用已有包名与签名，可覆盖此前同签名的 r 系列安装包。不要先卸载旧版。
- 保留中文饮食、饮水、训练、体重与体脂记录，AI API 由用户自行配置。
- 包含饮食推荐数据一致性、饮水/断食入口、体脂同步权限及 Gemini 回复预算四项修复。
- 包含 AI 中文回复要求、思考模式、餐次选择与餐食复用菜单改进。

下载 `Daily-Health-ZH-1.0.0.apk` 安装；`Daily-Health-ZH-1.0.0-source.zip` 为对应版本源码，编译环境见仓库 README。使用 `SHA256SUMS.txt` 校验文件完整性。

本次 398 项单元测试全部通过；Debug/Release 构建、签名校验及隐私静态检查通过。Release Lint 无错误，仍有 681 项警告。

验证结果见仓库 [docs/VALIDATION.md](https://github.com/free131/daily-health-zh/blob/v1.0.0/docs/VALIDATION.md)。真实云端 AI、真机相机及 Health Connect 写入尚未完成本版实测。
