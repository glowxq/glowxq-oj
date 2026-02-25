<template>
  <div class="github-link" @click="openGithub">
    <el-tooltip content="GitHub" placement="bottom">
      <el-icon class="github-icon">
        <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
          <path d="M512 12.64c-282.752 0-512 229.216-512 512 0 226.208 146.688 418.144 350.08 485.824 25.6 4.736 35.008-11.104 35.008-24.64 0-12.192-0.48-52.544-0.704-95.328-142.464 30.976-172.512-60.416-172.512-60.416-23.296-59.168-56.832-74.912-56.832-74.912-46.464-31.776 3.52-31.136 3.52-31.136 51.392 3.616 78.464 52.768 78.464 52.768 45.664 78.272 119.776 55.648 148.992 42.56 4.576-33.088 17.856-55.68 32.512-68.48-113.728-12.928-233.216-56.864-233.216-253.024 0-55.904 19.936-101.568 52.672-137.44-5.312-12.896-22.848-64.96 4.96-135.488 0 0 42.88-13.76 140.8 52.48 40.832-11.36 84.64-17.024 128.16-17.248 43.488 0.192 87.328 5.888 128.256 17.248 97.728-66.24 140.64-52.48 140.64-52.48 27.872 70.528 10.336 122.592 5.024 135.488 32.832 35.84 52.608 81.536 52.608 137.44 0 196.64-119.776 239.936-233.856 252.608 18.336 15.904 34.72 47.04 34.72 94.816 0 68.512-0.608 123.648-0.608 140.512 0 13.632 9.216 29.6 35.168 24.576C877.44 942.592 1024 750.592 1024 524.64c0-282.784-229.248-512-512-512z" fill="currentColor"/>
        </svg>
      </el-icon>
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
const openGithub = () => {
  window.open('https://github.com/glowxq/glowxq-oj', '_blank');
};
</script>

<style scoped lang="scss">
.github-link {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  
  .github-icon {
    font-size: 18px;
    color: var(--el-header-text-color);
    transition: all 0.3s ease;
    
    &:hover {
      color: var(--el-color-primary);
      transform: scale(1.1);
    }
  }
}

// 深色模式适配
[data-theme='dark'] .github-link {
  .github-icon:hover {
    color: #58a6ff;
  }
}
</style>
