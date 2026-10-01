# 验证记录

## 已有 r7 交付（2026-10-01）

从本地交付结果核对：384 项 JVM 单元测试，0 失败、0 错误、0 跳过；Debug/Release 构建通过。Release lint：677 Warning、20 Hint，无 Error/Fatal。静态隐私检查 20/20。

独立模拟器验收包与本机模拟接口覆盖全天/当餐三个方案、错误重试、取消后迟到结果不显示、描述记录入口、360dp 宽及 1.3 倍字体。测试响应为合成样例。

未调用真实 AI 密钥与服务，新增饮食推荐未做真机测试；相机、Health Connect 和设备级流量未完成全面验证。

发布源码不包含个人记录、密钥、签名文件、模拟器截图或构建缓存。

## 公共源码副本复验（2026-10-01）

在不包含 local.properties、签名密钥或 keystore.properties 的发布副本中，使用本机 JDK 17 和 Android SDK，通过环境变量指定 SDK 与 Gradle 缓存。重新执行 384 项单元测试，全部通过；assembleDebug、assembleRelease、lintRelease 均成功。Release 为未签名 APK。

本次 lint 结果：{'Warning': 677, 'Hint': 20}，无 Error/Fatal。未增加真实 AI 或真机验证。

为避开本机旧 Gradle 后台进程的缓存权限问题，本次验证额外使用 --no-daemon --max-workers=2 -Pkotlin.compiler.execution.strategy=in-process。
