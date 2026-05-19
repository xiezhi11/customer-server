@echo off
echo ========================================
echo  售后工单处理系统 - 后端启动脚本
echo ========================================
echo.
echo 正在检查 Maven...
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未找到 Maven，请先安装 Maven 并配置环境变量
    echo 下载地址: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo [信息] Maven 版本:
call mvn -version
echo.
echo [信息] 正在编译并启动后端服务...
echo [信息] 服务将在 http://localhost:8080 启动
echo [信息] H2控制台: http://localhost:8080/h2-console
echo.
echo ========================================
echo  按 Ctrl+C 停止服务
echo ========================================
echo.

call mvn clean spring-boot:run

if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败，请检查上面的错误信息
    pause
)
