<script setup>
import {Lock, User} from '@element-plus/icons-vue'
import {reactive} from "vue";
import {login} from "@/net";
import {ref} from 'vue'
import router from "@/router/index.js";

const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rule = {
  username: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ],
}

function userLogin() {
  formRef.value.validate((valid) => {
    if (valid) {
      login(form.username, form.password, form.remember, () => router.push('/index'))
    }
  })
}

</script>

<template>
  <div style="text-align: center; margin: 0 20px">
    <div>
      <div style="font-size: 25px;font-weight: bold">登录</div>
    </div>
    <div style="margin-top: 20px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名或邮箱">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox v-model="form.remember" label="记住我"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link>忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <el-row>
      <el-col>
        <el-row :gutter="20">
          <el-col :span="4"></el-col>
          <el-col :span="16">
            <div class="grid-content ep-bg-purple"/>
            <el-button @click="userLogin" style="width: 150px" type="primary">立即登录</el-button>
          </el-col>
          <el-col :span="4">
            <div class="grid-content ep-bg-purple"/>
            <el-link type="primary">注册</el-link>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>

.grid-content {
  border-radius: 4px;
  min-height: 8px;
}
</style>