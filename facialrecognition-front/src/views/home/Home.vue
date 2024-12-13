<template>
  <div class="home">
    <div class="left">
      首页内容
    </div>

    <div class="right">
      <div class="announcement">
        <div class="title">公告</div>
        <div class="scrollbar-demo">
          <el-scrollbar max-height="400px">
          <p v-for="item in 4" :key="item" class="scrollbar-demo-item">{{ item }}</p>
        </el-scrollbar>
        </div>
      </div >
      <div class="calendar">
        <el-calendar ref="calendar" :style="calendarStyle">
        <template #date-cell="{ data }">
          <p :class="data.isSelected ? 'is-selected' : ''">
            {{ data.day.split('-').slice(1).join('-') }}
            {{ data.isSelected ? '✔️' : '' }}
          </p>
        </template>
      </el-calendar>
      </div>
      
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      calendarStyle: {}
    };
  },
  methods: {
    adjustCalendarSize() {
      // 设置日历的宽度和高度
      this.calendarStyle = {
        width: '100%', // 根据父容器的宽度自动调整
        height: '500px' // 可以根据需要调整
      };
    }
  },

  mounted() {
    this.adjustCalendarSize();
  }
};
</script>

<style lang="scss" scoped>
.home {
  display: flex;
  height: 88vh; // 使整个容器占满整个视口高度

  .left {
    flex: 2; // 左边部分占 3 份
    display: flex;
    background-color: #f5f5f5;
  }

  .right {
    flex: 1; // 右边部分占 1 份
    display: flex;
    flex-direction: column; // 使子元素垂直排列
    justify-content: center;
    align-items: center;
    
    .announcement{
      flex: 1; // 公告部占 1 份
      background-color: #e0e0e0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .title{
        font-size: 20px;
        font-weight: bold;
        color :crimson;
      }

      .scrollbar-demo-item {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 50px;
        width: 400px;
        margin: 10px;
        text-align: center;
        border-radius: 4px;
        background: var(--el-color-primary-light-9);
        color: var(--el-color-primary);
      }
    }
    .calendar{
      flex: 1; // 日历部占 2 份
      background-color: #f5f5f5;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
</style>