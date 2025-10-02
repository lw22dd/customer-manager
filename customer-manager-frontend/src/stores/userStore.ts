import UserApi from '@/apis/userApi';
import { Result } from '@/models/Result';
import { CustomField, FormConfig, User } from '@/models/UserModel'
import { defineStore } from 'pinia'
import { computed, onMounted, ref } from 'vue'

//模拟数据
const mockApi = {
  // 获取会员列表
  getCustomers: async (): Promise<Result<User[]>> => {
    // 模拟本地存储数据
    const mockData: User[] = [
      {
        id: 1,
        name: '张三',
        phone: '13800138000',
        address: '北京市朝阳区建国路88号',
        email: '<EMAIL>',
        idCard: '110105199001011234',
        birthday: '1990-01-01',
        height: 180,
        weight: 75,
        shoeSize: '42',
        clothesSize: 'XL',
        athleteLevel: '1',
        refereeLevel: '2',
        familyHeritage: '父亲曾为省队运动员'
      },
      {
        id: 2,
        name: '李四',
        phone: '13900139000',
        address: '上海市浦东新区张江高科技园区',
        email: '<EMAIL>',
        idCard: '310115199505055678',
        birthday: '1995-05-05',
        height: 165,
        weight: 52,
        shoeSize: '37',
        clothesSize: 'M',
        athleteLevel: '',
        refereeLevel: '1',
        familyHeritage: ''
      },
      {
        id: 3,
        name: '王五',
        phone: '13700137000',
        address: '',
        email: '1554254454@qq.com',
        idCard: '440106200010109012',
        birthday: '1994-05-05',
        height: null,
        weight: null,
        shoeSize: '',
        clothesSize: '',
        athleteLevel: '3',
        refereeLevel: '',
        familyHeritage: '家族三代从事体育教育'
      },
      {
        id: 4,
        name: '赵六',
        phone: '13600136000',
        address: '广东省深圳市南山区科技园',
        email: '<EMAIL>',
        idCard: '440301198808083456',
        birthday: '1988-08-08',
        height: 175,
        weight: 68,
        shoeSize: '41',
        clothesSize: 'L',
        athleteLevel: '2',
        refereeLevel: '3',
        familyHeritage: ''
      }
    ];
    await new Promise(resolve => setTimeout(resolve, 100));

    return { code: 200, msg: 'success', data:mockData };
  },

  // 保存会员
  saveCustomer: async (user: User): Promise<Result<User>> => {
    const saved = localStorage.getItem('customers') || '[]';
    const list: User[] = JSON.parse(saved);

    if (user.id) {
      // 更新现有会员
      const index = list.findIndex(item => item.id === user.id);
      if (index > -1) list[index] = user;
    } else {
      // 添加新会员（生成唯一ID）
      const newCustomer = { ...user, id: Date.now() };
      list.push(newCustomer);
      user = newCustomer;
    }

    localStorage.setItem('customers', JSON.stringify(list));
    return { code: 200, msg: 'success', data: user };
  },

  // 删除会员
  deleteCustomers: async (ids: number[]): Promise<Result<boolean>> => {
    const saved = localStorage.getItem('customers') || '[]';
    let list: User[] = JSON.parse(saved);
    list = list.filter(item => !ids.includes(item.id));
    localStorage.setItem('customers', JSON.stringify(list));
    return { code: 200, msg: 'success', data: true };
  }
};

export const useUserStore = defineStore('user', () => {
  const isLogin = ref(false)
  const userInfo = ref<{ name?: string; email?: string }>({})

  const user = ref<User[]>([])
  const currentUser = ref<User | null>(null); // 当前操作的会员
  const selectedIds = ref<number[]>([]); // 选中的会员ID
  const isLoading = ref(false); // 加载状态

  function setLogin(status: boolean) {
    isLogin.value = status
  }

  function setUserInfo(info: { name?: string; email?: string }) {
    userInfo.value = info
  }

  function logout() {
    isLogin.value = false
    userInfo.value = {}
  }

  // 表单配置（系统默认字段）
  const formConfig = ref<FormConfig>({
    systemFields: [
      { key: 'name', label: '姓名', type: 'text', required: true, isSystem: true },
      { key: 'phone', label: '电话', type: 'text', required: true, isSystem: true },
      { key: 'address', label: '收件信息', type: 'text', required: false, isSystem: true },
      { key: 'idCard', label: '身份证号', type: 'text', required: false, isSystem: true },
      { key: 'birthday', label: '生日', type: 'date', required: false, isSystem: true },
      { key: 'height', label: '身高(cm)', type: 'number', required: false, isSystem: true },
      { key: 'weight', label: '体重(kg)', type: 'number', required: false, isSystem: true },
      { key: 'shoeSize', label: '鞋码', type: 'text', required: false, isSystem: true },
      { key: 'clothesSize', label: '服码', type: 'text', required: false, isSystem: true },
      {
        key: 'athleteLevel',
        label: '运动员等级',
        type: 'select',
        required: false,
        isSystem: true,
        options: [
          { label: '无', value: '' },
          { label: '一级', value: '1' },
          { label: '二级', value: '2' },
          { label: '三级', value: '3' }
        ]
      },
      {
        key: 'refereeLevel',
        label: '裁判员等级',
        type: 'select',
        required: false,
        isSystem: true,
        options: [
          { label: '无', value: '' },
          { label: '一级', value: '1' },
          { label: '二级', value: '2' },
          { label: '三级', value: '3' }
        ]
      },
      { key: 'familyHeritage', label: '家族传承', type: 'text', required: false, isSystem: true }
    ],
    customFields: [] // 自定义字段（后续可扩展）
  });

  const allFields = computed<CustomField[]>(() => [
    ...formConfig.value.systemFields,
    ...formConfig.value.customFields
  ]);

  const isAllSelected = computed({
    get: () =>
      user.value.length > 0 &&
      selectedIds.value.length === user.value.length,
    set: (value) => {
      selectedIds.value = value
        ? user.value.map(item => item.id)
        : [];
    }
  });
  // 加载会员列表
  const loadCustomers = async () => {
    isLoading.value = true;
    try {
      const response = await mockApi.getCustomers();
      if (response.code === 200 && response.data) {
        user.value = response.data;
      }
    } catch (error) {
      console.error('加载会员失败:', error);
    } finally {
      isLoading.value = false;
    }
  };
  // 设置当前操作的会员
  const setCurrentCustomer = (user: User | null) => {
    currentUser.value = user ? { ...user } : null;
  };
  // 保存会员（新增或更新）
  const saveCustomer = async (user: User) => {
    isLoading.value = true;
    try {
      const response = await mockApi.saveCustomer(user);
      if (response.code === 200) {
        // 刷新列表
        await loadCustomers();
        return true;
      }
    } catch (error) {
      console.error('保存会员失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  // 删除选中的会员
  const deleteSelected = async () => {
    if (selectedIds.value.length === 0) return false;

    isLoading.value = true;
    try {
      const response = await mockApi.deleteCustomers(selectedIds.value);
      if (response.code === 200) {
        selectedIds.value = [];
        await loadCustomers();
        return true;
      }
    } catch (error) {
      console.error('删除会员失败:', error);
    } finally {
      isLoading.value = false;
    }
    return false;
  };
  // 切换会员选择状态
  const toggleSelect = (id: number) => {
    const index = selectedIds.value.indexOf(id);
    if (index > -1) {
      selectedIds.value.splice(index, 1);
    } else {
      selectedIds.value.push(id);
    }
  };
  
  onMounted(() => {
    loadCustomers();
    
    // 从localStorage检查token并恢复登录状态
    const token = localStorage.getItem('token');
    if (token) {
      // 设置登录状态为已登录
      setLogin(true);
      // 重要：重新获取用户信息以恢复完整状态
      restoreUserInfo();
    }
  });

  // 新增方法：从服务器恢复用户信息
  const restoreUserInfo = async () => {
    try {
      const response = await UserApi.getCurrentUser();
      if (response.code === 200 && response.data) {
        setUserInfo({
          name: response.data.name,
          email: response.data.email || undefined
          // 其他需要的用户信息
        });
      }
    } catch (error) {
      console.error('恢复用户信息失败:', error);
      // 如果恢复失败，可以考虑清除token并跳转到登录页
      // logout();
      // localStorage.removeItem('token');
      // router.push('/login');
    }
  };


  return {
    isLogin, 
    userInfo, 
    user,
    currentUser,
    selectedIds,
    isAllSelected,
    isLoading,
    formConfig,
    allFields,
    setLogin, 
    setUserInfo, 
    logout,
    loadCustomers,
    setCurrentCustomer,
    saveCustomer,
    deleteSelected,
    toggleSelect
  }
})