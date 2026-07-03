<script setup lang="ts">
import {onMounted, ref, computed} from 'vue';
import {useLogStore} from "@/diary/stores";
import {Category, CategoryInfo} from "@/diary/types/vo/Category";
import {WeatherInfo} from "@/diary/types/vo/Weather";
const logStore = useLogStore();
const loading = ref(true);
const selectedYear = ref<number | null>(null);
const selectedMonth = ref<number | null>(null);

onMounted(async () => {
  logStore.pageQuery = { ...logStore.pageQuery, pageNum: 1, pageSize: 9999, query: undefined };
  await logStore.fetchLogsByPage();
  loading.value = false;
});

const years = computed(() => {
  const set = new Set<number>();
  logStore.logList.forEach(d => set.add(new Date(d.date).getFullYear()));
  return [...set].sort((a, b) => b - a);
});

const months = computed(() => {
  if (!selectedYear.value) return [];
  const set = new Set<number>();
  logStore.logList.forEach(d => {
    const year = new Date(d.date).getFullYear();
    const month = new Date(d.date).getMonth() + 1;
    if (year === selectedYear.value) set.add(month);
  });
  return [...set].sort((a, b) => b - a);
});

const filteredList = computed(() => {
  let list = logStore.logList;
  if (selectedYear.value) {
    list = list.filter(d => new Date(d.date).getFullYear() === selectedYear.value);
  }
  if (selectedMonth.value) {
    list = list.filter(d => new Date(d.date).getMonth() + 1 === selectedMonth.value);
  }
  return list;
});

const selectYear = (year: number) => {
  if (selectedYear.value === year) {
    selectedYear.value = null;
    selectedMonth.value = null;
  } else {
    selectedYear.value = year;
    selectedMonth.value = null;
  }
};

const selectMonth = (month: number) => {
  selectedMonth.value = selectedMonth.value === month ? null : month;
};

const formatDate = (dateStr: string) => {
  const d = new Date(dateStr);
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' });
};

const getWeatherInfo = (weather: string) => WeatherInfo[weather as keyof typeof WeatherInfo];
const getCategoryName = (cat: Category) => CategoryInfo[cat]?.name || cat;
const getCategoryColor = (cat: Category) => CategoryInfo[cat]?.color || '#999';
</script>

<template>
  <div class="history-page">
    <div class="history-sticky">
      <div class="page-header">
        <h2>📋 日志历史</h2>
        <span class="total-badge">
          {{ filteredList.length }} 天 /
          {{ filteredList.reduce((s, d) => s + d.logs.length, 0) }} 条
        </span>
      </div>

      <div class="filter-bar">
        <div class="filter-group">
          <span class="filter-label">年份</span>
          <div class="filter-options">
            <button
                v-for="y in years"
                :key="y"
                class="filter-btn"
                :class="{ active: selectedYear === y }"
                @click="selectYear(y)"
            >{{ y }}</button>
          </div>
        </div>
        <div v-if="selectedYear" class="filter-group">
          <span class="filter-label">月份</span>
          <div class="filter-options">
            <button
                v-for="m in months"
                :key="m"
                class="filter-btn"
                :class="{ active: selectedMonth === m }"
                @click="selectMonth(m)"
            >{{ m }}月</button>
          </div>
        </div>
      </div>
    </div>

    <div class="history-content history-scroll">
      <div v-if="loading" class="loading">加载中...</div>

      <div v-else-if="filteredList.length === 0" class="empty">暂无日志</div>

      <div v-else class="timeline">
        <div v-for="day in filteredList" :key="day.id" class="day-group">
        <div class="day-head">
          <span class="day-date">{{ formatDate(day.date) }}</span>
          <span class="day-weather">
            <i :class="getWeatherInfo(day.weather)?.icon"></i>
            {{ getWeatherInfo(day.weather)?.name }}
          </span>
        </div>
        <div class="day-entries">
          <div v-for="(log, i) in day.logs" :key="i" class="entry-row">
            <span class="entry-cat" :style="{ backgroundColor: getCategoryColor(log.category) }">
              {{ getCategoryName(log.category) }}
            </span>
            <span v-if="log.subcategory" class="entry-sub">{{ log.subcategory }}</span>
            <span class="entry-text">{{ log.activity }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<style scoped>
.history-page {
  max-width: 800px;
  margin: 0 auto;
  max-height: 95%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
}

.history-sticky {
  flex-shrink: 0;
}

.history-content {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  margin-top: 16px;
  padding-right: 4px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header h2 {
  margin: 0;
  color: #5a4a42;
  font-family: 'Georgia', serif;
}

.filter-bar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 12px;
  color: #b8a07a;
  width: 32px;
  flex-shrink: 0;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.filter-btn {
  padding: 4px 12px;
  font-size: 12px;
  color: #5a4a42;
  background: rgba(255,255,255,0.3);
  border: 1px solid rgba(184,160,122,0.2);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
  font-family: inherit;
}

.filter-btn:hover {
  background: rgba(184,160,122,0.15);
  border-color: rgba(184,160,122,0.4);
}

.filter-btn.active {
  color: #fff;
  background: #b8a07a;
  border-color: #b8a07a;
}

.total-badge {
  font-size: 12px;
  color: #b8a07a;
  background: rgba(184,160,122,0.15);
  padding: 2px 10px;
  border-radius: 10px;
  white-space: nowrap;
}

.loading, .empty {
  text-align: center;
  padding: 60px 20px;
  color: #b8a07a;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.day-group {
  background: rgba(255,255,255,0.25);
  border: 1px solid rgba(184,160,122,0.2);
  border-radius: 10px;
  overflow: hidden;
}

.day-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: rgba(184,160,122,0.1);
  border-bottom: 1px solid rgba(184,160,122,0.15);
}

.day-date {
  font-size: 14px;
  font-weight: 600;
  color: #5a4a42;
}

.day-weather {
  font-size: 12px;
  color: #b8a07a;
}

.day-entries {
  padding: 8px 16px;
}

.entry-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
  border-bottom: 1px solid rgba(184,160,122,0.08);
}

.entry-row:last-child {
  border-bottom: none;
}

.entry-cat {
  font-size: 11px;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
  flex-shrink: 0;
}

.entry-sub {
  font-size: 12px;
  color: #8b6b4a;
  background: rgba(184,160,122,0.12);
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
  flex-shrink: 0;
}

.entry-text {
  font-size: 13px;
  color: #5a4a42;
  flex: 1;
  word-break: break-word;
}
</style>

<style>
.history-scroll::-webkit-scrollbar { width: 8px; }
.history-scroll::-webkit-scrollbar-thumb { background-color: rgba(255,255,255,0.4); border-radius: 4px; }
.history-scroll::-webkit-scrollbar-track { background-color: rgba(255,255,255,0.1); border-radius: 4px; }
</style>
