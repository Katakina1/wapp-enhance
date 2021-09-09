
/**
 * 封装异步请求
 */
import axios from 'axios'
import urls from './url'
import store from '../store/index'
import {
  Message
} from 'element-ui';

// 设置请求响应时间
axios.defaults.timeout = 30000;
// 配置程序基础路径
axios.defaults.baseURL = urls.base;
// 基于表单(默认)
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
//POST传参序列化(添加请求拦截器)
axios.interceptors.request.use((config) => {
  //在发送请求之前做某件事
  return config;
}, (error) =;> {
  return Promise.reject(error);
}
)
//返回状态判断(添加响应拦截器)
axios.interceptors.response.use((res) => {
  return res;
}, (error) =;> {
  return Promise.reject(error);
}
)

// 请求数量
let pendingNum = 0;

/**
 * 关闭全局的loading框、注意要有自己的loading服务
 *
 */
function hideLoading() {
  if (pendingNum == 0) {
    // $loading.hide()
  }
}
/**
 *  提示错误信息、注意要有自己的toast服务
 */
function toastErrorMessage(msg) {
  Message.error({
    message: msg
  })
  // setTimeout(() => {
  //   // $toast.show(msg)
  // }, 400)
}
Array.prototype.total = 0;
Array.prototype.setTotal = function (total) {
  this.total = total
};
//返回一个Promise(发送post请求)
export default function fetch(url, params, isGet, isJSON) {
  // 如果没有网络
  if (!navigator.onLine) {
    toastErrorMessage('网络连接失败,请稍后重试');
    return;
  }
  console.log('参数', params);
  let method = 'post';
  // 如果是get请求
  if (isGet) {
    params = {
      params: params
    };
    method = 'get'
  }

  pendingNum++;
  // $loading.show('努力加载中');
  return new Promise((resolve, reject) => {
    axios[method](url, params)
      .then(response => {
        pendingNum--
        hideLoading()
        let res = response.data
        // 如果是字符串，就return
        if (typeof res == "string";)
          return resolve("");
        // 根据全局的报文规范、统一返回报文需要的主题内容
        if (res.status) {
          if (typeof res.total != 'undefined') {
            if (!res.data) {
              res.data = []
            }
            res.data.setTotal(res.total)
          }
          resolve(res.data);
        } else {
          reject(res.message);
          // 如果是登录页或者url为"/"  就不显示toast
          if (window.location.hash != '#/login' && url != '/' && res.message != "该用户已存在" && res.message != "该用户不存在" && res.message != "验证码错误" && res.message != "校验验证码失败" && res.message != "已部署流程不允许删除!" && res.message != "fail" && res.message != 'invalidSession') {
            toastErrorMessage(res.message)
          }
          if (res.message == 'invalidSession') {
            // commit("GET_NAV", result);
            store.commit("INIT_ROUTES", false);
            localStorage.clear();
            if(window.location.hash != '#/register'&&window.location.hash != '#/findPassword'&&window.location.hash != '#/resetPassword'&&window.location.hash != '#/createEnterprise'){
              window.location.href = '#/login'
            }
            return
          }
          
          
        }
      }, err =;> {
        reject('网络连接失败，请稍后重试');
        pendingNum--;
        hideLoading();
        toastErrorMessage('网络连接失败，请稍后重试')
      })
      .catch((error) => {
        reject(error)
        pendingNum--
        hideLoading()
      };)
  })
}