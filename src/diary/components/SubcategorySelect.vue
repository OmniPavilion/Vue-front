<script setup lang="ts">
import {ref, computed, watch, onMounted, onUnmounted} from 'vue';
import {subcategoryApi, type Subcategory} from "@/diary/api/subcategoryApi";

const props = defineProps<{
  modelValue: string | undefined;
  categoryId: number;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string | undefined];
}>();

const open = ref(false);
const inputText = ref('');
const items = ref<Subcategory[]>([]);
const dropdownRef = ref<HTMLElement | null>(null);
const inputRef = ref<HTMLInputElement | null>(null);

const filteredItems = computed(() => {
  if (!inputText.value) return items.value;
  return items.value.filter(i => i.name.toLowerCase().includes(inputText.value.toLowerCase()));
});

const loadItems = async () => {
  const res = await subcategoryApi.listByCategory(props.categoryId);
  items.value = res.data.data;
};

const toggle = async () => {
  if (!open.value) {
    await loadItems();
    open.value = true;
    setTimeout(() => inputRef.value?.focus(), 50);
  } else {
    close();
  }
};

const close = () => {
  open.value = false;
  inputText.value = '';
};

const selectItem = async (name: string) => {
  emit('update:modelValue', name);
  open.value = false;
  inputText.value = '';
};

const handleInputKeydown = async (e: KeyboardEvent) => {
  if (e.key === 'Enter' && inputText.value.trim()) {
    const name = inputText.value.trim();
    const exists = items.value.some(i => i.name === name);
    if (!exists) {
      await subcategoryApi.create(name, props.categoryId);
      await loadItems();
    }
    emit('update:modelValue', name);
    open.value = false;
    inputText.value = '';
  }
  if (e.key === 'Escape') {
    close();
  }
};

const deleteItem = async (name: string) => {
  await subcategoryApi.deleteSubcategory(name);
  items.value = items.value.filter(i => i.name !== name);
};

const clearValue = () => {
  emit('update:modelValue', undefined);
};

const handleClickOutside = (e: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    close();
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <div ref="dropdownRef" class="subcategory-dropdown">
    <div class="subcategory-trigger" @click="toggle">
      <span v-if="modelValue" class="selected-text">{{ modelValue }}</span>
      <span v-else class="placeholder">事项</span>
      <div class="trigger-actions">
        <span v-if="modelValue" class="clear-btn" @click.stop="clearValue">&times;</span>
        <span class="arrow" :class="{ open }">▾</span>
      </div>
    </div>

    <Transition name="drop">
      <div v-if="open" class="dropdown-panel">
        <div class="search-box">
        <input
            ref="inputRef"
            v-model="inputText"
            type="text"
            placeholder="搜索或输入创建..."
            @keydown="handleInputKeydown"
        />
      </div>
      <div class="option-list">
        <div
            v-for="item in filteredItems"
            :key="item.name"
            class="option-item"
            :class="{ active: modelValue === item.name }"
            @click="selectItem(item.name)"
        >
          <span>{{ item.name }}</span>
          <span class="option-delete" @click.stop="deleteItem(item.name)">&times;</span>
        </div>
        <div v-if="filteredItems.length === 0" class="option-empty">
          {{ inputText ? '按 Enter 创建' : '暂无事项' }}
        </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.subcategory-dropdown {
  position: relative;
  width: 130px;
  flex-shrink: 0;
}

.subcategory-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 32px;
  padding: 0 8px;
  background-color: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(184, 160, 122, 0.3);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
  box-sizing: border-box;
}

.subcategory-trigger:hover {
  background-color: rgba(255, 255, 255, 0.75);
  border-color: rgba(184, 160, 122, 0.5);
}

.selected-text {
  color: #5a4a42;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.placeholder {
  color: #b8a07a;
  flex: 1;
}

.trigger-actions {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-shrink: 0;
}

.clear-btn {
  color: #b8a07a;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  padding: 0 2px;
}

.clear-btn:hover {
  color: #b85c5c;
}

.arrow {
  color: #b8a07a;
  font-size: 10px;
  transition: transform 0.25s ease;
}

.arrow.open {
  transform: rotate(180deg);
}

.dropdown-panel {
  position: absolute;
  top: calc(100% + 2px);
  left: 0;
  right: 0;
  z-index: 2000;
  background-color: #f5e8d0;
  border: 1px solid #b8a07a;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.search-box {
  padding: 6px;
  border-bottom: 1px solid rgba(184, 160, 122, 0.2);
}

.search-box input {
  width: 100%;
  height: 28px;
  padding: 0 8px;
  border: 1px solid rgba(184, 160, 122, 0.3);
  border-radius: 4px;
  background-color: rgba(255, 255, 255, 0.7);
  color: #5a4a42;
  font-size: 12px;
  outline: none;
  box-sizing: border-box;
}

.search-box input:focus {
  border-color: #b8a07a;
  background-color: rgba(255, 255, 255, 0.9);
}

.option-list {
  max-height: 180px;
  overflow-y: auto;
}

.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 10px;
  font-size: 12px;
  color: #5a4a42;
  cursor: pointer;
  transition: background 0.1s;
}

.option-item:hover {
  background-color: rgba(184, 160, 122, 0.2);
}

.option-item.active {
  color: #8b6b4a;
  font-weight: 600;
}

.option-delete {
  color: #b8a07a;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.15s;
  padding: 0 2px;
}

.option-item:hover .option-delete {
  opacity: 1;
}

.option-delete:hover {
  color: #b85c5c;
}

.option-empty {
  padding: 12px;
  text-align: center;
  color: #b8a07a;
  font-size: 12px;
}

.drop-enter-active {
  transition: all 0.2s ease-out;
}

.drop-leave-active {
  transition: all 0.15s ease-in;
}

.drop-enter-from {
  opacity: 0;
  transform: translateY(-4px);
}

.drop-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
