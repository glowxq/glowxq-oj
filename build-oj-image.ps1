# GlowXQ OJ Docker镜像构建脚本 (PowerShell版本)
# 用于测试修复后的Dockerfile是否能正常构建

param(
    [switch]$SkipTest,
    [switch]$KeepImage
)

# 颜色函数
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# 检查Docker是否可用
function Test-Docker {
    Write-Info "检查Docker环境..."
    
    try {
        $null = docker --version
        $null = docker info
        Write-Success "Docker环境检查通过"
        return $true
    }
    catch {
        Write-Error "Docker未安装或未启动，请先安装并启动Docker Desktop"
        return $false
    }
}

# 检查必要文件
function Test-Files {
    Write-Info "检查必要文件..."
    
    if (-not (Test-Path "app/app-oj/Dockerfile")) {
        Write-Error "Dockerfile不存在: app/app-oj/Dockerfile"
        return $false
    }
    
    Write-Success "Dockerfile文件检查通过"
    return $true
}

# 构建镜像
function Build-Image {
    Write-Info "开始构建Docker镜像..."
    
    # 设置镜像名称和标签
    $imageName = "glowxq-oj"
    $imageTag = "test-$(Get-Date -Format 'yyyyMMdd-HHmmss')"
    $fullImageName = "${imageName}:${imageTag}"
    
    Write-Info "镜像名称: $fullImageName"
    Write-Info "构建上下文: $(Get-Location)"
    
    # 构建镜像
    Write-Info "执行Docker构建..."
    try {
        docker build -f app/app-oj/Dockerfile -t $fullImageName .
        if ($LASTEXITCODE -eq 0) {
            Write-Success "镜像构建成功！"
            Write-Success "镜像名称: $fullImageName"
            
            # 显示镜像信息
            Write-Info "镜像详细信息:"
            docker images $fullImageName
            
            return $fullImageName
        }
        else {
            Write-Error "镜像构建失败！"
            return $null
        }
    }
    catch {
        Write-Error "构建过程中发生错误: $($_.Exception.Message)"
        return $null
    }
}

# 测试镜像
function Test-Image {
    param([string]$ImageName)
    
    if ($SkipTest) {
        Write-Info "跳过镜像测试"
        return $true
    }
    
    Write-Info "测试镜像中的Java安装..."
    
    try {
        docker run --rm $ImageName /usr/local/java/bin/java -version
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Java安装测试通过！"
            return $true
        }
        else {
            Write-Error "Java安装测试失败！"
            return $false
        }
    }
    catch {
        Write-Error "测试过程中发生错误: $($_.Exception.Message)"
        return $false
    }
}

# 清理测试镜像
function Remove-TestImage {
    param([string]$ImageName)
    
    if ($KeepImage) {
        Write-Info "保留测试镜像: $ImageName"
        return
    }
    
    $response = Read-Host "是否删除测试镜像 $ImageName? (y/N)"
    if ($response -match "^[Yy]$") {
        try {
            docker rmi $ImageName
            Write-Success "测试镜像已删除"
        }
        catch {
            Write-Warning "删除镜像时发生错误: $($_.Exception.Message)"
        }
    }
    else {
        Write-Info "保留测试镜像: $ImageName"
    }
}

# 主函数
function Main {
    Write-Host ""
    Write-Success "========================================="
    Write-Success "GlowXQ OJ Docker镜像构建测试脚本"
    Write-Success "========================================="
    Write-Host ""
    
    if (-not (Test-Docker)) {
        exit 1
    }
    
    if (-not (Test-Files)) {
        exit 1
    }
    
    $imageName = Build-Image
    if ($null -eq $imageName) {
        Write-Error "构建失败，请检查错误信息"
        exit 1
    }
    
    if (Test-Image -ImageName $imageName) {
        Write-Success "所有测试通过！Dockerfile修复成功！"
    }
    else {
        Write-Error "镜像测试失败"
        exit 1
    }
    
    Write-Host ""
    Write-Info "构建完成！"
    Remove-TestImage -ImageName $imageName
}

# 执行主函数
Main
