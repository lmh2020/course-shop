
const http = {
    TOKEN: null,
    USER: null,

    get: function (url, params, callback) {
        $.ajax({
            url: params ? url + "?" + this.queryParams(params) : url,
            type: "get",
            headers: {
                Authorization: this.getToken()
            },
            success: function (res){
                if (res.hasOwnProperty("code") && res.code !== 200){
                    return Swal.fire({title: '提示', text: res.msg, icon: 'warning'})
                }
                if (callback){
                    callback(res.hasOwnProperty("data") ? res.data : res)
                }
            }
        })
    },

    post: function (url, data, callback) {
        $.ajax({
            url: url,
            headers: {
                Authorization: this.getToken()
            },
            dataType: "json",
            contentType: "application/json",
            data: data ? JSON.stringify(data) : null,
            type: "post",
            success: function (res){
                if (res.hasOwnProperty("code") && res.code !== 200){
                    return Swal.fire({title: '提示', text: res.msg, icon: 'warning'})
                }
                if (callback){
                    callback(res.hasOwnProperty("data") ? res.data : res)
                }
            }
        })
    },

    put: function (url, data, callback) {
        $.ajax({
            url: url,
            headers: {
                Authorization: this.getToken()
            },
            dataType: "json",
            contentType: "application/json",
            data: data ? JSON.stringify(data) : null,
            type: "put",
            success: function (res){
                if (res.hasOwnProperty("code") && res.code !== 200){
                    return Swal.fire({title: '提示', text: res.msg, icon: 'warning'})
                }
                if (callback){
                    callback(res.hasOwnProperty("data") ? res.data : res)
                }
            }
        })
    },

    page: function (url, params, callback) {
        $.ajax({
            url: params ? url + "?" + this.queryParams(params) : url,
            type: "get",
            headers: {
                Authorization: this.getToken()
            },
            success: function (res){
                if (res.code !== 200){
                    return Swal.fire({title: '提示', text: res.msg, icon: 'warning'})
                }
                if (callback){
                    callback(res)
                }
            }
        })
    },

    go: function (path, params) {
        if (params){
            location.href = `/front/page?path=${path}&${this.queryParams(params)}&Authorization=${http.getToken()}`
        } else {
            location.href = `/front/page?path=${path}&Authorization=${http.getToken()}`
        }
    },
    //
    // load: function ($dom, path) {
    //     // location.href = `/front/load?path=${path}&Authorization=${http.getToken()}`
    //     $dom.load(`/front/load?path=${path}&Authorization=${http.getToken()}`)
    // },

    queryParams (data, isPrefix = false) {
        if (!data){
            return ''
        }
        let prefix = isPrefix ? '?' : ''
        let _result = []
        for (let key in data) {
            let value = data[key]
            // 去掉为空的参数
            if (['', undefined, null].includes(value)) {
                continue
            }
            if (value.constructor === Array) {
                value.forEach(_value => {
                    _result.push(encodeURIComponent(key) + '[]=' + encodeURIComponent(_value))
                })
            } else {
                _result.push(encodeURIComponent(key) + '=' + encodeURIComponent(value))
            }
        }

        return _result.length ? prefix + _result.join('&') : ''
    },

    getToken: function (){
        if (this.TOKEN){
            return this.TOKEN
        }
        const t = localStorage.getItem("TOKEN")
        return t ? t.toString() : null
    },

    refreshToken: function(token){
        this.TOKEN = token
        localStorage.setItem("TOKEN", token)
    },

    clearToken: function(){
        this.TOKEN = null
        localStorage.removeItem("TOKEN")
    },

    getUser: function(){
        if (this.TOKEN){
            return this.TOKEN
        }
        const t = localStorage.getItem("USER")
        return t ? JSON.parse(t.toString()) : null
    },

    refreshUser: function(user){
        this.USER = user
        localStorage.setItem("USER", JSON.stringify(user))
    },

    clearUser: function(){
        this.USER = null
        localStorage.removeItem("USER")
    },


}
