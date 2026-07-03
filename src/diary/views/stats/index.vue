<script setup lang="ts">
import {onMounted, ref, nextTick} from 'vue';
import * as echarts from 'echarts';
import {myAxios} from '@/common/utils/axios';

const pieRef = ref<HTMLElement | null>(null);
const calendarRef = ref<HTMLElement | null>(null);
const barRefs = ref<Record<string, HTMLElement>>({});
const total = ref(0);
const categoryCount = ref<Record<string, number>>({});
const subcategoryData = ref<Record<string, Record<string, number>>>({});
const dailyData = ref<[string, number][]>([]);

const colors = ['#FF6B8A', '#FF9F43', '#FFD93D', '#6BCB77', '#4D96FF', '#9B59B6'];
const catColors: Record<string, string> = {};

onMounted(async () => {
  const res = await myAxios({ method: 'get', url: '/diary/logs/stats' });
  const data = res.data.data;

  total.value = data.total;
  categoryCount.value = data.categoryCount || {};
  subcategoryData.value = data.subcategoryCount || {};
  dailyData.value = (data.dailyCount || []).map((d: any) => [d[0], d[1]]);

  const sorted = Object.entries(data.categoryCount || {}).sort((a: any, b: any) => b[1] - a[1]);
  sorted.forEach(([name]: any, i) => { catColors[name] = colors[i % colors.length]; });

  await nextTick();
  renderCalendar();
  renderPie();
  renderBars();
});

function renderPie() {
  if (!pieRef.value) return;
  const entries = Object.entries(categoryCount.value).sort((a, b) => b[1] - a[1]);

  const chart = echarts.init(pieRef.value);
  chart.setOption({
    animationDuration: 800,
    animationEasing: 'elasticOut',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: 'rgba(184,160,122,0.3)',
      borderWidth: 1,
      borderRadius: 8,
      padding: [8, 12],
      textStyle: { color: '#5a4a42', fontSize: 12 },
      formatter: '{b}: <strong>{c}</strong>条 ({d}%)',
    },
    legend: {
      orient: 'vertical', right: '5%', top: 'center',
      textStyle: { color: '#5a4a42', fontSize: 12 },
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 12,
    },
    series: [{
      type: 'pie',
      radius: ['42%', '72%'],
      center: ['38%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#f5e8d0',
        borderWidth: 3,
      },
      label: {
        show: true,
        formatter: '{b}: {d}%',
        color: '#5a4a42',
        fontSize: 11,
      },
      emphasis: {
        scale: true,
        itemStyle: {
          shadowBlur: 15,
          shadowColor: 'rgba(255,107,138,0.3)',
        },
      },
      data: entries.map(([name, value], i) => ({
        name, value,
        itemStyle: { color: colors[i % colors.length] },
      })),
    }],
  });
  window.addEventListener('resize', () => chart.resize());
}

function renderCalendar() {
  if (!calendarRef.value || dailyData.value.length === 0) return;
  const dates = dailyData.value.map(d => d[0]).sort();
  const start = dates[0];
  const end = dates[dates.length - 1];

  const chart = echarts.init(calendarRef.value);
  chart.setOption({
    animationDuration: 800,
    animationEasing: 'elasticOut',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: 'rgba(184,160,122,0.3)',
      borderWidth: 1,
      borderRadius: 8,
      padding: [8, 12],
      textStyle: { color: '#5a4a42', fontSize: 12 },
      formatter: (p: any) => {
        const v = p.value[1] as number;
        return `<strong>${p.value[0]}</strong><br/>${v} 条日志`;
      },
    },
    visualMap: {
      min: 0,
      max: Math.max(...dailyData.value.map(d => d[1]), 3),
      calculable: false,
      orient: 'horizontal',
      left: 'center',
      bottom: 0,
      itemWidth: 14,
      itemHeight: 130,
      inRange: {
        color: ['rgba(184,160,122,0.06)', '#FFE8E0', '#FFB7B2', '#FF6B8A'],
      },
      textStyle: { color: '#b8a07a', fontSize: 10 },
    },
    calendar: {
      left: 20,
      right: 20,
      top: 30,
      bottom: 55,
      range: [start, end],
      cellSize: ['auto', 16],
      splitLine: { lineStyle: { color: '#f5e8d0', width: 2 } },
      itemStyle: {
        borderWidth: 2,
        borderColor: '#f5e8d0',
        color: 'rgba(184,160,122,0.06)',
        borderRadius: 4,
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 8,
          shadowColor: 'rgba(255,107,138,0.3)',
          borderColor: '#FF6B8A',
        },
      },
      dayLabel: { color: '#b8a07a', fontSize: 10, nameMap: 'zh' },
      monthLabel: { color: '#5a4a42', fontSize: 11 },
    },
    series: [{
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: dailyData.value,
      blur: { itemStyle: { opacity: 0.5 } },
    }],
  });
}

function renderBars() {
  const catNames = Object.keys(subcategoryData.value).sort(
    (a, b) => (categoryCount.value[b] || 0) - (categoryCount.value[a] || 0)
  );

  catNames.forEach(catName => {
    const entries = Object.entries(subcategoryData.value[catName]).sort((a, b) => b[1] - a[1]);
    if (entries.length === 0) return;

    const el = barRefs.value[catName];
    if (!el) return;
    el.style.height = Math.max(160, entries.length * 36) + 'px';

    const chart = echarts.init(el);
    chart.setOption({
      animationDuration: 600,
      animationEasing: 'cubicOut',
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255,255,255,0.95)',
        borderColor: 'rgba(184,160,122,0.3)',
        borderWidth: 1,
        borderRadius: 8,
        padding: [8, 12],
        textStyle: { color: '#5a4a42', fontSize: 12 },
      },
      grid: { left: 10, right: 40, top: 10, bottom: 10, containLabel: true },
      xAxis: {
        type: 'value',
        axisLabel: { color: '#b8a07a', fontSize: 11 },
        splitLine: { lineStyle: { color: 'rgba(184,160,122,0.08)' } },
        axisLine: { show: false },
        axisTick: { show: false },
      },
      yAxis: {
        type: 'category',
        data: entries.map(e => e[0]),
        axisLabel: { color: '#5a4a42', fontSize: 11 },
        axisLine: { show: false },
        axisTick: { show: false },
      },
      series: [{
        type: 'bar',
        data: entries.map(([name, value]) => ({
          value,
          itemStyle: {
            color: catColors[catName] || colors[0],
            borderRadius: [0, 6, 6, 0],
          },
        })),
        barMaxWidth: 24,
        label: {
          show: true, position: 'right',
          formatter: '{c}条', color: '#5a4a42', fontSize: 11,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0,0,0,0.1)',
          },
        },
      }],
    });
  });
}
</script>

<template>
  <div class="stats-page stats-scroll">
    <div class="stats-header">
      <h2>📊 日记统计</h2>
      <div class="total-card">
        <span class="total-num">{{ total }}</span>
        <span class="total-label">总条目</span>
      </div>
    </div>

    <div class="chart-container" v-if="total > 0">
      <div ref="calendarRef" style="width: 100%; height: 230px"></div>
    </div>

    <div class="chart-container" v-if="total > 0">
      <div ref="pieRef" style="width: 100%; height: 340px"></div>
    </div>

    <div v-if="Object.keys(subcategoryData).length > 0" class="sub-bars">
      <div v-for="catName in Object.keys(subcategoryData).sort()" :key="catName" class="bar-card">
        <h3 class="bar-title" :style="{ color: catColors[catName] }">{{ catName }} - 事项分布</h3>
        <div :ref="(el: any) => { if (el) barRefs[catName] = el }" style="width: 100%; height: 200px"></div>
      </div>
    </div>

    <div v-else class="empty-state">暂无事项数据，写日记时添加事项试试</div>
  </div>
</template>

<style scoped>
.stats-page {
  padding: 20px;
  max-width: 700px;
  margin: 0 auto;
  max-height: 95%;
  overflow-y: auto;
  height: 100%;
}

.stats-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.stats-header h2 {
  margin: 0;
  color: #5a4a42;
  font-family: 'Georgia', serif;
}

.total-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(184, 160, 122, 0.3);
  border-radius: 8px;
}

.total-num {
  font-size: 28px;
  font-weight: bold;
  color: #5a4a42;
}

.total-label {
  font-size: 12px;
  color: #b8a07a;
}

.chart-container {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(184, 160, 122, 0.2);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.sub-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-card {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(184, 160, 122, 0.2);
  border-radius: 12px;
  padding: 16px;
}

.bar-title {
  margin: 0 0 8px;
  font-size: 14px;
  font-family: 'Georgia', serif;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #b8a07a;
}

</style>


<style>
.stats-scroll::-webkit-scrollbar { width: 8px; }
.stats-scroll::-webkit-scrollbar-thumb { background-color: rgba(255,255,255,0.4); border-radius: 4px; }
.stats-scroll::-webkit-scrollbar-track { background-color: rgba(255,255,255,0.1); border-radius: 4px; }
</style>
