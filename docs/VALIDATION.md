# 1.0.0 验证记录

验证日期：2026-10-01。验证对象为本仓库整理后的 Android 源码，versionName `1.0.0`、versionCode `50`。

## 本版实际执行

| 检查 | 结果 |
| --- | --- |
| JVM 单元测试 | 398 项通过，0 失败、0 错误、0 跳过 |
| Debug APK 构建 | 通过 |
| 签名 Release APK 构建 | 通过 |
| Release Lint | 0 Error/Fatal，681 Warning、20 Hint |
| API-only 隐私静态检查 | 20/20 通过 |
| APK 元数据 | 包名 `com.apoorvdarshan.calorietracker.privatezh`，versionName `1.0.0`，versionCode `50` |
| APK 签名 | apksigner 验证通过，证书与 r8.1 一致 |
| 发布文件检查 | 无个人配置、签名私钥、API 密钥模式命中、SDK 或构建缓存；保留必要许可和 Wrapper |

发布签名证书 SHA256：`2a020ad5a44c1f2007b86e10b102113a86164ebc52051c86bca89c65cba95d44`。

### 构建方法

JDK 17、Android SDK Platform 37.2、Build Tools 36.0.0，使用本机已缓存依赖运行：

```bash
cd android
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease :app:lintRelease \
  -PworkoutVectors=none -Pkotlin.compiler.execution.strategy=in-process \
  --no-daemon --max-workers=2 --offline --console=plain
```

首次在新环境构建时需去掉 `--offline` 下载依赖，并配置 `ANDROID_HOME`。发布签名在构建时通过被 Git 忽略的本机配置注入；上传源码不包含该配置和签名私钥。

最初受限进程构建遇到 Gradle 缓存 JAR 的 `AccessDeniedException`；使用本机用户权限及独立 Gradle 进程后，以上完整任务集通过。没有跳过编译或单元测试。

## 沿用 r8.1 的功能验收证据

r8.1 已在独立 QA 模拟器包中验证饮水新增及重启保留、断食开始与结束、Gemini 模拟推荐请求不含查询工具、关闭/开启思考时分别传递 3072/8192 Token，以及三个方案显示。体重与体脂权限组合、权限查询和写入故障、取消传播、较大 Gemini 回复预算由本版重跑的单元测试覆盖。

1.0.0 从该修复基线整理发布，保留相同应用功能。本轮未再次安装应用或修改模拟器内用户数据；签名兼容性通过证书比对确认，不将其表述为本轮覆盖安装实测。

## 未覆盖范围

- 未调用真实云端 AI 服务，不保证各提供商、模型和代理的真实输出质量与额度。
- 未实测真机相机和 Health Connect 系统权限/写入。
- 静态网络限制检查不等于设备级抓包验证。
- Lint 仍有上述警告，不宣称零警告。

发布包的文件摘要见 Release 附件 `SHA256SUMS.txt`。源码包由 `v1.0.0` 对应 Git 提交导出。
