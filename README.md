# bearApp

## Android 改动后的默认验证流程

后续只要涉及 Android 代码修改，默认按下面流程验证，不只停留在“代码已修改”：

1. 编译：
   `Set-Location 'D:\claudeCode\bearApp\android'; .\gradlew.bat :app:assembleDebug`

2. 安装到已连接设备：
   `adb install -r D:\claudeCode\bearApp\android\app\build\outputs\apk\debug\app-debug.apk`

3. 启动应用：
   `adb shell am start -n com.bear.asset/.MainActivity`

4. 验证是否存活：
   `adb shell pidof com.bear.asset`

5. 如有异常，抓取日志定位：
   `adb logcat -d -t 200 | Select-String -Pattern 'AndroidRuntime|FATAL EXCEPTION|com.bear.asset|Exception'`

说明：

- 默认优先完成“编译 -> 安装 -> 启动 -> 查日志”整套闭环。
- 如果 adb 已连接设备，应尽量直接在真机/已连接设备上验证。
- 若构建或启动失败，需要继续排查到明确原因，而不是只报告代码已修改。
