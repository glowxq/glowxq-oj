<template>
  <el-select
    v-model="selectedValue"
    :placeholder="placeholder"
    :loading="loading"
    :remote-method="remoteMethod"
    @change="updateValue"
    remote
    filterable
    remote-show-suffix
    clearable
  >
    <el-option
      v-for="item in options"
      :key="item[fieldMappings.key]"
      :label="item[fieldMappings.label]"
      :value="item[fieldMappings.value]"
    />
  </el-select>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue';
import { ElSelect, ElOption } from 'element-plus';
defineOptions({
  name: 'RemoteSearchSelect'
});
const props = defineProps({
  modelValue: [String, Number],
  placeholder: String,
  fetchOptions: {
    type: Function,
    required: true
  },
  api: {
    type: Function,
    default: (query: any) => query
  },
  fieldMappings: {
    type: Object,
    default: () => ({
      key: 'id',
      label: 'label',
      value: 'value'
    })
  }
});

const emits = defineEmits(['update:modelValue']);

const selectedValue = ref(props.modelValue);
const loading = ref(false);
const options = ref<any[]>([]);

const remoteMethod = async (query: string) => {
  console.log('remoteMethod triggered with query:', query);
  if (query) {
    loading.value = true;
    try {
      const queryParams = props.api(query);
      const result = await props.fetchOptions(queryParams);
      options.value = result.data;
    } catch (error) {
      console.error('Error fetching options:', error);
    } finally {
      loading.value = false;
    }
  }
};

const updateValue = (value: any) => {
  emits('update:modelValue', value);
};

watch(
  () => props.modelValue,
  newValue => {
    selectedValue.value = newValue;
  }
);
</script>
