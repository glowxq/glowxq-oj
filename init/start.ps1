# GlowXQ OJ 系统一键部署脚本 (PowerShell)
# 作者: GlowXQ Team
# 版本: 1.0.0

# 设置错误处理
$ErrorActionPreference = "Stop"

# 颜色定义
function Write-ColorOutput($ForegroundColor) {
    $fc = $host.UI.RawUI.ForegroundColor
    $host.UI.RawUI.ForegroundColor = $ForegroundColor
    if ($args) {
        Write-Output $args
    }
    $host.UI.RawUI.ForegroundColor = $fc
}

function Log-Info($message) {
    Write-ColorOutput Blue "[INFO] $message"
}

function Log-Success($message) {
    Write-ColorOutput Green "[SUCCESS] $message"
}

function Log-Warning($message) {
    Write-ColorOutput Yellow "[WARNING] $message"
}

function Log-Error($message) {
    Write-ColorOutput Red "[ERROR] $message"
}

# 检查 Docker 和 Docker Compose
function Check-Docker {
    Log-Info "检查 Docker 环境..."
    
    try {
        $null = docker --version
    }
    catch {
        Log-Error "Docker 未安装，请先安装 Docker Desktop"
        exit 1
    }
    
    try {
        $null = docker-compose --version
    }
    catch {
        try {
            $null = docker compose version
        }
        catch {
            Log-Error "Docker Compose 未安装，请先安装 Docker Compose"
            exit 1
        }
    }
    
    Log-Success "Docker 环境检查通过"
}

# 检查必要文件
function Check-Files {
    Log-Info "检查必要文件..."
    
    if (-not (Test-Path "docker-compose.yml")) {
        Log-Error "docker-compose.yml 文件不存在"
        exit 1
    }
    
    if (-not (Test-Path "init.sql")) {
        Log-Error "init.sql 文件不存在"
        exit 1
    }
    
    if (-not (Test-Path ".env")) {
        Log-Warning ".env 文件不存在，将使用默认配置"
    }
    
    Log-Success "文件检查完成"
}

# 创建必要目录
function Create-Directories {
    Log-Info "创建必要目录..."
    
    # 创建日志目录
    New-Item -ItemType Directory -Force -Path "logs\mysql" | Out-Null
    New-Item -ItemType Directory -Force -Path "logs\redis" | Out-Null
    New-Item -ItemType Directory -Force -Path "logs\oj" | Out-Null
    
    Log-Success "目录创建完成"
}

# 停止现有服务
function Stop-Services {
    Log-Info "停止现有服务..."
    
    try {
        $services = docker-compose ps -q 2>$null
        if ($services) {
            docker-compose down
            Log-Success "现有服务已停止"
        }
        else {
            Log-Info "没有运行中的服务"
        }
    }
    catch {
        Log-Info "没有运行中的服务"
    }
}

# 启动服务
function Start-Services {
    Log-Info "启动 GlowXQ OJ 系统..."
    
    # 拉取最新镜像
    Log-Info "拉取最新镜像..."
    docker-compose pull
    
    # 启动服务
    Log-Info "启动服务容器..."
    docker-compose up -d
    
    Log-Success "服务启动完成"
}

# 等待服务就绪
function Wait-ForServices {
    Log-Info "等待服务就绪..."

    # 等待 MySQL 就绪
    Log-Info "等待 MySQL 服务就绪..."
    $timeout = 300
    while ($timeout -gt 0) {
        try {
            $result = docker-compose exec -T mysql mysqladmin ping -h localhost --silent 2>$null
            if ($LASTEXITCODE -eq 0) {
                Log-Success "MySQL 服务就绪"
                break
            }
        }
        catch {
            # 继续等待
        }
        Start-Sleep -Seconds 5
        $timeout -= 5
    }

    if ($timeout -le 0) {
        Log-Error "MySQL 服务启动超时"
        exit 1
    }

    # 等待 Redis 就绪
    Log-Info "等待 Redis 服务就绪..."
    $timeout = 60
    while ($timeout -gt 0) {
        try {
            $result = docker-compose exec -T redis redis-cli -a glowxq-oj-123 ping 2>$null
            if ($result -match "PONG") {
                Log-Success "Redis 服务就绪"
                break
            }
        }
        catch {
            # 继续等待
        }
        Start-Sleep -Seconds 2
        $timeout -= 2
    }

    if ($timeout -le 0) {
        Log-Error "Redis 服务启动超时"
        exit 1
    }
}

# 初始化数据库
function Initialize-Database {
    Log-Info "检查数据库初始化状态..."

    # 检查数据库是否存在
    try {
        $dbResult = docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 -e "SHOW DATABASES LIKE 'glowxq_oj';" 2>$null
        if (-not ($dbResult -match "glowxq_oj")) {
            Log-Error "数据库 glowxq_oj 不存在"
            exit 1
        }
        Log-Success "数据库 glowxq_oj 已存在"
    }
    catch {
        Log-Error "无法检查数据库状态"
        exit 1
    }

    # 检查关键表是否存在
    try {
        $tableResult = docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 -D glowxq_oj -e "SHOW TABLES;" 2>$null
        $tableCount = ($tableResult -split "`n").Count - 1

        if ($tableCount -lt 5) {
            Log-Warning "数据库表数量不足，开始执行数据初始化..."

            # 检查 init.sql 文件是否存在
            if (-not (Test-Path "init.sql")) {
                Log-Error "init.sql 文件不存在"
                exit 1
            }

            # 执行数据库初始化
            Log-Info "正在执行数据库初始化脚本..."
            $initResult = Get-Content "init.sql" | docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 2>$null
            if ($LASTEXITCODE -eq 0) {
                Log-Success "数据库初始化完成"
            } else {
                Log-Error "数据库初始化失败"
                exit 1
            }
        } else {
            Log-Success "数据库已包含 $($tableCount-1) 个表，无需重新初始化"
        }
    }
    catch {
        Log-Warning "无法检查表状态，尝试执行初始化脚本..."
        Get-Content "init.sql" | docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 2>$null
    }

    # 验证关键表
    Log-Info "验证关键数据表..."
    $tables = @("sys_user", "applet_user", "code_monitor", "course", "judge", "problem")
    $missingTables = @()

    foreach ($table in $tables) {
        try {
            $tableResult = docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 -D glowxq_oj -e "SHOW TABLES LIKE '$table';" 2>$null
            if (-not ($tableResult -match $table)) {
                $missingTables += $table
            }
        }
        catch {
            $missingTables += $table
        }
    }

    if ($missingTables.Count -eq 0) {
        Log-Success "所有关键表验证通过"
    } else {
        Log-Warning "缺失的表: $($missingTables -join ', ')"
        Log-Info "尝试重新执行初始化脚本..."
        Get-Content "init.sql" | docker-compose exec -T mysql mysql -u root -pglowxq-oj-123 2>$null
    }
}

# 等待 OJ 应用就绪
function Wait-ForApplication {
    Log-Info "等待 OJ 应用服务就绪..."
    $timeout = 180
    while ($timeout -gt 0) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:7101/health" -TimeoutSec 5 -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) {
                Log-Success "OJ 应用服务就绪"
                return $true
            }
        }
        catch {
            # 继续等待
        }
        Start-Sleep -Seconds 10
        $timeout -= 10
    }

    Log-Warning "OJ 应用服务启动可能需要更多时间，请稍后检查"
    return $false
}

# 显示服务状态
function Show-Status {
    Log-Info "服务状态："
    docker-compose ps
    
    Write-Output ""
    Log-Info "服务访问地址："
    Write-Output "  - OJ 系统: http://localhost:7101"
    Write-Output "  - MySQL: localhost:3307"
    Write-Output "  - Redis: localhost:6380"
    
    Write-Output ""
    Log-Info "默认账号信息："
    Write-Output "  - MySQL root 密码: glowxq-oj-123"
    Write-Output "  - Redis 密码: glowxq-oj-123"
}

# 主函数
function Main {
    Write-Output "========================================"
    Write-Output "    GlowXQ OJ 系统一键部署脚本"
    Write-Output "========================================"
    Write-Output ""

    Check-Docker
    Check-Files
    Create-Directories
    Stop-Services
    Start-Services
    Wait-ForServices
    Initialize-Database
    Wait-ForApplication
    Show-Status

    Write-Output ""
    Log-Success "GlowXQ OJ 系统部署完成！"
    Write-Output ""
    Log-Info "如需停止服务，请运行: docker-compose down"
    Log-Info "如需查看日志，请运行: docker-compose logs -f [服务名]"
    Log-Info "更多信息请查看 init.md 文档"
}

# 执行主函数
try {
    Main
}
catch {
    Log-Error "部署过程中发生错误: $($_.Exception.Message)"
    exit 1
}
