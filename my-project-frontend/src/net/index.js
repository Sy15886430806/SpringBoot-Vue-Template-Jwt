// 引入axios库用于发送HTTP请求
import axios from "axios"
// 引入Element Plus库中的ElMessage组件用于显示消息提示
import {ElMessage} from "element-plus";

// 定义存储访问令牌的键名
const authItemName = 'access_token'

// 定义默认的请求失败处理函数
const defaultFailure = (message, code, url) => {
    // 在控制台输出警告信息，包括请求地址、状态码和错误信息
    console.warn(`请求地址:${url},状态码:${code},错误信息${message}`)
    // 使用ElMessage组件显示警告消息
    ElMessage.warning(message)
}

// 定义默认的错误处理函数
const defaultError = (error) => {
    // 在控制台输出警告信息，包括错误对象
    console.warn(error)
    // 使用ElMessage组件显示警告消息
    ElMessage.warning('发生了一些错误,请联系管理员')
}

// 存储访问令牌的函数
function storeAccessToken(token, remember, expire) {
    // 创建一个包含令牌和过期时间的对象
    const authObj = {
        token: token, expire: expire
    }
    // 将对象转换为字符串
    const str = JSON.stringify(authObj)
    // 根据remember参数决定将令牌存储在localStorage还是sessionStorage
    if (remember) localStorage.setItem(authItemName, str)
    else sessionStorage.setItem(authItemName, str)
}

// 获取访问令牌的函数
function takeAccessToken() {
    // 从localStorage或sessionStorage中获取存储的令牌字符串
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    // 如果没有获取到令牌字符串，返回null
    if (!str) return null;
    // 将字符串解析为对象
    const authObj = JSON.parse(str)
    // 检查令牌是否过期，如果过期则删除令牌并显示警告消息，返回null
    if (authObj.expire <= new Date()) {
        deleteAccessToken()
        ElMessage.warning('登录状态已过期,请重新登录')
        return null;
    }
    // 返回令牌
    return authObj.token
}

// 发送POST请求的内部函数
function internalPost(url, data, header, success, failure, error = defaultError) {
    // 使用axios发送POST请求
    axios.post(url, data, {
        headers: header
    }).then(({data}) => {
        // 如果响应代码为200，调用成功回调函数
        if (data.code === 200) {
            success(data.data)
        } else {
            // 否则调用失败回调函数
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err)) // 捕获请求错误并调用错误处理函数
}

// 删除访问令牌的函数
function deleteAccessToken() {
    // 从localStorage和sessionStorage中删除令牌
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

// 定义一个名为 accessHeader 的函数，用于获取请求头中的授权信息
function accessHeader() {
    // 调用 takeAccessToken 函数获取访问令牌
    const token = takeAccessToken();
    // 使用三元运算符判断 token 是否存在
    // 如果 token 存在，则返回一个包含 Authorization 字段的对象，值为 'Bearer ' 加上 token
    // 如果 token 不存在，则返回一个空对象
    return token ? {'Authorization': `Bearer ${token}`} : {}
}

// 发送GET请求的内部函数
function internalGet(url, header, success, failure, error = defaultError) {
    // 使用axios发送GET请求
    axios.get(url, {
        headers: header
    }).then(({data}) => {
        // 如果响应代码为200，调用成功回调函数
        if (data.code === 200) {
            success(data.data)
        } else {
            // 否则调用失败回调函数
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err)) // 捕获请求错误并调用错误处理函数
}

// 定义一个名为 get 的函数，用于发送 GET 请求
function get(url, success, failure = defaultFailure) {
    // 调用 internalGet 函数，传递 URL、访问头信息、成功回调函数和失败回调函数
    // 其中 failure 参数有一个默认值 defaultFailure，如果调用时没有提供 failure 参数，则使用 defaultFailure
    internalGet(url, accessHeader(), success, failure)
}

// 定义一个名为 post 的函数，用于发送 POST 请求
function post(url, data, success, failure = defaultFailure) {
    // 调用内部函数 internalPost 发送 POST 请求
    // 参数依次为：请求的 URL、请求的数据、访问头信息、成功回调函数、失败回调函数
    // 其中失败回调函数 failure 有一个默认值 defaultFailure
    internalPost(url, data, accessHeader(), success, failure)
}

// 定义一个登录函数，接收用户名、密码、是否记住我、成功回调函数和失败回调函数（默认为defaultFailure）
function login(username, password, remember, success, failure = defaultFailure) {
    // 调用internalPost函数发送POST请求到'/api/auth/login'接口
    internalPost('/api/auth/login', {
        // 请求体数据，包含用户名和密码
        username: username,
        password: password,
    }, {
        // 请求头设置，指定内容类型为'application/x-www-form-urlencoded'
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        // 请求成功时的回调函数
        // 调用storeAccessToken函数存储访问令牌，传入令牌、是否记住我、令牌过期时间
        storeAccessToken(data.token, remember, data.expire)
        // 使用ElMessage组件显示成功消息，包含用户名
        ElMessage.success(`登录成功,欢迎${username}来到一粟的个人网站`)
        // 调用成功回调函数，传入返回的数据
        success(data)
    }, failure) // 请求失败时的回调函数，使用默认的failure函数
}

function logout(success, failure = defaultFailure) {
    get('/api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success('退出登录成功')
        success()
    }, failure)
}

function unauthorized() {
    return !takeAccessToken()
}

export {login, logout, get, post, unauthorized}