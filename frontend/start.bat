@echo off
echo ========================================
echo  售后工单处理系统 - 前端启动脚本
echo ========================================
echo.
echo 正在检查 Node.js...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js，请先安装 Node.js
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)

echo [信息] Node.js 版本:
call node -v
echo [信息] npm 版本:
call npm -v
echo.

if not exist "node_modules" (
    echo [信息] 首次启动，正在安装依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)

echo.
echo [信息] 正在启动前端服务...
echo [信息] 页面将在 http://localhost:8081 打开
echo.
echo ========================================
echo  按 Ctrl+C 停止服务
echo ========================================
echo.

call npm run serve
