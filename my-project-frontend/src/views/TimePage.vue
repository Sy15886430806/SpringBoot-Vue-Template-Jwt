<template>
  <div class="container">
    <el-image
        style="width: 100%; height: 100%; position: absolute; top: 0; left: 0; z-index: -1"
        fit="cover"
        src="https://5pw.net/i/2025/03/06/67c910a925b7e.jpg"
    />
    <div class="content-wrapper">
      <!-- 当前日期和星期 -->
      <div class="date-blurred">
        <div class="date" style="font-size: 30px">
          在线时钟
        </div>
        <div class="time" style="font-size: 40px">{{ currentDate }}</div>
        <div class="time" style="font-size: 60px">{{ currentTime }}</div>
      </div>

      <!-- 番茄时钟 -->
      <div class="pomodoro-blurred">
        <div class="time" style="font-size: 60px">番茄时钟</div>
        <div class="pomo-time" style="font-size: 100px">
          <span>{{ timerDisplay }}</span>
        </div>
        <div class="controls">
          <el-button @click="startFocus" :disabled="isRunning || isResting">开始专注 (25分钟)</el-button>
          <el-button @click="stopTimer" :disabled="!isRunning && !isResting">停止</el-button>
          <el-button @click="startRest" :disabled="isRunning || isResting">开始休息 (5分钟)</el-button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElButton, ElMessage } from 'element-plus';

const currentTime = ref('');
const currentDate = ref('');
const focusTime = 25 * 60; // 25分钟
const restTime = 5 * 60; // 5分钟
const timer = ref(focusTime);
const isRunning = ref(false); // 是否正在专注
const isResting = ref(false); // 是否正在休息
let interval;

// 格式化时间显示
const timerDisplay = computed(() => {
  const minutes = Math.floor(timer.value / 60);
  const seconds = timer.value % 60;
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
});

// 获取当前时间和日期
function updateTime() {
  const now = new Date();
  currentTime.value = now.toLocaleTimeString();
  currentDate.value = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${'日一二三四五六'[now.getDay()]}`;
}

onMounted(() => {
  updateTime();
  setInterval(updateTime, 1000);
});

// 启动专注计时
function startFocus() {
  if (!isRunning.value && !isResting.value) {
    isRunning.value = true;
    isResting.value = false;
    timer.value = focusTime; // 设置为专注时间
    interval = setInterval(decrementTimer, 1000);
  }
}

// 启动休息计时
function startRest() {
  if (!isRunning.value && !isResting.value) {
    isRunning.value = false;
    isResting.value = true;
    timer.value = restTime; // 设置为休息时间
    interval = setInterval(decrementTimer, 1000);
  }
}

// 停止计时
function stopTimer() {
  clearInterval(interval); // 停止当前计时
  timer.value = 25 * 60; // 清空当前时间
  isRunning.value = false; // 停止计时器
  isResting.value = false; // 停止休息状态
  ElMessage.warning('已停止计时，请重新开始');
}

// 计时器递减
function decrementTimer() {
  if (timer.value > 0) {
    timer.value--;
  } else {
    clearInterval(interval); // 停止计时
    if (isResting.value) {
      isRunning.value = false;
      isResting.value = false;
      ElMessage.success('休息结束，准备开始专注');
    } else {
      isRunning.value = false;
      isResting.value = false;
      ElMessage.success('专注结束，开始休息');
    }
  }
}
</script>

<style scoped>
.container {
  text-align: center;
  margin-top: 50px;
}

.content-wrapper {
  position: relative;
  z-index: 1;
}

.date-blurred, .pomodoro-blurred {
  backdrop-filter: blur(4px); /* 毛玻璃效果 */
  border-radius: 10px;
  padding: 10px;
  margin: 20px auto;
  width: 600px; /* 设置宽度为1000px */
  max-width: 100%; /* 确保在小屏幕上自适应 */
}


.date {
  color: #d4d7de;
  margin-bottom: 20px;
  font-size: 24px;
}

.time {
  color: #d4d7de;
  font-size: 48px;
  font-weight: bold;
}

.pomo-time {
  color: #d4d7de;
  font-size: 48px;
  font-weight: bold;
}

.controls {
  display: flex;
  justify-content: center;
  gap: 20px;
}

</style>
