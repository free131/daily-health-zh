# 每日健康记录 · Daily Health ZH

基于 [Fud AI](https://github.com/apoorvdarshan/fud-ai) 的 Android 中文定制版本，集中记录饮食、饮水、训练、体重与体脂。手动记录可离线使用，AI 功能由用户自行配置 API。

> 本项目为独立衍生版本，非 Fud AI 官方发行。当前源码版本为 `7.1.1-private-zh-r7`（versionCode 45），包名为 `com.apoorvdarshan.calorietracker.privatezh`。仓库名不改变现有应用名称或包名。

## 功能

- **日常记录**：今天、记录、训练、趋势四个主要页面；饮食、饮水及营养统计。
- **饮食录入**：手动填写、文字描述和拍照识别；AI 方式需要配置相应模型。
- **今日饮食推荐**：主动生成全天或指定餐次的三个方案，结果不会自动记为已摄入食物。
- **中文训练库**：内置 877 个动作的中文名称与说明，支持中英文搜索及训练记录。
- **身体趋势**：体重、可选体脂及历史记录，使用滚轮录入。
- **本地数据**：支持手动导入导出，可选 Health Connect 系统集成。

## 与上游的主要区别

增加中文界面、动作文本、日期/单位显示及 AI 中文输出要求；调整日常页面、训练入口和身体记录，增加饮食推荐。

本版本关闭自动更新、Google 登录/Drive 云备份、排行榜、在线条码查询、语音识别、模型下载与远程训练图片下载。原本依赖在线示意图的部分区域使用占位或文字。Android 自动备份和设备迁移已禁用，数据库与照片放在 `noBackupFilesDir`。

## 使用与数据流向

Android 8.0（API 26）及以上。首次启动可选择仅使用本地记录；需要 AI 时，在设置中填写供应商、接口地址、模型和自己的 API Key。拍照识别须选择支持图片输入的模型，供应商能力与收费以其实际服务为准。

应用 HTTP 请求通过 `ApiOnlyNetworkPolicy` 限制为 AI 适配器标记的 POST 请求，自动重定向关闭；自定义地址应填写最终接口地址。构建依赖下载不属于安装后应用的数据流量。

- 拍照识别会发送照片与补充文字；文字分析及聊天发送输入和功能所需上下文。
- 饮食推荐发送目标、当日饮食与营养合计、当日训练摘要、过敏原、补充要求和已配置的 AI 背景，不发送整个历史日记。
- 如果配置备用服务，失败请求可能继续发送至该服务。API Key 和记录应由使用者自行保管。
- Health Connect、系统文件选择器、分享接收应用或云相册有各自的数据处理规则。

卸载或清除数据会删除本机记录，请先手动导出。AI 输出和营养估算需要自行核对。

## 从源码构建

环境：JDK 17、Android SDK Platform 37.2、Build Tools 36.0.0。仓库包含 Gradle Wrapper 9.7.1，首次构建需联网下载依赖。依赖版本以 `android/gradle/libs.versions.toml` 为准。

在 Android Studio 中打开 `android/`，通过 SDK Manager 安装对应平台；也可在 Git Bash 中执行：

```bash
git clone https://github.com/free131/daily-health-zh.git
cd daily-health-zh/android
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export ANDROID_HOME="C:/Users/YOUR_USER/AppData/Local/Android/Sdk"
./gradlew :app:testDebugUnitTest :app:assembleDebug -PworkoutVectors=none
```

请将环境变量改为自己的安装位置。也可以通过 Android Studio 生成本机 `android/local.properties`。该文件不应提交。

Debug APK 位于 `android/app/build/outputs/apk/debug/app-debug.apk`，包名有 `.debug` 后缀，可与本地发布版共存。

```bash
# 发布检查；未配置签名时生成未签名 APK
./gradlew :app:assembleRelease :app:lintRelease -PworkoutVectors=none
```

发布签名参考 `android/keystore.properties.template`，填写自己的签名信息至被忽略的 `android/keystore.properties`。未签名 APK 不能直接安装；不同签名不能覆盖已有安装。本仓库不提供个人签名私钥。

本仓库仅包含 Android 构建所需源码和资源，不包含约 1.2 GB 的完整训练图片集；请使用 `-PworkoutVectors=none`，不要选择 `sample` 或 `all`。

## 目录

| 路径 | 内容 |
| --- | --- |
| `android/` | Kotlin / Jetpack Compose 工程、单元测试与 Gradle Wrapper |
| `ios/calorietracker/Resources/FreeExerciseDB/` | Android 共用动作 JSON 与原始数据许可；不是完整 iOS 工程 |
| `shared/workout-vectors/` | 训练图片清单，不含完整图片 |
| `local-models/legal/` | 上游保留的相关许可声明 |
| `docs/` | 来源说明、变更与验证范围 |

## 验证范围

r7 本地交付记录显示：384 项单元测试通过，Debug/Release 构建通过，Release lint 无 Error/Fatal（仍有 677 个 Warning 和 20 个 Hint）。模拟器和模拟接口覆盖饮食推荐、失败重试、取消、小屏布局等流程。本次公开源码副本也已重新通过 384 项测试、Debug/未签名 Release 构建与 lint 检查。详见 [验证说明](docs/VALIDATION.md)。

真实 AI 服务的输出质量、调用额度、真机相机和 Health Connect 数据交换未完成全面验证，不将模拟接口结果作为真实模型能力证明。

## 来源、引用与许可证

本项目基于 [apoorvdarshan/fud-ai](https://github.com/apoorvdarshan/fud-ai) 的 `android-v7.1.1`，基线提交为 [`78526cac206d9680aef480284372273d2f813416`](https://github.com/apoorvdarshan/fud-ai/tree/78526cac206d9680aef480284372273d2f813416)。感谢原作者 Apoorv Darshan 及贡献者。本仓库以当前 Android 定制源码快照建立独立历史。

- 项目代码沿用 [MIT License](LICENSE)，保留原作者版权声明。
- 动作数据来自 [Free Exercise DB](https://github.com/yuhonas/free-exercise-db)，见 [数据来源说明](docs/FREE_EXERCISE_DB.md) 和 [Unlicense](ios/calorietracker/Resources/FreeExerciseDB/LICENSE.md)。
- 肌群图标来自 [react-muscle-highlighter](https://github.com/soroojshehryar/react-muscle-highlighter)，相关 MIT 声明保留在 [ASSET_CREDITS.md](ASSET_CREDITS.md)。
- 供应商图标来自 [Lobe Icons](https://github.com/lobehub/lobe-icons) 和 [Simple Icons](https://github.com/simple-icons/simple-icons)，见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
- 上游素材说明中涉及的在线条码、远程图片和 iOS 功能不代表本分支仍提供这些功能；品牌与商标归各自权利人所有。

修改、分发时请继续保留适用的版权和许可声明。
