# GlowXQ OJ 数据目录初始化脚本 (Windows PowerShell)
# 用于创建 Docker 容器所需的宿主机目录

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  GlowXQ OJ 数据目录初始化" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# 获取脚本所在目录
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$DataDir = Join-Path $ScriptDir "data"

Write-Host "数据目录: $DataDir" -ForegroundColor Yellow
Write-Host ""

# 创建所需的目录
Write-Host "正在创建目录..." -ForegroundColor Green

$Directories = @(
    "testcase",
    "file",
    "log",
    "run",
    "spj",
    "interactive"
)

foreach ($Dir in $Directories) {
    $FullPath = Join-Path $DataDir $Dir
    if (-not (Test-Path $FullPath)) {
        New-Item -ItemType Directory -Path $FullPath -Force | Out-Null
        Write-Host "✓ 创建目录: $Dir" -ForegroundColor Green
    } else {
        Write-Host "✓ 目录已存在: $Dir" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  初始化完成！" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "提示：" -ForegroundColor Yellow
Write-Host "1. 数据目录已创建在: $DataDir"
Write-Host "2. 现在可以运行: docker-compose up -d"
Write-Host "3. 测试用例文件将保存在: $DataDir\testcase"
Write-Host "4. 日志文件将保存在: $DataDir\log"
Write-Host ""

